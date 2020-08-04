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


	List<FileIndex> findsAllById(List<String> ids);
	Integer[] deleteAll(List<String> ids);

	List<FileIndex> findsAllByPath(List<String> paths);
	Integer[] deleteAllByPath(List<String> paths);

	FileIndex get(FileIndex obj);
	Integer delete(FileIndex obj);

	FileIndex get(String checksums);
	Integer delete(String checksums);


	List<FileIndex> findsAllByStartPath(String startPath);
	Integer deleteAllByStartPath(String startPath);

	Integer count(FileIndex obj);

	List<FileIndex> finds(FileIndex obj, Page page);


}