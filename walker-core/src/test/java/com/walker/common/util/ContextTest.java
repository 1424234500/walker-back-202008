package com.walker.common.util;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.junit.Test;

import com.walker.core.aop.Fun;

public class ContextTest {

	@Test
	public void test() {
		URL url = Context.class.getResource("/");///walker/walker-socket/target/classes/
		File f = new File(url.getFile());
		Tools.out(f.getAbsolutePath());
		f = new File(f.getParent());
		Tools.out(f.getAbsolutePath());
		f = new File(f.getParent());
		Tools.out(f.getAbsolutePath());
		
		
		
		
		Tools.out(Context.getPathRoot());
		Tools.formatOut(new File(Context.getPathRoot()).list());
		
		
	}

}
