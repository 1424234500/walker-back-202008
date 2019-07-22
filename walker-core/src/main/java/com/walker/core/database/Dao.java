package com.walker.core.database;

import com.walker.common.util.MapListUtil;
import com.walker.common.util.Page;
import com.walker.common.util.Watch;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 数据库常用操作工具 选择一种连接池实现 每种连接池对应多种数据源 多种数据库
 * 
 * jdbc 一个实例 绑定 连接池 和 数据源 但 底层 是同一个连接池 且 每个连接池每个 数据源是 唯一
 */
public class Dao extends BaseDaoAdapter {

	private Pool pool;
	private Connection conn;

	public Dao() {
		this.pool = PoolMgr.getInstance();
	}

	public Dao(Type type) {
		this.pool = PoolMgr.getInstance(type);
	}

	/**
	 *  获取链接
	 */
	private Connection getConnection() throws SQLException {
		if (conn == null || conn.isClosed()) {
			conn = this.pool.getConn(dsName);
		}
		return conn;
	}

	/**
	 * 关闭连接
	 */
	private void close(Connection conn, PreparedStatement pst, ResultSet rs) {
		this.pool.close(conn, pst, rs);
	}
//	PreparedStatement ps = conn.prepareStatement(sql);
//	conn = this.pool.getConn(dsName);
//	this.pool.close(conn, pst, rs);



	String dsName = "mysql";

	public void setDs(String dsName) {
		this.dsName = dsName;
	}

	@Override
	public String getDs() {
		return this.dsName;
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
	public Integer executeSql(String sql, Object... objects) {
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

	public Integer[] executeSql(String sql, List<List<Object>> objs) {
		sql = SqlUtil.filter(sql);
		Watch w = new Watch(sql).put("size", objs.size()).put("eg", objs.get(0));
		Integer[] res = {};
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
			int resint[] = pst.executeBatch();
			res = new Integer[resint.length];
			for(int i = 0; i< resint.length; i++){
				res[i] = resint[i];
			}
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
	public Integer executeProc(String proc, Object... objects) {
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
