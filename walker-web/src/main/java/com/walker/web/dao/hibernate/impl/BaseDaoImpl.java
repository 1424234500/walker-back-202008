package com.walker.web.dao.hibernate.impl;

import java.sql.*;
import java.util.*;

import org.hibernate.*;
import org.hibernate.jdbc.Work;
import org.hibernate.transform.Transformers;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.walker.common.util.MapListUtil;
import com.walker.common.util.Page;
import com.walker.core.cache.CacheMgr;
import com.walker.core.database.SqlUtil;
import com.walker.web.dao.hibernate.BaseDao;

 
@Repository("baseDao")
public class BaseDaoImpl implements BaseDao  {
	static public Logger log = Logger.getLogger("Hibernate"); 
	String dsName = CacheMgr.getInstance().get("jdbcdefault", "mysql");
	public void out(String str){
		log.info(str);
	}

	@Override
	public String getDs() {
		return this.dsName;
	}
	@Override
	public void setDs(String ds) {
		this.dsName = ds;
	}

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * 获得当前事务的session
	 * @return org.hibernate.Session
	 */
	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * 获取列名集合的第一种方式
	 */
	public List<String> getColumnsBySql(String sql){
		final String sqlStr=sql;
		final List<String> list=new ArrayList<String>();
		getCurrentSession().doWork( new Work() {  
		    public void execute(Connection connection) {
		    	try{
		    	PreparedStatement pstm=connection.prepareStatement(sqlStr);
		    	ResultSet rs  = pstm.executeQuery(); 
		    	list.addAll(SqlUtil.toKeys(rs));
		      }catch(Exception e){
		    	e.printStackTrace();
		    }
		    }  
		    });
		return list;
	}
	  
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> find(String sql, Object... objects) {
		SQLQuery q = getCurrentSession().createSQLQuery(sql);
		setObjectsToSql(q, objects);
		return q.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	@Override
	public Map<String, Object> findOne(String sql, Object... objects) {
		List<Map<String, Object>> list = this.findPage(sql, 1, 1, objects);
		Map<String, Object> res = null;
		if(list != null && list.size() > 0){
			res = list.get(0);
		} 
		return res;
	}
	
	@Override
	public List<Map<String, Object>> findPage(String sql, Integer page, Integer rows, Object... objects) {
		return this.find(SqlUtil.makeSqlPage(getDs(), sql, page, rows), objects);
	}
 
	@Override
	public Integer executeSql(String sql, Object... objects) {
		SQLQuery q = getCurrentSession().createSQLQuery(sql);
		setObjectsToSql(q, objects);
		return q.executeUpdate();
	}
 
	@Override
	public Integer count(String sql, Object... objects) {
		SQLQuery q = getCurrentSession().createSQLQuery(SqlUtil.makeSqlCount(sql));
		setObjectsToSql(q, objects);
		return ((Number) q.uniqueResult()).intValue();
	}
	
	public List<String> getColumnsByTableName(String tableName){
		String sql = SqlUtil.makeSqlColumn(getDs(), tableName);
		List<Map<String, Object>> list = this.find(sql);
		List<String> res = null;
		//[
		//		{id : 1, name : n1}
		//		{id : 2, name : n2}
		//]
		// -> [1, 2]
		List<List<String>> listValue = MapListUtil.toArrayAndTurn(list);
		if (list.size() > 0) {
			res = listValue.get(0);
		} else {
			res = new ArrayList<String>();
		}
//		for(Map<String, Object> map : list){
//			res.add(String.valueOf(map.get("COLUMN_NAME")));
//		}
		
		return res;
	}

	
	public void setObjectsToSql(SQLQuery q, Object...objects){
		if (objects != null &&  objects.length > 0) {
			for (Integer i = 0; i < objects.length; i++) {
				q.setParameter(i+1, objects[i]);
			}
		}
		out(SqlUtil.makeSql(q.getQueryString(), objects)); 
	}

	@Override
	public List<Map<String, Object>> findPage(Page page, String sql, Object... objects) {
		page.setNUM(this.count(sql, objects ));
		sql = SqlUtil.makeSqlOrder(sql, page.getORDER());
		return this.findPage(sql,page.getNOWPAGE(), page.getSHOWNUM(), objects );
	}

	@Override
	public Integer[] executeSql(String sql, List<List<Object>> objs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer executeProc(String proc, Object... objects) {
		// TODO Auto-generated method stub
		return 0;
	}

}
