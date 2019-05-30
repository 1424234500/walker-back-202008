package com.walker.core.cache;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.walker.common.util.TestBase;
import com.walker.common.util.Tools;

public class CacheMgrTest extends TestBase{

	public CacheMgrTest() {
		super();
	}

	@Test
	public void test() {

		Cache<String> cache = CacheMgr.getInstance(Type.EHCACHE);
		cache.put("aaa", "value");
		Tools.out(1, cache.get("aaa", "bbb"));
		
		
		
	}

}
