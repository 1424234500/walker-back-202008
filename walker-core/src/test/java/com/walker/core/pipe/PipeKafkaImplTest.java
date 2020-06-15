package com.walker.core.pipe;

import com.walker.common.util.ThreadUtil;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.core.aop.Fun;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PipeKafkaImplTest {
	int i = 0;

	@Test
	public void testStart() {
		Pipe<String> pipe = new PipeKafkaImpl();
        pipe.start("key");
		assertTrue(pipe.put("a"));
		assertTrue(pipe.putHead("2"));
		assertTrue(pipe.putHead("1"));
		assertTrue(pipe.put(Arrays.asList("b", "c")));
		new Thread(){
			public void run(){
				pipe.put("N" + i++);
				ThreadUtil.sleep(1000);
			}
		}.start();
		pipe.startConsumer(2, new Fun<String>() {
			@Override
			public <T> T make(String obj) {
				Tools.out(obj);
 				return null;
			}
		});
		ThreadUtil.sleep(30000);
		pipe.await(20999, TimeUnit.SECONDS);
		pipe.stopConsumer();
		pipe.stop();
	} 

}
