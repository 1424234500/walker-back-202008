package com.walker.core.database;

import static org.junit.Assert.*;

import org.junit.Test;

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

}
