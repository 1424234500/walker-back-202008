package com.walker.web.controller;

import com.walker.core.cache.CacheMgr;

/**
 * 系统共用环境 结合缓存 统一接口 避免多处直接查询缓存 缓存设置值来源于 系统初始化 时加载的 配置文件 或者 数据库
 *
 */
public class ContextSystem extends com.walker.common.util.Context{

	final static public String defaultFileUploadDir = "/home/walker/tomcat";
	final static public String defaultFileDownloadDirs = "/home/walker/tomcat,/home/walker/help_note";

	static public String getUploadDir() {
		return CacheMgr.getInstance().get("FILE_UPLOAD_DIR", defaultFileUploadDir);
	}

	static public String getScanDirs() {
		return CacheMgr.getInstance().get("FILE_SCAN_DIR", defaultFileDownloadDirs);
	}

}
