package com.walker.service;

import java.util.Map;

/**
 * 日志管理
 */
public interface FileService  {
	/**
	 * 扫描文件夹下文件信息 并存入数据库记录
	 */
	public void saveScan();
	/**
	 * 上传文件后 根据crc码和路径path 来存储文件
	 * 返回0插入
	 * 返回1更新
	 * 返回2 -1 其他
	 */ 
	public int saveUpload(String key, String id, String name, String path, String about);
	
	public Map<String,Object> findFile(String key, String path);
	public void deleteFile(String key, String path);

	/**
	 * 文件上传或者下载记录 fileId up/down 耗时detaTime
	 */
	public int saveUpOrDown(String fileId, String type, String detaTime);
	/**
	 * 初始化文件目录
	 */
	public void initDirs();
	
	
	
}