package com.walker.core;

import java.util.ArrayList;
import java.util.List;

import com.walker.common.util.Context;
import com.walker.common.util.Tools;

public class Launcher {

	public static void main(String[] args) {
		new Launcher();
	}

	
	public Launcher() {
		Tools.out(Context.getPathRoot());

//		test1();
//		testAnd();
		testSplit();
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
