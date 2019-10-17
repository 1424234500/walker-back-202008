package com.walker.service;

import com.walker.common.util.Page;
import com.walker.mode.FileIndex;

import java.util.List;
import java.util.Map;

/**
 * 文件索引管理
 */
public interface FileIndexService  {
	List<FileIndex> saveAll(List<FileIndex> obj);
	Integer[] deleteAll(List<String> ids);

	FileIndex get(FileIndex obj);
	Integer delete(FileIndex obj);

	List<FileIndex> finds(FileIndex obj, Page page);
	Integer count(FileIndex obj);

	
}