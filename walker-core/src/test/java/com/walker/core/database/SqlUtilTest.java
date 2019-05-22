package com.walker.core.database;

import static org.junit.Assert.*;

import org.junit.Test;

import com.walker.common.util.Context;
import com.walker.core.cache.CacheMgr;

public class SqlUtilTest {

	@Test
	public void test() {

		String path = (Context.getPathRoot("sql/table_" + CacheMgr.getInstance().get("jdbcdefault", "mysql") + ".sql"));
		SqlUtil.executeSqlFile(new Dao(), path);
	
	}

}
