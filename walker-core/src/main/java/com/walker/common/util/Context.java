package com.walker.common.util;

import java.net.URL;

/**
 * 运行上下文 
 * 
 * 
 * 常量配置池
 * @author walker
 *
 */
public class Context {
	
	
	
	
	/**
	 * 文件路径
	 * @return
	 */
	public static String getPathRoot() {
		
		URL url = ClassLoader.getSystemResource("");
		if(url != null) {
			return url.getPath();
		}
		url = Context.class.getResource("/");
		if(url != null) {
			return url.getPath();
		}
		return "/root";
	}
	
	
	
	
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
