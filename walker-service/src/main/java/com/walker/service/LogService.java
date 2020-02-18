package com.walker.service;

import com.walker.mode.LogModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志管理
 */
public interface LogService  {
	Logger log = LoggerFactory.getLogger(LogService.class);
	String CACHE_KEY = "cache-url-request";
	String CACHE_KEY_CONTROL = "cache-url-request-control";


	/**
	 * 日志记录模型 controller job
	 */
	void saveLogModel(LogModel logModel);

	/**
	 * 把缓冲的日志记录和 日志统计结果记录 持久化
	 */
	void saveStatis();


}