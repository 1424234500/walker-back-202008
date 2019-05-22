package com.walker.web.event.start;

import org.springframework.beans.factory.annotation.Autowired;

import com.walker.core.aop.TestAdapter;
import com.walker.core.cache.CacheMgr;
import com.walker.core.database.Dao;
import com.walker.core.database.SqlUtil;
import com.walker.web.controller.Context;
import com.walker.web.dao.hibernate.BaseDao;

public class StartTable extends TestAdapter{

	@Override
	public boolean doTest() {
		return super.doTest();
	}
    @Autowired
    private BaseDao baseDao;
	@Override
	public boolean doInit() {
		String path = (Context.getPathRoot("sql/table_" + CacheMgr.getInstance().get("jdbcdefault", "mysql") + ".sql"));
		SqlUtil.executeSqlFile(baseDao, path);
		return super.doInit();
	}

	@Override
	public boolean doUninit() {
		return super.doUninit();
	}

	
}
