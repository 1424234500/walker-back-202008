package com.walker.common.mode;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.walker.common.util.ThreadUtil;
import com.walker.common.util.Tools;
import com.walker.common.util.Watch;

public class WatchTest {
	private static Logger log = Logger.getLogger(WatchTest.class); 

 
	@Test
	public void testWatch2() {
		Watch w = new Watch("query");
		ThreadUtil.sleep(1000);
		w.cost();
		ThreadUtil.sleep(1000);
		try {
			w.exceptionWithThrow(new Exception("aaaaaaaaaaa"), log);
		}catch(Exception e) {
			
		}
		Tools.out(w);
	}
	@Test
	public void testWatch3() {
		Watch w = new Watch("query");
		ThreadUtil.sleep(1000);
		w.cost("sql");
		ThreadUtil.sleep(200);
		w.cost("parse");
		ThreadUtil.sleep(10);
		w.cost("other");
		ThreadUtil.sleep(50);
		w.cost("return");

		w.res(5, log);
		Tools.out(w);
		Tools.out(w.toPrettyString());
	}
	
	
}
