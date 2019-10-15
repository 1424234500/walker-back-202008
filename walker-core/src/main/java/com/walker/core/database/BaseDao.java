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
public interface BaseDao extends BaseJdbc{


	/**
	 *获取 mysql数据库列表 oracle用户列表
	 *
	 * @return String List数组
	 */
	public List<String> getDatabasesOrUsers();
	/**
	 *获取指定用户/db的表列表
	 *
	 * @param userOrDb oracle用户 or mysql数据库db	为空则查当前用户/db
	 * @return String List数组
	 */
	public List<String> getTables(String userOrDb);


	/**
	 * 获取表的列和备注map
	 *
	 * @param tableName
	 * @return
	 */
	public Map<String, String> getColumnsMapByTableName(String dbOrUser, String tableName);
		/**
         * 获取表的列数组
         *
         * @param tableName
         * @return
         */
	public List<String> getColumnsByTableName(String dbOrUser, String tableName);
		/**
         *获取查询结果的列数组
         *
         * @param sql SQL语句
         * @return String List数组
         */
	public List<String> getColumnsBySql(String sql);





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
	public List<Map<String, Object>> findPage(String sql, Integer page, Integer rows, Object... params);

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
	public Integer count(String sql, Object... params);

	
	
	


}
