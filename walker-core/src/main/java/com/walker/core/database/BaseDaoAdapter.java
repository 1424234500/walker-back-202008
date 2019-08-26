package com.walker.core.database;

import com.walker.common.util.MapListUtil;
import com.walker.common.util.Page;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据库常用操作工具 选择一种连接池实现 每种连接池对应多种数据源 多种数据库
 * 
 * jdbc 一个实例 绑定 连接池 和 数据源 但 底层 是同一个连接池 且 每个连接池每个 数据源是 唯一
 *
 * 部分实现 抽离关键 子实现
 */
public abstract class BaseDaoAdapter implements BaseDao {
	protected static Logger log = Logger.getLogger(BaseDaoAdapter.class);



	@Override
	public List<String> getColumnsByTableName(String tableName) {
		List<String> res = null;
		List<List<String>> list = MapListUtil.toArrayAndTurn(this.find(SqlUtil.makeSqlColumn(getDs(), tableName)));
		list = MapListUtil.turnRerix(list);
		if (list.size() > 0) {
			res = list.get(0);
		} else {
			res = new ArrayList<String>();
		}
		return res;
	}



	@Override
	public Map<String, Object> findOne(String sql, Object... objects) {
		List<Map<String, Object>> list = this.findPage(sql, 1, 1, objects);
		Map<String, Object> res = null;
		if (list.size() >= 1) {
			res = list.get(0);
		}
		return res;
	}

	/**
	 * 获得结果集
	 * 
	 * @param sql    SQL语句
	 * @param page   要显示第几页
	 * @param rows   每页显示多少条
	 * @return 结果集
	 */
	@Override
	public List<Map<String, Object>> findPage(String sql, Integer page, Integer rows, Object... objects) {
		sql = SqlUtil.makeSqlPage(getDs(), sql, page, rows);
		return this.find(sql, objects);
	}

	@Override
	public List<Map<String, Object>> findPage(Page page, String sql, Object... objects) {
		page.setNum(this.count(sql, objects));
		sql = SqlUtil.makeSqlOrder(sql, page.getOrder());
		return this.findPage(sql, page.getNowpage(), page.getShownum(), objects);
	}

	@Override
	public Integer count(String sql, Object... objects) {
		Integer res = 0;
		sql = SqlUtil.makeSqlCount(sql);
		List<List<String>> list = MapListUtil.toArray(this.find(sql, objects));
		if(list != null && list.size() > 0) {
			List<String> row = list.get(0);
			if(row != null && row.size() > 0) {
				res = Integer.valueOf(row.get(0));
			}
		}
		return res;
	}
	
	
	
	@Override
	public List<String> getColumnsBySql(String sql) {
		return SqlUtil.toKeys(this.findPage(sql, 1, 1));
	}

}
