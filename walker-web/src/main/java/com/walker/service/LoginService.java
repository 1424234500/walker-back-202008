package com.walker.service;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

/**
 * 登录服务
 */
public interface LoginService  {
	Logger logger = Logger.getLogger(LoginService.class); 
	String CACHE_KEY = "cache-user-token";
	/**
	 * 默认 游客登录
	 */
	Boolean login();
	/**
	 * 指定id pwd登录
	 */
	Boolean saveLogin(String id, String pwd);
		
}