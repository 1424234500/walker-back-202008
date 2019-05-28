package com.walker.web.event.start;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;

import com.walker.common.util.FileUtil;
import com.walker.core.aop.Fun;
import com.walker.core.aop.TestAdapter;
import com.walker.core.cache.CacheMgr;
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
		String path = Context.getPathRoot("sql/" + CacheMgr.getInstance().get("jdbcdefault", "mysql"));
		FileUtil.showDir(path, new Fun<File>() {
			@Override
			public <T> T make(File obj) {
				if(obj.isFile()) {
					SqlUtil.executeSqlFile(baseDao, obj.getAbsolutePath());
				}
				return null;
			}
		});
		return super.doInit();
	}

	@Override
	public boolean doUninit() {
		return super.doUninit();
	}

	
}
