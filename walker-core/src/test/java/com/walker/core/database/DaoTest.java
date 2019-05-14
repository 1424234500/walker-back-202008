package com.walker.core.database;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.mysql.jdbc.PingTarget;
import com.walker.common.util.Tools;
import com.walker.core.encode.Pinyin;

public class DaoTest {
	BaseDao dao = new Dao();

	@Test
	public void testFind() {
		Tools.out(dao.executeSql("CREATE TABLE  IF NOT EXISTS  junit (code VARCHAR(20), name VARCHAR(20)); "));
		Tools.out(dao.getColumns("junit"));
		Tools.out(dao.count("select * from junit"));
		for(int i = 0; i < 5; i++)
			Tools.out(dao.executeSql("insert into junit values(?,?)", Tools.getRandomNum(0, 999, 4), Pinyin.getRandomName(1, null)));
		Tools.out(dao.count("select * from junit"));
		Tools.out(dao.find("select * from junit"));
		Tools.out(dao.findPage("select * from junit", 1, 2));
		Tools.out(dao.findPage("select * from junit", 2, 2));
		
		Tools.out(dao.executeSql("delete from junit  where code like ?", "%04%"));
		Tools.out(dao.executeSql("delete from junit  where code like ?", "%03%"));
		Tools.out(dao.executeSql("delete from junit  where code like ?", "%02%"));
		Tools.out(dao.executeSql("delete from junit  where code like ?", "%01%"));
		Tools.out(dao.count("select * from junit"));
		Tools.out(dao.find("select * from junit"));
		
	}

	
	

}
