package com.walker.core.database;

import java.util.*;

import org.apache.log4j.Logger;

import com.walker.core.cache.*;

import redis.clients.jedis.*;

/**
 * redis数据库
 * 连接池管理
 * 提供连接 销毁连接
 * 单例
 * @author walker
 *
 */
public class Redis  { 
	protected static Logger log = Logger.getLogger(Redis.class); 
	String host;
	JedisPoolConfig config;
	/**
	 * 连接数计数器
	 */
	int cc = 0;
	/**
	 * 查询次数计数器
	 */
	int get = 0;
	/**
	 * 关闭计数器
	 */
	int close = 0;
	/**
	 * 保持不断掉的jedis
	 */
	Map<String, Jedis> mapJedisLong;
	/**
	 * 连接池
	 */
	JedisPool pool;
	
	/**
	 * 回调环绕执行redis 操作
	 */
	public static <T> T doJedis(Fun<T> fun){
		T res = null;
		if(fun != null) {
			Jedis jedis = Redis.getInstance().getJedis();
			try {
				res = fun.make(jedis);
			}finally {
				Redis.getInstance().close(jedis);
			}
		} else {
			log.error("fun is null");
		}
		return res;
	}
	/**
	 * 不销毁的 独用连接
	 * @param key
	 */
	public Jedis getJedis(String key) {
		Jedis res = mapJedisLong.get(key);
		if(res == null) {
			res = getJedis();
			mapJedisLong.put(key, res);
		}
		return res;
	}
	/**
	 * 获取jedis 必须关闭close
	 * @return
	 */
	public Jedis getJedis(){
		get++;
		return pool.getResource();
	}
	public void close(String key) {
		Jedis res = mapJedisLong.get(key);
		close(res);
		mapJedisLong.remove(key);
	}
	
	public void close(Jedis jedis){
		if(jedis != null){
			jedis.close();
			get--;
		}
	}
	/**
	 * 从缓存获取并初始化
	 */
	private void init(){ 
		Cache<String> cache = CacheMgr.getInstance();
		
		config = new JedisPoolConfig();
        // 设置最大连接数
		config.setMaxTotal(cache.get("redis_maxTotal", 10));
		//设置最大等待时间
		config.setMaxWaitMillis(cache.get("redis_maxWaitMillis", 1000)); 
        // 设置空闲连接
        config.setMaxIdle(cache.get("redis_maxIdle", 3));
        host = cache.get("redis_host", "localhost");
        
		pool = new JedisPool(config, host);
 
		mapJedisLong = new HashMap<>();
		log.info("redis init ----------------------- " + cc++);
		log.info(this.toString());
		if(cc > 1) {
			log.error("----------------------------");
			log.error("---------单例失败？？？？？------------");
			log.error("----------------------------");
		}
	}
	
	private Redis() {
		init();
	}
	
	public static  Redis getInstance() {
		return SingletonFactory.instance;
	}
	//单例
	private static class SingletonFactory{
		static Redis instance ;
		static {
			log.warn("singleton instance construct " + SingletonFactory.class);
			instance = new Redis();
		}
	}
	public String toString() {
		return "host:" + host + " long:" + mapJedisLong.size() + " get:" + get + " cc:" + cc + " config:" + config;
	}
	
	/**
	 * jedis获取 执行后 自动关闭
	 */
	public interface Fun<T>{
		public T make(Jedis jedis) ;
	} 
}	
