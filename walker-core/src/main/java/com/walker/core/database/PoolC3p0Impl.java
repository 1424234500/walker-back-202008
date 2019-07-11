package com.walker.core.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.walker.common.util.Context;
import com.walker.common.util.MapListUtil;
import com.walker.common.util.Watch;


/**
 * 连接接口 c3p0实现
 * 
 * @author walker
 *
 */
@SuppressWarnings("unchecked")
class PoolC3p0Impl extends Pool {
	/**
	 * 根据通用配置 初始化多个数据源
	 */
	private static Map<String, ComboPooledDataSource> dataSource;
	static {
		log.warn(Context.beginTip("init pool"));
		dataSource = new LinkedHashMap<>();
		for(String name : dsConfigPro.keySet()){ //jdbc
//			###----------------- Oracle -----------------  
//			jdbc.oracle.DriverClass=oracle.jdbc.driver.OracleDriver
//			jdbc.oracle.JdbcUrl=jdbc\:oracle\:thin\:@127.0.0.1\:1521\:xe
//			jdbc.oracle.User=walker
//			jdbc.oracle.Password=qwer
//			-------------->>>>>>>>>>>>>>>
//			ds.setDriverClass(driverClass);
//			ds.setJdbcUrl(jdbcUrl);
//			ds.setUser(user);
//			ds.setPassword(password);
			Watch w = new Watch(name);
			try {
				Map<String, String> map = (Map<String, String>) dsConfigPro.get(name); //oracle
				w.put("map", map);
				map.putAll(MapListUtil.copy(dsConfig));//"InitialPoolSize", "MaxPoolSize"
				w.put("dsConfig", dsConfig);
				System.setProperty("com.mchange.v2.c3p0.cfg.xml", Context.getPathConf("c3p0-config.xml")); 
				ComboPooledDataSource ds = new ComboPooledDataSource(name);
				//针对c3p0配置的键值  若是其他连接池需要对这些键 做映射 map 之后才能反射初始化
	//			ds.setDataSourceName(name);
				setDataSource(ds, map);
				dataSource.put(name, ds);
				w.res(log);
			}catch (Exception e) {
				w.exception(e, log);
			}
		}
		log.warn(Context.endTip("init pool"));
	}

	
	@Override
	public Connection getConn() {
		return getConn(defaultDsName);
	}
	@Override
	public Connection getConn(String dsName) {
		try {
			dsName = dsName == null || dsName.length() == 0 ? defaultDsName : dsName;
			return dataSource.get(dsName).getConnection();
		} catch (Exception e) {
			log.error("Exception in C3p0Utils! of " + dsName, e);
			throw new RuntimeException("数据库连接出错!" + dsName, e);
		}
	}
	@Override
	public void close(Connection conn, PreparedStatement pst, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				log.error("Exception in C3p0Utils! ResultSet", e);
				throw new RuntimeException("数据库连接关闭出错!", e);
			}
		}
		if (pst != null) {
			try {
				pst.close();
			} catch (SQLException e) {
				log.error("Exception in C3p0Utils! PreparedStatement", e);
				throw new RuntimeException("数据库连接关闭出错!", e);
			}
		}

		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("Exception in C3p0Utils! Connection", e);
				throw new RuntimeException("数据库连接关闭出错!", e);
			}
		}
	}

}
