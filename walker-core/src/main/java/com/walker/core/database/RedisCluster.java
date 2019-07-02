package com.walker.core.database;

import java.util.*;

import org.apache.log4j.Logger;

import com.walker.core.aop.TestAdapter;
import com.walker.core.cache.*;

import redis.clients.jedis.*;

/**
 * redis 集群模式
 * 连接池管理
 * 提供连接 销毁连接
 * 单例
 * @author walker
 *
 */
public class RedisCluster  extends TestAdapter{ 
	protected static Logger log = Logger.getLogger(RedisCluster.class); 
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
	 * 连接池
	 */
//	JedisPool pool;
	JedisCluster jedisCluster; 
	
	/**
	 * 回调环绕执行redis 操作
	 */
	public static <T> T doJedis(Fun<T> fun){
		T res = null;
		if(fun != null) {
			try {
				RedisCluster rc = RedisCluster.getInstance();
				JedisCluster jc = rc.getJedisCluster();
				res = fun.make(jc);
			}finally {
			}
		} else {
			log.error("fun is null");
		}
		return res;
	} 
	public JedisCluster getJedisCluster() {
		return this.jedisCluster;
	}
	/**
	 * 从缓存获取并初始化
	 */
	public boolean doInit(){ 
		Cache<String> cache = CacheMgr.getInstance();
		
		config = new JedisPoolConfig();
        // 设置最大连接数
		config.setMaxTotal(cache.get("redis_maxTotal", 10));
		//设置最大等待时间
		config.setMaxWaitMillis(cache.get("redis_maxWaitMillis", 1000)); 
        // 设置空闲连接
        config.setMaxIdle(cache.get("redis_maxIdle", 3));
//        host = cache.get("redis_host", "localhost");
        host = cache.get("redis_host_cluster", "localhost:7000,localhost:7001,localhost:7002,localhost:7003,localhost:7004,localhost:7005");
        String nodes[] = host.split(",");
        
		//集群结点
		Set<HostAndPort> jedisClusterNode = new HashSet<HostAndPort>();
		for(int i = 0; i < nodes.length; i++) {
			String node = nodes[i];
			String ipPort[] = node.split(":");
			log.warn("add cluster \t" + i + "\t " + node);
			jedisClusterNode.add(new HostAndPort(ipPort[0], Integer.valueOf(ipPort[1])));
		}
		jedisCluster = new JedisCluster(jedisClusterNode, config);
		
		log.info("redis init ----------------------- " + cc++);
		log.info(this.toString());
		if(cc > 1) {
			log.error("----------------------------");
			log.error("---------单例失败？？？？？------------");
			log.error("----------------------------");
		}
		test();
		return true;
	}
	
	@Override
	public boolean doTest() {
		jedisCluster.set("test:cluster","1");
		return jedisCluster.get("test:cluster").equals("1");
	}

	@Override
	public boolean doUninit() {
		return super.doUninit();
	}

	private RedisCluster() {
		init();
	}
	
	public static  RedisCluster getInstance() {
		return SingletonFactory.instance;
	}
	//单例
	private static class SingletonFactory{
		static RedisCluster instance ;
		static {
			log.warn("singleton instance construct " + SingletonFactory.class);
			instance = new RedisCluster();
		}
	}
	public String toString() {
		return "host:" + host + " get:" + get + " cc:" + cc + " config:" + config + " cluster:" + String.valueOf(jedisCluster);
	}
	
	/**
	 * jedis获取 执行后 自动关闭
	 */
	public interface Fun<T>{
		public T make(JedisCluster jedis) ;
	} 
}	
