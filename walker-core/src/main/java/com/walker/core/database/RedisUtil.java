package com.walker.core.database;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.walker.common.util.LangUtil;
import com.walker.common.util.SerializeUtil;
import com.walker.common.util.Tools;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisException;

/**
 * jedis设置存取 抽离
 * @author walker
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public  class RedisUtil   { 
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



	public static String lockWithTimeout(Jedis jedis, String lockName, long acquireTimeout, long timeout) {
		String key_identifier = null;
		try {
			String identifier = Thread.currentThread().getName() + ":" + LangUtil.getUUID(); // Thread
			// just
			// for
			// debug
			String lockKey = "lock:" + lockName;
			int lockExpire = (int) (timeout / 1000);
			long end = System.currentTimeMillis() + acquireTimeout;
			while (System.currentTimeMillis() < end) {
				if (jedis.setnx(lockKey, identifier) == 1) {// execute
					// successfully will
					// return "1"
					jedis.expire(lockKey, lockExpire);
					key_identifier = identifier;
					System.out.println(Thread.currentThread().getName() + "  获取锁:"+key_identifier);
					return key_identifier;
				}
				if (jedis.ttl(lockKey) == -1) {
					jedis.expire(lockKey, lockExpire);
				}
				long lockKey_ttl = jedis.ttl(lockKey);
				try {
					out(Thread.currentThread().getName()
							+ "  lock_withTimeout获取锁竞争失败，休息1秒，继续尝试获取,锁ttl剩余：" + lockKey_ttl);

					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			out(Thread.currentThread().getName() + "  获取redis连接失败，放弃获取锁");
		} catch (Exception e) {
			out(Thread.currentThread().getName() + "  获取锁发生异常");
			e.printStackTrace();
		} finally {
		}
		return key_identifier;
	}

	public static boolean lockRelease(Jedis jedis, String lockName, String identifier) {
		String lockKey = "lock:" + lockName;
		boolean retFlag = false;
		String _temp_identifier_from_redis = "";
		try {
			while (true) {
				jedis.watch(lockKey);
				_temp_identifier_from_redis = jedis.get(lockKey);
				if (_temp_identifier_from_redis == null || "".equals(_temp_identifier_from_redis)) {
					out(Thread.currentThread().getName() + "  锁已过期失效失效");
				} else if (identifier.equals(_temp_identifier_from_redis)) {
					long del_result = jedis.del(lockKey);
					if (del_result == 1) {
						out(Thread.currentThread().getName() + "  完成任务，释放锁");
						retFlag = true;
					} else {
						out(Thread.currentThread().getName() + "  释放锁失败，锁已提前释放");
						//continue;
					}
				} else {
					out(Thread.currentThread().getName() + "  锁已过期失效，被污染");
				}
				jedis.unwatch();
				break;
			}
		} catch (JedisException e) {
			e.printStackTrace();
		} finally {

		}
		return retFlag;
	}



	public static  void out(Object...objs){
		Tools.out(objs);
	}
}	
