package com.walker.core.annotation;
import org.apache.log4j.Logger;

import com.walker.common.util.Call;
import com.walker.core.aop.TestAdapter;

/**
 *	注解处理工厂 
 *	负责初始化各种注解配置
 *
 */

public class TrackerMgr extends TestAdapter{
	static public Logger log = Logger.getLogger("Annotation"); 

	public boolean doInit() {
		return start();
	} 
	
	/**
	 * 扫描所有注解 以及对应的处理器 并调用执行处理器 初始化注解系统
	 * @return
	 */
	public static Boolean start(){
		log.info("**扫描所有注解 以及对应的处理器 并调用执行处理器 初始化注解系统");
		TrackerUtil.make("", TrackerUtil.scan(""));
		log.info("**!扫描所有注解 以及对应的处理器 并调用执行处理器 初始化注解系统");
		return true;
	}
	
}