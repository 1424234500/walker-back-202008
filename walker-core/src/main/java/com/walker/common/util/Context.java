package com.walker.common.util;

import java.io.File;
import java.net.URL;

import com.walker.core.cache.CacheMgr;

/**
 * 运行上下文 
 * 
 * 
 * 常量配置池
 * @author walker
 *
 */
public class Context {
	
	public final static String YES = "1";
	public final static String NO = "0";
	public static String valueFlag(String str) {
		if(str.equals(YES)) {
			return YES;
		}
		return NO;
	}
	
	
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
	/**
	 * 相对路径
	 * @param file
	 * @return
	 */
	public static String getPathRoot(String file) {
		return getPathRoot() + File.separator + file;
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
	
	
	

	/**
	 * 默认数据库操作一次大小
	 */
	public static int getDbOnce() {
		return CacheMgr.getInstance().get("DB_ONCE", 500);
	}
	/**
	 * 默认分页每页数量
	 */
	public static int getShowNum() {
		return CacheMgr.getInstance().get("SHOW_NUM", 5);
	}

	
}
