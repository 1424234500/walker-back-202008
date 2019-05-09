package com.walker.core.pipe;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import com.walker.core.aop.Fun;

public class PipeRedisImplTest {

	@Test
	public void testStart() {
		Pipe<String> pipe = new PipeRedisImpl();
		pipe.start("key");
		
		assertTrue(pipe.put("a"));
		assertEquals("a", pipe.get());
		assertTrue(pipe.putHead("2"));
		assertTrue(pipe.putHead("1"));
		assertEquals("1", pipe.get());
		assertEquals("2", pipe.get());
		
		assertTrue(pipe.put(Arrays.asList("b", "c")));
		pipe.startConsumer(2, new Fun<String>() {
			@Override
			public <T> T make(String obj) {
				assertTrue("b".equals(obj) || "c".equals(obj)) ;
				return null;
			}
		});
		
		pipe.stopConsumer();
		pipe.stop();
	} 

}
