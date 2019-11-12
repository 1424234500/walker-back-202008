package com.walker.service;

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
	 * 登陆用户操作 记录
	 */
	public void saveControl(String userid, String url, String ip, String host, int port, String params);

	/**
	 * 所有请求时间花费 统计 redis
	 */
	public void saveStatis(String url, String params, long costtime);

	/**
	 * 把统计在redis中的数据导出到oracle
	 */
	public void saveStatis();


}