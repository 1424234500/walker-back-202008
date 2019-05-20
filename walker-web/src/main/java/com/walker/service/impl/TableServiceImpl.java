package com.walker.service.impl;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.walker.common.util.Bean;
import com.walker.common.util.FileUtil;
import com.walker.common.util.MD5;
import com.walker.common.util.MapListUtil;
import com.walker.common.util.Tools;
import com.walker.core.aop.Fun;
import com.walker.core.cache.Cache;
import com.walker.core.cache.CacheMgr;
import com.walker.core.database.SqlHelp;
import com.walker.service.LogService;
import com.walker.service.LoginService;
import com.walker.service.TableService;
import com.walker.web.controller.Context;
import com.walker.web.dao.Redis;
import com.walker.web.dao.hibernate.BaseDao;
import com.walker.web.mode.LoginUser;

@Service("loginService")
@Scope("prototype") 
public class TableServiceImpl implements TableService,Serializable {
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


    



}