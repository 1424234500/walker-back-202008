package com.walker.service.impl;

import com.walker.core.database.BaseDaoAdapter;
import com.walker.dao.JdbcDao;
import com.walker.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@org.springframework.stereotype.Service("baseService")
@com.alibaba.dubbo.config.annotation.Service
@Transactional
public class BaseServiceImpl extends BaseDaoAdapter implements BaseService,Serializable {
	private static final long serialVersionUID = 8304941820771045214L;

	public BaseServiceImpl(){
		this.setDs("mysql");
	}

	@Autowired
	JdbcDao jdbcDao;

	String ds = "mysql";
	@Override
	public void setDs(String ds) {
		this.ds = ds;
	}

	@Override
	public String getDs() {
		return ds;
	}

	@Override
	public List<Map<String, Object>> find(String sql, Object... params) {
//		return jdbcDao.queryForList(sql, params);
		return jdbcDao.find(sql, params);
	}

	@Override
	public Integer executeSql(String sql, Object... params) {
//		return jdbcDao.update(sql, params);
		return jdbcDao.executeSql(sql, params);
	}

	@Override
	public Integer[] executeSql(String sql, List<List<Object>> objs) {
		List<Object[]> args = new ArrayList<>();
		for(List<Object> arg : objs){
			args.add(arg.toArray());
		}
//		int resint[] = jdbcDao.batchUpdate(sql, args);
		Integer resint[] = jdbcDao.executeSql(sql, objs);
		Integer[] res = new Integer[resint.length];
		for(int i = 0; i< resint.length; i++){
			res[i] = resint[i];
		}
		return res;
	}

	@Override
	public Integer executeProc(String proc, Object... objects) {
		return 0;
	}

}