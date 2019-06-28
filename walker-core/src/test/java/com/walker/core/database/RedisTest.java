package com.walker.core.database;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.walker.common.util.Tools;
import com.walker.core.database.Redis.Fun;
import com.walker.core.mode.Watch;

import redis.clients.jedis.*;

public class RedisTest {

	@Test
	public void testDoJedis() {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
		Jedis  j = pool.getResource();
		assertNotNull(j);
		j.set("aaa", "bbb");
		assertEquals(j.get("aaa"), "bbb");
	}
	@Test
	public void testPipeline() {
		
		Redis.doJedis(new Fun<Object>() {

			@Override
			public Object make(Jedis jedis) {
				Watch w = new Watch("test pipe ");
				int count = 200;
				for(int i = 0; i < count; i++) {
					jedis.set("test:pipe:" + i, "value" + i);
				}
				w.cost("set " + count);
				Pipeline pipe = jedis.pipelined();
				int pc = 200;
				for(int i = 0; i < pc; i++) {
					pipe.set("test:pipe:set:"+i, "pc:"+i);
				}
				w.cost("pipeSet " + pc);
				for(int i = 0; i < count; i++) {
					pipe.get("test:pipe:" + i);
				}
				w.cost("pipeGet " + count);
				pipe.del("test:pipe*");

				List<Object> res = pipe.syncAndReturnAll();
				Tools.formatOut(res);
				Tools.out(w.res());
				
				return null;
			}
		});
		
		
	}
	
	
	

}
