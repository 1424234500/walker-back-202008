package com.walker.service.impl;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.walker.common.util.FileUtil;
import com.walker.core.aop.Fun;
import com.walker.core.aop.TestAdapter;
import com.walker.core.cache.CacheMgr;
import com.walker.core.database.SqlHelp;
import com.walker.service.TableService;
import com.walker.web.controller.Context;
import com.walker.web.dao.hibernate.BaseDao;

@Service("tableService")
@Scope("prototype") 
public class TableServiceImpl extends TestAdapter implements TableService,Serializable {
	private static Logger log = Logger.getLogger(TableServiceImpl.class);

	private static final long serialVersionUID = 8304941820771045214L;
    @Autowired
    private BaseDao baseDao;
    
    
	@Override
	public void initTable() {
		String path = (Context.getPathRoot("sql/table_" + CacheMgr.getInstance().get("jdbcdefault", "mysql") + ".sql"));
		FileUtil.readByLines(path, new Fun<String>() {
			@Override
			public <T> T make(String obj) {
				try {
					obj = StringUtils.strip(obj);
					obj = SqlHelp.filter(obj);
					if(!obj.startsWith("--")) {
						log.info(obj);
						baseDao.executeSql(obj);
					}
				}catch(Exception e) {
					log.error(e);
				}
				return null;
			}
		}, null);
		
		
	}    

public boolean doInit() {
	initTable();
	return true;
}
    



}