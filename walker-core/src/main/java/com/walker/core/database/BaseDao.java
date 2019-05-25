package com.walker.core.database;

import java.util.List;
import java.util.Map;

import com.walker.common.util.Page;

/**
 * 基础数据库操作类
 * 
 * 统一使用Map<String, Object>对象
 * 
 * @author
 * 
 */
public interface BaseDao {

	/**
	 * 数据源 不同数据源 不同实现方式 分页 ddl dml
	 * 
	 * @param ds
	 */
	public void setDs(String ds);
	public String getDs() ;

	/**
	 * 获取表的列数组
	 * 
	 * @param tableName
	 * @return
	 */
	public List<String> getColumnsByTableName(String tableName);

	/**
	 * 获取单条记录
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public Map<String, Object> findOne(String sql, Object... params);

	/**
	 * 获得结果集
	 * 
	 * @param sql    SQL语句
	 * @param params 参数
	 * @param page   要显示第几页
	 * @param rows   每页显示多少条
	 * @return 结果集
	 */
	public List<Map<String, Object>> findPage(String sql, int page, int rows, Object... params);

	/**
	 * 获得结果集 分页
	 * 
	 * @param sql    SQL语句
	 * @param params 参数
	 * @param page   分页对象 排序
	 * @return 结果集
	 */
	public List<Map<String, Object>> findPage(Page page, String sql, Object... params);

	/**
	 * 统计
	 * 
	 * @param sql    SQL语句
	 * @param params 参数
	 * @return 数目
	 */
	public int count(String sql, Object... params);

	
	
	
	
/////////////////////////////////必须实现 其他依赖以下接口
	/**
	 * 获得结果集		
	 * 
	 * @param sql    SQL语句
	 * @param params 参数
	 * @return 结果集
	 */
	public List<Map<String, Object>> find(String sql, Object... params);

	/**
	 *获取查询结果的列数组
	 * 
	 * @param sql SQL语句
	 * @return String List数组
	 */
	public List<String> getColumnsBySql(String sql);
	/**
	 * 执行SQL语句
	 * 
	 * @return 响应行数
	 */
	public int executeSql(String sql, Object... params);

	/**
	 * 批量执行sql
	 * 
	 * @return
	 */
	public int[] executeSql(String sql, List<List<Object>> objs);

	/**
	 * 执行存储过程 最后一个占位?返回值
	 * 
	 * @param proc    "{call countBySal(?,?)}"
	 * @param objects
	 * @return
	 */
	public int executeProc(String proc, Object... objects);

}
