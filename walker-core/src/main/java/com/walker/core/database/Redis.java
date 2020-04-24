package com.walker.core.database;


import com.walker.core.aop.TestAdapter;
import com.walker.core.cache.Cache;
import com.walker.core.cache.CacheMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis数据库
 * 连接池管理
 * 提供连接 销毁连接
 * 单例
 * @author walker
 *
 */
public class Redis  extends TestAdapter{ 
	protected static Logger log = LoggerFactory.getLogger(Redis.class);
	String host;
	int port;
	String password;
	int timeout;
	int database;
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
//	/**
//	 * 保持不断掉的jedis
//	 */
//	Map<String, Jedis> mapJedisLong;
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
			}catch (Exception e){
				log.debug(e.getMessage(), e);
				throw e;
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
//		Jedis res = mapJedisLong.get(key);
//		if(res == null) {
//			res = getJedis();
//			mapJedisLong.put(key, res);
//		}
//		return res;
		return getJedis();
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
//		Jedis res = mapJedisLong.get(key);
//		close(res);
//		mapJedisLong.remove(key);
	}
	
	public void close(Jedis jedis){
		if(jedis != null){
			jedis.close();
			get--;
		}
	}
	/**
	 * 从缓存获取并初始化
	 * @return 
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

		config.setTestOnBorrow(false);
		config.setTestOnReturn(false);
		config.setTestOnCreate(true);
		config.setBlockWhenExhausted(true);

		host = cache.get("redis_host", "localhost");
		port = cache.get("redis_port", 6379);
		timeout = cache.get("redis_timeout", 10000);
		password = cache.get("redis_password", "");
		database = cache.get("redis_database", 0);

//  public JedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port, int timeout, final String password, final int database) {
		pool = new JedisPool(config, host, port, timeout, password, database);
		log.info("redis init ----------------------- " + cc++);
		test();
		log.info(this.toString());
		if(cc > 1) {
			log.error("----------------------------");
			log.error("---------单例失败？？？？？------------");
			log.error("----------------------------");
		}
		return true;
	}
	
	@Override
	public boolean doUninit() {
		log.warn("uninit");
		return super.doUninit();
	}
	@Override
	public boolean doTest() {
		Jedis jedis = getJedis();
		try {
			jedis.set("test:redis", "1");
			return jedis.get("test:redis").equals("1");
		}finally {
			close(jedis);
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
		return "host:" + host + " get:" + get + " cc:" + cc + " config:" + config;
	}
	
	/**
	 * jedis获取 执行后 自动关闭
	 */
	public interface Fun<T>{
		public T make(Jedis jedis) ;
	} 
}	
