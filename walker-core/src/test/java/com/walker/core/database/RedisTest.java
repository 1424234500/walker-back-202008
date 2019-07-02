package com.walker.core.database;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.walker.common.util.Tools;
import com.walker.core.database.Redis.Fun;
import com.walker.core.mode.Watch;

import redis.clients.jedis.*;

public class RedisTest {
	static{
		RedisCluster.getInstance().test();
		Redis.getInstance().test();;
		
	}
	int maxcount = 1000;
	static int count = 10;
	@Test
	public void test() {
		RedisTest rt = new RedisTest();
		for(int i = 10; i<=maxcount; i*=10) {
			count = i;
			Tools.out(count);
			
			try {
				rt.testRedis1();
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				rt.testRedis2();
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				rt.testPipeline();
			}catch(Exception e) {
				e.printStackTrace();
			}
			try {
				rt.testCluster();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void testPipeline() {
		
		Redis.doJedis(new Fun<Object>() {

			@Override
			public Object make(Jedis jediss) {

				final String t = "pipeli";
				final String key = "test:" + t;
				Watch w = new Watch("test \t" + key + " \t" + count);
				Pipeline jedis = jediss.pipelined();

				jedis.del(key + "*");
				for(int i = 0; i < count; i++) {
					jedis.set(key + ":" + i, "value" + i);
				}
				jedis.syncAndReturnAll();
				w.cost("set\t");
				for(int i = 0; i < count; i++) {
					outg(jedis.get(key + ":" + i));
				}
				List<Object> res = jedis.syncAndReturnAll();
				formatOut(res);
				w.cost("get\t");
				
				jedis.del(key + "*");
				for(int i = 0; i < count; i++) {
					jedis.zadd(key, System.currentTimeMillis(), "value" + i);
				}
				jedis.syncAndReturnAll();
				w.cost("zadd\t");
				for(int i = 0; i < count; i++) {
					int start = i;
					outg(jedis.zrange(key, start, start));
				}
				res = jedis.syncAndReturnAll();
				formatOut(res);
				w.cost("zran\t");
				Tools.out(w.res());
				return null;
			}
		});
	}

	
	public void testRedis1() {
		final String t = "redis1";
		final String key = "test:" + t;
		Redis.doJedis(new Redis.Fun<Object>() {

			@Override
			public Object make(Jedis jedis) {
				Watch w = new Watch("test \t" + key + " \t" + count);
				
				jedis.del(key + "*");
				for(int i = 0; i < count; i++) {
					jedis.set(key + ":" + i, "value" + i);
				}
				w.cost("set\t");
				for(int i = 0; i < count; i++) {
					outg(jedis.get(key + ":" + i));
				}
				w.cost("get\t");
				
				jedis.del(key + "*");
				for(int i = 0; i < count; i++) {
					jedis.zadd(key, System.currentTimeMillis(), "value" + i);
				}
				w.cost("zadd\t");
				for(int i = 0; i < count; i++) {
					int start = i;
					outg(jedis.zrange(key, start, start));
				}
				w.cost("zran\t");
				Tools.out(w.res());
				
				return null;
			}
		});
	}
	
	public void testRedis2() {
		final String t = "redis2";
		final String key = "test:" + t;
		Redis redis = Redis.getInstance();
		Jedis jedis = null;
		
		Watch w = new Watch("test \t" + key + " \t" + count);
		try {
			jedis = redis.getJedis();
			jedis.del(key + "*");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			redis.close(jedis);
		}
		for(int i = 0; i < count; i++) {
			try {
				jedis = redis.getJedis();
				jedis.set(key + ":" + i, "value" + i);
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				redis.close(jedis);
			}
		}
		w.cost("set\t");
		for(int i = 0; i < count; i++) {
			try {
				jedis = redis.getJedis();
				outg(jedis.get(key + ":" + i));
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				redis.close(jedis);
			}
		}
		w.cost("get\t");
		try {
			jedis = redis.getJedis();
			jedis.del(key + "*");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			redis.close(jedis);
		}
		for(int i = 0; i < count; i++)  {
			try {
				jedis = redis.getJedis();
				jedis.zadd(key, System.currentTimeMillis(), "value" + i);
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				redis.close(jedis);
			}
		}
		w.cost("zadd\t");
		for(int i = 0; i < count; i++) {
			try {
				jedis = redis.getJedis();
				int start = i;
				outg(jedis.zrange(key, start, start));
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				redis.close(jedis);
			}
		}
		w.cost("zran\t");
		Tools.out(w.res());
				
	}

	
	public void testCluster() {
		final String t = "cluste";
		final String key = "test:" + t;
		RedisCluster.doJedis(new RedisCluster.Fun<Object>() {

			@Override
			public Object make(JedisCluster jedis) {
				Watch w = new Watch("test \t" + key + " \t" + count);
				
				jedis.del(key + "*");
				for(int i = 0; i < count; i++) {
					jedis.set(key + ":" + i, "value" + i);
				}
				w.cost("set\t");
				for(int i = 0; i < count; i++) {
					outg(jedis.get(key + ":" + i));
				}
				w.cost("get\t");
				
				jedis.del(key + "*");
				for(int i = 0; i < count; i++) {
					jedis.zadd(key, System.currentTimeMillis(), "value" + i);
				}
				w.cost("zadd\t");
				for(int i = 0; i < count; i++) {
					int start = i;
					outg(jedis.zrange(key, start, start));
				}
				w.cost("zran\t");
				Tools.out(w.res());
				
				return null;
			}
		});
	}
	
	public static void outg(Object...objects) {
//		Tools.out(objects);
	}
	public  static <T> void formatOut(Collection<T> obj) {
//		Tools.formatOut(obj);
	}

}
