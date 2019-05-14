package com.walker.common.mode;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.walker.common.util.ThreadUtil;
import com.walker.common.util.Tools;
import com.walker.core.database.Dao;

public class WatchTest {
	private static Logger log = Logger.getLogger(WatchTest.class); 

 
	@Test
	public void testWatch2() {
		Watch w = new Watch("query");
		ThreadUtil.sleep(1000);
		w.cost();
		ThreadUtil.sleep(1000);
		w.exceptionWithThrow(new Exception("aaaaaaaaaaa"), log);
		Tools.out(w);
	}
	@Test
	public void testWatch3() {
		Watch w = new Watch("query");
		ThreadUtil.sleep(1000);
		w.cost("sql");
		ThreadUtil.sleep(1000);
		w.cost("parse");
		w.res(5, log);
		Tools.out(w);
	}
	
	
}
