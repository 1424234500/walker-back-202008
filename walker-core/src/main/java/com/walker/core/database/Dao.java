package com.walker.core.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.walker.common.util.MapListUtil;
import com.walker.common.util.Page;
import com.walker.common.util.Watch;

/**
 * 数据库常用操作工具 选择一种连接池实现 每种连接池对应多种数据源 多种数据库
 * 
 * jdbc 一个实例 绑定 连接池 和 数据源 但 底层 是同一个连接池 且 每个连接池每个 数据源是 唯一
 */
public class Dao implements BaseDao {
	private static Logger log = Logger.getLogger(Dao.class);

	private Pool pool;
	private String dsName = Pool.defaultDsName;
	private Connection conn;

	public Dao() {
		this.pool = PoolMgr.getInstance();
	}

	public Dao(Type type) {
		this.pool = PoolMgr.getInstance(type);
	}

	public void setDs(String dsName) {
		this.dsName = dsName;
	}

	@Override
	public String getDs() {
		return this.dsName;
	}

	// 获取链接
	private Connection getConnection() throws SQLException {
		if (conn == null || conn.isClosed()) {
			conn = this.pool.getConn(dsName);
		}
		return conn;
	}

	private void close(Connection conn, PreparedStatement pst, ResultSet rs) {
		this.pool.close(conn, pst, rs);
	}
//	PreparedStatement ps = conn.prepareStatement(sql);
//	conn = this.pool.getConn(dsName);
//	this.pool.close(conn, pst, rs);


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
	public List<Map<String, Object>> findPage(String sql, int page, int rows, Object... objects) {
		sql = SqlUtil.makeSqlPage(getDs(), sql, page, rows);
		return this.find(sql, objects);
	}

	@Override
	public List<Map<String, Object>> findPage(Page page, String sql, Object... objects) {
		page.setNUM(this.count(sql, objects));
		sql = SqlUtil.makeSqlOrder(sql, page.getORDER());
		return this.findPage(sql, page.getNOWPAGE(), page.getSHOWNUM(), objects);
	}

	@Override
	public int count(String sql, Object... objects) {
		int res = 0;
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
		Watch w = new Watch(SqlUtil.makeSql(sql));

		List<String> res = null;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = this.getConnection();
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			res = SqlUtil.toKeys(rs);
			w.res(res, log);
		} catch (Exception e) {
			w.exceptionWithThrow(e, log);
		} finally {
			close(conn, pst, rs);
		}
		return res;
	}
	@Override
	public List<Map<String, Object>> find(String sql, Object... objects) {
		sql = SqlUtil.filter(sql);
		Watch w = new Watch(SqlUtil.makeSql(sql, objects));
		List<Map<String, Object>> res = null;
		Connection conn = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			conn = this.getConnection();
			pst = conn.prepareStatement(sql);
			for (int i = 0; i < objects.length; i++) {
				pst.setObject(i + 1, objects[i]);
			}
			rs = pst.executeQuery();
			res = SqlUtil.toListMap(rs);
			w.res(res, log);
		} catch (Exception e) {
			w.exceptionWithThrow(e, log);
		} finally {
			close(conn, pst, rs);
		}
		return res;

	}
	@Override
	public int executeSql(String sql, Object... objects) {
		sql = SqlUtil.filter(sql);
		Watch w = new Watch(SqlUtil.makeSql(sql, objects));
		int res = 0;
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = this.getConnection();
			pst = conn.prepareStatement(sql);
			for (int i = 0; i < objects.length; i++) {
				pst.setObject(i + 1, objects[i]);
			}
			res = pst.executeUpdate();
			w.res(res, log);
		} catch (Exception e) {
			w.exceptionWithThrow(e, log);
		} finally {
			close(conn, pst, null);
		}
		return res;
	}

	public int[] executeSql(String sql, List<List<Object>> objs) {
		sql = SqlUtil.filter(sql);
		Watch w = new Watch(sql).put("size", objs.size()).put("eg", objs.get(0));
		int[] res = {};
		Connection conn = null;
		PreparedStatement pst = null;
		try {
			conn = this.getConnection();
			pst = conn.prepareStatement(sql);
			for (List<Object> objects : objs) {
				for (int i = 0; i < objects.size(); i++) {
					pst.setObject(i + 1, objects.get(i));
				}
				pst.addBatch();
			}
			res = pst.executeBatch();
			w.res(Arrays.toString(res), log);
		} catch (Exception e) {
			w.exceptionWithThrow(e, log);
		} finally {
			close(conn, pst, null);
		}
		return res;
	}

	/**
	 * 
	 * 调用存储过程的语句，call后面的就是存储过程名和需要传入的参数
	 * 
	 * @param proc    "{call countBySal(?,?)}"
	 * @param objects
	 * @return
	 */
	@Override
	public int executeProc(String proc, Object... objects) {
		Watch w = new Watch(proc);
		int res = 0;

		Connection conn = null;
		CallableStatement cst = null;
		try {
			conn = this.getConnection();

			cst = conn.prepareCall(proc);
			for (int i = 0; i < objects.length; i++) {
				if (i == objects.length - 1) {
					cst.registerOutParameter(objects.length + 1, Types.INTEGER);// 注册out参数的类型
				} else {
					cst.setObject(i + 1, objects[i]);
				}
			}
			cst.execute();
			res = cst.getInt(objects.length);
			w.res(res, log);
		} catch (Exception e) {
			w.exceptionWithThrow(e, log);
		} finally {
			close(conn, cst, null);
		}
		return res;
	}
}
