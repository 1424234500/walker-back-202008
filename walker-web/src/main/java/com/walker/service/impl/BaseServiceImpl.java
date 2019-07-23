package com.walker.service.impl;

import com.walker.common.util.Page;
import com.walker.service.BaseService;
import com.walker.web.dao.hibernate.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
@Transactional
@Service("baseService")
public class BaseServiceImpl implements BaseService,Serializable {
 
	private static final long serialVersionUID = 8304941820771045214L;
	/**
     * hibernate入口
     */
    
    @Autowired
    protected BaseDao baseDao;
 


	@Override
	public List<Map<String, Object>> find(String sql, Object... params) {
		return baseDao.find(sql, params);
	}

	@Override
	public Map findOne(String sql, Object... params) {
		return baseDao.findOne(sql, params);
	}

	@Override
	public List<Map<String, Object>> findPage(Page page, String sql, Object... params) {
		page.setNUM(baseDao.count(sql, params ));
		return baseDao.findPage(sql,page.getNOWPAGE(),page.getSHOWNUM(), params );
	}

	@Override
	public Integer executeSql(String sql, Object... params) {
		return baseDao.executeSql(sql, params);
	}

	@Override
	public Integer count(String sql, Object... params) {
		return baseDao.count(sql, params);
	}

	@Override
	public void setDs(String ds) {
		baseDao.setDs(ds);
	}

	@Override
	public String getDs() {
		return baseDao.getDs();
	}

	@Override
	public List<String> getColumnsBySql(String sql) {
		return baseDao.getColumnsBySql(sql);
	}

	@Override
	public List<String> getColumnsByTableName(String tableName) {
		return baseDao.getColumnsByTableName(tableName);
	}

	@Override
	public List<Map<String, Object>> findPage(String sql, Integer page, Integer rows, Object... params) {
		return baseDao.findPage(sql, page, rows, params);
	}

	@Override
	public Integer[] executeSql(String sql, List<List<Object>> objs) {
		return baseDao.executeSql(sql, objs);
	}

	@Override
	public Integer executeProc(String proc, Object... objects) {
		return baseDao.executeProc(proc, objects);
	}

 

}