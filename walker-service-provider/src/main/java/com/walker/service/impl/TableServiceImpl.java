package com.walker.service.impl;

import com.walker.common.util.Bean;
import com.walker.dao.JdbcDao;
import com.walker.service.EchoService;
import com.walker.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;


@org.springframework.stereotype.Service
@com.alibaba.dubbo.config.annotation.Service
@Transactional
//@Scope("prototype")
public class TableServiceImpl implements TableService,Serializable {
	private static final long serialVersionUID = 8304941820771045214L;

	@Autowired
	JdbcDao jdbcDao;
	@Override
	public Bean getTableColsMap(String tableName) {


		return null;
	}
}