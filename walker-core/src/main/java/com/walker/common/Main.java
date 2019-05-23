package com.walker.common;

import com.walker.common.util.Tools;

public class Main {
	
	public static void main(String[] args) {
		new Main();
	}

	
	public Main() {
		
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
