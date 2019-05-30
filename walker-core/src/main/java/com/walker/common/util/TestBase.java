package com.walker.common.util;

import java.io.File;

import org.apache.log4j.PropertyConfigurator;

/**
 * 测试类基类
 * 初始化 日志
 * 初始化其他
 * 
 */
public abstract class TestBase{
	public TestBase(){
		PropertyConfigurator.configure(Context.getPathConf("log4j.properties"));
	}
	
	

} 