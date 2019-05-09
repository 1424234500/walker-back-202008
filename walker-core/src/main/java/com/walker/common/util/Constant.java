package com.walker.common.util;


/**
 * 常量配置池
 * @author walker
 *
 */
public class Constant {
	public static String beginTip(Class<?> clz) {
		return beginTip(clz.getName());
	}	
	public static String beginTip(String tip) {
		return tip + " begin";
	}
	public static String endTip(Class<?> clz) {
		return endTip(clz.getName());
	}	
	public static String endTip(String tip) {
		return tip + " end";
	}
	
	public static String okTip(Class<?> clz) {
		return okTip(clz.getName());
	}	
	public static String okTip(String tip) {
		return tip + " ok";
	}

	public static String errorTip(Class<?> clz) {
		return errorTip(clz.getName());
	}	
	public static String errorTip(String tip) {
		return tip + " error";
	}
	
	
	
	
	
}
