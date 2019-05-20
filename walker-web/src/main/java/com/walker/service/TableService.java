package com.walker.service;

import org.apache.log4j.Logger;

/**
 * 表管理 初始化数据库表
 */
public interface TableService  {
	Logger log = Logger.getLogger(TableService.class); 

	
	/**
	 * 每次启动时 初始化表
	 */
	public void initTable();
	
}