package com.walker.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.walker.common.util.LangUtil;
import com.walker.common.util.MakeMap;
import com.walker.common.util.Page;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.core.database.SqlUtil;
import com.walker.service.StudentService;
import com.walker.web.controller.Context;
import com.walker.web.dao.hibernate.BaseDao;
@Transactional
@Service("studentServiceHibernate")
public class StudentServiceImplHibernate implements StudentService,Serializable {
 
	private static final long serialVersionUID = 8304941820771045214L;
	/**
     * hibernate入口
     */
	@Autowired
	private SessionFactory sessionFactory;
    
    @Autowired
    protected BaseDao baseDao;

	@Override
	public List<Map<String, Object>> finds(String id, String name, String sFlag, String timefrom, String timeto, Page page) {
		String sql = "";
		List<String> params = new ArrayList<String>();
		sql += "select * from student where 1=1";
		if(Tools.notNull(id)){
			sql += " and id like ? ";
			params.add("%" + id + "%");
		} 
		if(Tools.notNull(name)){
			sql += " and name like ? ";
			params.add("%" + name + "%");
		}
		if(Tools.notNull(sFlag)){
			sql += " and s_flag = ?  ";
			params.add(Context.valueFlag(sFlag));
		}
		if(Tools.notNull(timefrom)){
			sql += " and s_mtime >= ?";
			params.add(timefrom);
		}
		if(Tools.notNull(timeto)){
			sql += " and s_mtime < ?";
			params.add( timeto);
		} 
		
		return baseDao.findPage(page, sql, params.toArray());
	}

	@Override
	public int update(String id, String name, String time) {
		int res = baseDao.executeSql(
				"update student set name=?,s_mtime=? where id=?",
				name,TimeUtil.getTimeYmdHmss(),id  );
		return res;
	}
	@Override
	public int add(String name, String time) {
		int res = 0;
		res = baseDao.executeSql("insert into student values(?,?,?,?)",LangUtil.getGenerateId(), name, TimeUtil.getTimeYmdHmss(), Context.YES);
 		return res;
	}
	@Override
	public int delete(String id) {
//		int res = 0;
//		res = baseDao.executeSql("delete from student where id=? ", id);
// 		return res;
 		int res = baseDao.executeSql(
				"update student set s_flag=?,s_mtime=? where id=?",
				Context.NO, TimeUtil.getTimeYmdHmss(), id  );
		return res;
	}
	@Override
	public Map find(String id) {
 		return  baseDao.findOne("select * from student where id=? ", id);
	}

}