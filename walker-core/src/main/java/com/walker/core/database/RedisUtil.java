package com.walker.core.database;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.walker.common.util.*;

import com.walker.core.aop.FunArgsReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.SetParams;

/**
 * jedis设置存取 抽离
 * @author walker
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public  class RedisUtil   {
	private static final String KEY_LOCK_GET_CACHE_OR_DB = "lock:get:cache:or:db:";
	private static final String KEY_LOCK = "lock:make:";
	private static Logger log = LoggerFactory.getLogger(RedisUtil.class);



	/**
	 * Redis数据结构 ---- 数据库结构[ id:01, name: walker, age: 18 ]
	 * set get key - value
	 * 1.	id:01 - {name:walker, age:18}
	 * 2.	id:01:name - walker
	 * 2.	id:01:age  - 18
	 * 3.	id:01 -  map{
	 * 						id:01,
	 * 						name:walker,
	 * 						age:18
	 * 					} 
	 */
	
	
	/**
	 * 处理list采用rpush结构 否则 全使用序列化 string byte[]
	 * @param jedis
	 * @param key
	 * @param value
	 * @param expire
	 */
	public static <V> void put(Jedis jedis, String key, V value, long expire) {
		if(value instanceof List ){
			List list = (List)value;
			for(Object item : list){
				jedis.rpush(key.getBytes(), SerializeUtil.serialize(item));
			}
		}else{
//			jedis.set(key, value.toString());
			jedis.set(key.getBytes(), SerializeUtil.serialize(value));
		}
		//后置设定过期时间
		jedis.expire(key, (int)Math.ceil(expire / 1000));

	}
	/**
	 * 处理list采用rpush结构 否则 全使用序列化 string byte[]
	 * @param jedis
	 * @param key
	 * @return
	 */
	public static <V> V get(Jedis jedis, String key){
		return get(jedis, key, null);
	}


	/*
	首先，先说一下。老外提出了一个缓存更新套路，名为《Cache-Aside pattern》。其中就指出
　　失效：应用程序先从cache取数据，没有得到，则从数据库中取数据，成功后，放到缓存中。
　　命中：应用程序从cache中取数据，取到后返回。
　　更新：先把数据存到数据库中，成功后，再让缓存失效。
	*/
	/**
	 * 缓存获取，穿透实现
	 * 分布式锁 粒度小 避免大量同key数据库访问
	 * 如何解决缓存一致性问题？
	 * @param key
	 * @param millisecondsToExpire	当查询db成功时 设置缓存过期时间
	 * @param millisecondsToWait	当查询db时锁等待时间 避免穿透
	 * @param getFromDb
	 * @return
	 */
	public static String getCacheOrDb(String key, long millisecondsToExpire, long millisecondsToWait, FunArgsReturn<String, String> getFromDb){
		return Redis.doJedis(new Redis.Fun<String>(){
			@Override
			public String make(Jedis jedis) {
				String res = RedisUtil.get(jedis, key, null);
				if(res == null){
//					加锁 避免缓存击穿 锁粒度最小程度 key
					String lockName = RedisUtil.KEY_LOCK_GET_CACHE_OR_DB + key;
					String lock = RedisUtil.tryLock(jedis, lockName, millisecondsToExpire, millisecondsToWait);
					if (lock.length() > 0) {
						try {
							res = RedisUtil.get(jedis, key, null);    //再次获取缓存
							if (res == null) {
								res = getFromDb.make(key);
								if(res == null){	//避免缓存穿透  null是否缓存 快速过期来保护数据库  本来计划10分钟过期 则 null1分钟过期 最小5秒过期
//									布隆过滤器预热 性能实现 全局map 精确映射数据库有没有
//									RedisUtil.put(jedis, key, res, Math.max(millisecondsToExpire/10, 5000));
								}else{
									RedisUtil.put(jedis, key, res, millisecondsToExpire);
								}
								log.debug("get from db " + key + " res is null ? " + (res == null) );
							}
						}finally {
							RedisUtil.releaseLock(jedis, lockName, lock);
						}
					}else{
						log.debug("cache funArgsReturn lock no: " + key);
					}
				}else{
					log.debug("get from cache " + key);
				}
				return res;
			}
		});
	}

	/**
	 * 先更新数据库，再删除缓存
	 * 	　　先删除缓存，再更新数据库
	 * 			功能问题：请求A和请求B进行操作 A删缓存 B查出又设置了旧缓存 导致脏旧
	 * 				解决方案：延迟双删， 删除缓存，更新数据库之后，延迟一段时间再次删除缓存（若失败? 重试几次报警记录日志=人工介入？）
	 * 					mysql主从读写分离：未主从同步时 依然有问题 延时间隔
	 *
	 * @param key
	 * @param millisecondsToWait 延时等待 数据库主从同步时间 或 并发时间
	 * @param setToDb
	 * @return
	 */
	public static String setDbAndClearCache(String key, long millisecondsToWait, FunArgsReturn<String, String> setToDb){
		String res = "";
		if(setToDb != null){
			res = setToDb.make(key);	//存入数据库后，等待主从同步到从查询后再次删除，等待避免多线程查询了旧的存入了缓存 脏旧

			try {
				Redis.doJedis(new Redis.Fun<String>() {
					@Override
					public String make(Jedis jedis) {
						log.debug(" 1 del cache key:" + key + "size:" + RedisUtil.del(jedis, key));
						return "";
					}
				});
			}catch (Exception e){
				log.error(key + " " + e.getMessage(), e);
			}

//			延迟执行 延迟队列？
			ThreadUtil.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						Redis.doJedis(new Redis.Fun<String>() {
							@Override
							public String make(Jedis jedis) {
								RedisUtil.del(jedis, key);
								log.debug(" 2 del cache key:" + key + "size:" + RedisUtil.del(jedis, key));
								return "";
							}
						});
					}catch (Exception e){
						log.error(key + " " + e.getMessage(), e);
					}
				}
			}, millisecondsToWait, TimeUnit.MILLISECONDS);


		}else{
			log.warn("no set to db ? " + key);
		}
		return res;
	}


	/**
	 * 订阅
	 * @param jedis
	 * @param jedisPubSub
	 * @param channels
	 */
	public static void subscribe(Jedis jedis, JedisPubSub jedisPubSub, String channels) {
		jedis.subscribe(jedisPubSub, channels);
	}
	/**
	 * 发布
	 * @param jedis
	 * @param channel
	 * @param message
	 */
	public static Long publish(Jedis jedis, String channel, String message) {
		return jedis.publish(channel, message);
	}
	

	/**
	 * Redis数据结构 ---- 数据库结构[ id:01, name: walker, age: 18 ]
	 * set get key - value
	 * 1.	id:01 - {name:walker, age:18}
	 * 2.	id:01:name - walker
	 * 2.	id:01:age  - 18
	 * 3.	id:01 -  map{
	 * 						id:01,
	 * 						name:walker,
	 * 						age:18
	 * 					} 
	 */
	
	
	/**
	 * 添加一个指定key/id的map
	 */
	public static  String setMap(Jedis jedis, String key, Map<String, String> map){
		String res = jedis.hmset(key, map);
		return res;
	}
	/**
	 * 移除一个指定key 
	 */
	public static long del(Jedis jedis, String key){
		long res = jedis.del(key);
		return res;
	}
	/**
	 * 获取指定key的map 
	 */
	public static  Map<String, String> getMap(Jedis jedis, String key){
		Map<String, String> res = jedis.hgetAll(key);
		return res;
	}
	/**
	* 添加一个list 通过byte 序列化 lpush头插入 rpush尾插入
	*/
	public static  long  listRPush(Jedis jedis, String keyName, Collection<? extends String> c){ 
		int res = 0;
		for(String item : c){
			res += jedis.rpush(keyName, item);
		}
		return res;
	}
	/**
	* 添加一个list 通过byte 序列化 lpush头插入 rpush尾插入
	*/
	public static  long listLPush(Jedis jedis, String keyName, Collection<? extends String> c){ 
		int res = 0;
		for(String item : c){
			res += jedis.lpush(keyName, item.toString());
		}
		return res;
	}
	/**
	* 添加 通过byte 序列化 lpush头插入 rpush尾插入
	*/
	public static  long listRpush(Jedis jedis, String keyName, String obj){ 
		return jedis.rpush(keyName, obj);

	}
	/**
	* 添加 通过byte 序列化 lpush头插入 rpush尾插入
	*/
	public static  long listLpush(Jedis jedis, String keyName, String obj){ 
		return jedis.lpush(keyName, obj);
	}
	 
	public static  String lpop(Jedis jedis, String key){

		String res = jedis.lpop(key);

		return res;
	}
	/**
	 * 获取对象 自动解析键值类型
	 */
	public static <V> V get(Jedis jedis, String key, V defaultValue){
		Object res = defaultValue;
		if(jedis.exists(key)){
			String type = jedis.type(key);
			if(type.equals("string")){
				res = jedis.get(key);
			}else if(type.equals("list")){
				res = jedis.lrange(key, 0, -1);
			}else if(type.equals("hash")){
				res = (jedis.hgetAll(key));
			}else if(type.equals("set")){
				res = (jedis.smembers(key));
			}else if(type.equals("zset")){
				res = (jedis.zrange(key, 0, -1)); 
			}else{
				res = jedis.get(key); 
			}
		}
		return (V) res;
	}
	public static  boolean exists(Jedis jedis, String key){

		boolean res = jedis.exists(key);

		return res;
	}
	public static  long size(Jedis jedis, String key) {

		long res = 0;
		if(jedis.type(key).equals("list")) {
			res = jedis.llen(key);
		}else {
			res = jedis.hlen(key);
		}

		return res;
	}
	
	/**
	 * 显示redis所有数据
	 */
	public static  void show(Jedis jedis){
		out("-----------Redis show-----------------");

		//获取所有key 各种类型
		Set<String> set = jedis.keys("*");
		for(String key : set){
			String type = jedis.type(key);
			out("key:" + key + ", type:" + type + "  ");
			if(type.equals("string")){
				out(jedis.get(key));
			}else if(type.equals("list")){
				out(jedis.lrange(key, 0, -1));
			}else if(type.equals("hash")){
				out(jedis.hgetAll(key));
			}else if(type.equals("set")){
				out(jedis.smembers(key));
			}else if(type.equals("zset")){
				out(jedis.zrange(key, 0, -1)); 
			}
			out("#############");
		}

		out("--------------------------------------");
	}
	
	public static  void showHash(Jedis jedis){
		out("-----------Redis showHash-----------------");

		//获取所有key 各种类型
		Set<String> set = jedis.keys("*"); 
		for(String key : set){
			String type = jedis.type(key);
			//out("key:" + key + ", type:" + type + "  "); 
			if(type.equals("hash")){	
				out(key, jedis.hgetAll(key));
			}  
		} 

		out("--------------------------------------");
	}

	public static String tryLock(String lockName, long millisecondsToExpire, long millisecondsToWait) {
		return Redis.doJedis(new Redis.Fun<String>() {
			@Override
			public String make(Jedis jedis) {
				return tryLock(jedis, lockName, millisecondsToExpire, millisecondsToWait);
			}
		});
	}
	/**
	 * 尝试获取分布式锁
	 * @param jedis Redis客户端
	 * @param lockName 锁
	 * @param millisecondsToExpire	超期时间ms
	 * @param millisecondsToWait	等待锁时间ms
	 * @return identifier 获取成功具体解锁回执， 失败为空
	 */
	public static String tryLock(Jedis jedis, String lockName, long millisecondsToExpire, long millisecondsToWait) {
		String lockKey =  KEY_LOCK + lockName;
		String value = Thread.currentThread().getName() + ":" + LangUtil.getTimeSeqId(); // Thread
		long startTime = System.currentTimeMillis();
		String result = "";
		int cc = 0;
		while(cc++ < 1000) {	//自循环等待 硬限制最多1000次
			try {
				result = jedis.set(lockKey, value, new SetParams().nx().px(millisecondsToExpire));
//		String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
				if (String.valueOf(result).toUpperCase().contains("OK")) {
					log.debug(Tools.objects2string( "tryLock ok", cc, lockKey, lockName,value, result, millisecondsToExpire, millisecondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime)));
					return value;
				}
			} catch (Exception e) {
				log.error(Tools.objects2string("tryLock exception", cc, lockKey, lockName,value, result, millisecondsToExpire, millisecondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime)), e);
			}
			if(System.currentTimeMillis() > startTime + millisecondsToWait){
				log.warn(Tools.objects2string("tryLock error wait timeout", cc, lockKey, lockName,value, result, millisecondsToExpire, millisecondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime)) );
				break;
			}
			try {//1000ms等待锁，共轮询10次
				Thread.sleep(Math.max(millisecondsToWait/4, 20));
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
		return "";
	}
	public static Boolean releaseLock(String lockName, String identifier) {
		return Redis.doJedis(new Redis.Fun<Boolean>() {
			@Override
			public Boolean make(Jedis jedis) {
				return releaseLock(jedis, lockName, identifier);
			}
		});
	}
	/**
	 * 释放分布式锁
	 * @param jedis Redis客户端
	 * @param lockName 锁
	 * @param identifier 识别码
	 * @return 是否释放成功
	 */
	public static boolean releaseLock(Jedis jedis, String lockName, String identifier) {
		String lockKey = KEY_LOCK + lockName;

		try {
			String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
			Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(identifier));

			if (String.valueOf(result).toUpperCase().contains("OK")) {
				log.debug(Tools.objects2string("release lock ok", lockKey, lockName, identifier));
				return true;
			}else{
				log.warn(Tools.objects2string("release lock error", lockKey, lockName, identifier, result));
			}
		}catch (Exception e){
			log.error(Tools.objects2string("release lock exception", lockKey, lockName, identifier, e.getMessage()), e);
		}
		return false;

	}



	public static  void out(Object...objs){
		Tools.out(objs);
	}
}	
