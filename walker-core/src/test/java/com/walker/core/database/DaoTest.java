package com.walker.core.database;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.mysql.jdbc.PingTarget;
import com.walker.common.util.Tools;
import com.walker.core.encode.Pinyin;

public class DaoTest {

	@Test
	public void testEF() {
		BaseDao dao = new Dao();

		Tools.out(dao.executeSql("CREATE TABLE  IF NOT EXISTS  junit (code VARCHAR(20), name VARCHAR(20)); "));
		Tools.out(dao.getColumnsByTableName("junit"));
		Tools.out(dao.getColumnsBySql("select t.*, 'addCol' from junit t "));
		for(int i = 0; i < 3; i++)
			Tools.out(dao.executeSql("insert into junit values(?,?)", i , Pinyin.getRandomName(1, null)));
		Tools.out(dao.count("select * from junit"));
		Tools.out(dao.find("select * from junit"));
		Tools.out(dao.findPage("select * from junit", 1, 3));
		Tools.out(dao.findPage("select * from junit", 2, 2));
		
		Tools.out(dao.executeSql("delete from junit  where code like ?", "%1%"));
		Tools.out(dao.executeSql("delete from junit  where code like ?", "%2%"));
		
	}

	

}
