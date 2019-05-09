package com.walker.core.aop;
/**
 * 测试接口
 * 用于需要启动测试的模块 
 * 抛出runtime异常   TipException
 * 
 * eg：
 * 		缓存启动 自检
 * 		redis连接启动 自检
 * 
 */

public interface Test{ 
	/**
	 * 测试功能
	 */
	public void test(); 
	
}


