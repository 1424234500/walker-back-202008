package com.walker.service;

import com.walker.mode.JobHis;
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
	 * 保存jobhis quartz类执行日志
	 * @param jobHis
	 */
	public void saveJobHis(JobHis jobHis);

	/**
	 * 登陆用户操作 记录
	 */
	public void saveControl(LogModel logModel);

	/**
	 * 把统计在redis中的数据导出到oracle
	 */
	public void saveStatis();


}