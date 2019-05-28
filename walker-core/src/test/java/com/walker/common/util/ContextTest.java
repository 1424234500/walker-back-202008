package com.walker.common.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import com.walker.core.aop.Fun;

public class ContextTest {

	@Test
	public void test() {
		Tools.out(Context.getPathRoot());
		FileUtil.showDir(Context.getPathRoot(), new Fun<File>() {
			
			@Override
			public <T> T make(File obj) {
				Tools.out(obj);
				return null;
			}
		});
		
		
	}

}
