package com.walker.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import com.walker.common.util.Context;
import com.walker.common.util.FileUtil;
import com.walker.common.util.Tools;

public class Launcher {

	public static void main(String[] args) {
		new Launcher();
	}

	
	public Launcher() {
		PropertyConfigurator.configure(Context.getPathRoot("conf" + File.separator + "log4j.properties"));
		
		Tools.out("-----------------launcher-------------------");
		String root = Context.getPathRoot();
		Tools.out(root);
		String[] list = new File(root).list();
		Tools.formatOut(list);

//		test1();
//		testAnd();
		testSplit();
		Tools.out("-----------------end-------------------");
	}
	public void testSplit() {
		Tools.out(",a,b".split(",")[1]);
	}
	public void testAnd() {
		int max = 10;
		
		for(int i = 0; i < max; i++) {
			byte c = (byte) i;
			Tools.out(c);
			List<Object> list = new ArrayList<>();
			for(int j = 0; j < 8; j++) {
				list.add(c & j);
			}
			Tools.out(list);
			
		}
		
		
		
	}
	public void test1() {

		int a = 1;
		Integer b = new Integer(1);
		Integer c = a;
		int d = b;
		fun(a, b, c, d, b.intValue());
	}
	public void fun(Object...methodArgs) {
		Class<?>[] args = new Class[methodArgs.length];
		for (int i = 0; i < methodArgs.length; ++i) {
			args[i] = methodArgs[i].getClass();
			Tools.out(args[i]);
		}
		
	
	}

}