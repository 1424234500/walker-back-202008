package com.walker.service.impl;

import com.walker.core.database.BaseDaoAdapter;
import com.walker.service.BaseService;
import com.walker.service.EchoService;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


@org.springframework.stereotype.Service
@com.alibaba.dubbo.config.annotation.Service
@Transactional
//@Scope("prototype")
public class BaseServiceImpl extends BaseDaoAdapter implements BaseService,Serializable {
	private static final long serialVersionUID = 8304941820771045214L;


	/**
	 * 数据源 不同数据源 不同实现方式 分页 ddl dml
	 *
	 * @param ds
	 */
	@Override
	public void setDs(String ds) {
		
	}

	@Override
	public String getDs() {
		return null;
	}

	/**
	 * 获得结果集
	 *
	 * @param sql    SQL语句
	 * @param params 参数
	 * @return 结果集
	 */
	@Override
	public List<Map<String, Object>> find(String sql, Object... params) {
		return null;
	}

	/**
	 * 执行SQL语句
	 *
	 * @param sql
	 * @param params
	 * @return 响应行数
	 */
	@Override
	public Integer executeSql(String sql, Object... params) {
		return null;
	}

	/**
	 * 批量执行sql
	 *
	 * @param sql
	 * @param objs
	 * @return
	 */
	@Override
	public Integer[] executeSql(String sql, List<List<Object>> objs) {
		return new Integer[0];
	}

	/**
	 * 执行存储过程 最后一个占位?返回值
	 *
	 * @param proc    "{call countBySal(?,?)}"
	 * @param objects
	 * @return
	 */
	@Override
	public Integer executeProc(String proc, Object... objects) {
		return null;
	}
}