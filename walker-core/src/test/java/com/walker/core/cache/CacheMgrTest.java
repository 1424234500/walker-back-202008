package com.walker.core.cache;

import java.io.File;

import org.junit.Test;

import com.walker.common.util.TestBase;
import com.walker.common.util.Tools;

public class CacheMgrTest extends TestBase{

	public CacheMgrTest() {
		super();
	}

	@Test
	public void test() {

		Cache<String> cache = CacheMgr.getInstance(CacheMgr.Type.MAP);
		cache.put("aaa", "value");
		Tools.out(1, cache.get("aaa", "bbb"));
		
		
		
	}

}
