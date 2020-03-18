package com.walker.core.database;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mysql.jdbc.PingTarget;
import com.walker.common.util.Tools;
import com.walker.core.encode.Pinyin;

public class DaoTest {
	@Test
	public void testGetColumnMap(){
		Tools.out(new Dao().getDatabasesOrUsers());
		Tools.out(new Dao().getTables(""));
		Tools.out(new Dao().getTables("walker"));

		Tools.out(new Dao().getColumnsByTableName("", "W_TEACHER"));
		Tools.out(new Dao().getColumnsByTableName("walker", "W_TEACHER"));

		Tools.out(new Dao().getColumnsMapByTableName("", "W_TEACHER"));
		Tools.out(new Dao().getColumnsMapByTableName("walker", "W_TEACHER"));

		Tools.out(new Dao().findPageRand(3, "select * from W_AREA"));



	}
	@Test
	public void testPo(){
		BaseDao  dao = new Dao();
		List<Object> list = new ArrayList<>();
//		list.add(new String[]{"0", "3"});
		list.add("0");
		list.add("3");
		Tools.out(dao.findPage("select * from TEACHER where ID in (?, ?) ", 1, 10,  list.toArray()));

		list.clear();
		list.add("ID ");
		Tools.out(dao.findPage("select * from TEACHER where 1=1 order by ? ", 1, 10,  list.toArray()));


	}
	@Test
	public void testEF() {
		BaseDao dao = new Dao();

		Tools.out(dao.executeSql("CREATE TABLE  IF NOT EXISTS  junit (code VARCHAR(20), name VARCHAR(20)); "));
		Tools.out(dao.getColumnsByTableName("", "junit"));
		Tools.out(dao.getColumnsBySql("select t.*, 'addCol' from junit t "));
		for(int i = 0; i < 3; i++)
			Tools.out(dao.executeSql("insert into junit values(?,?)", i , Pinyin.getRandomName(1, null)));
		Tools.out(dao.count("select * from junit"));
		Tools.out(dao.find("select * from junit"));
		Tools.out(dao.findPage("select * from junit", 1, 3));
		Tools.out(dao.findPage("select * from junit", 2, 2));
		
		Tools.out(dao.executeSql("delete from junit  where code like ?", "%1%"));
		Tools.out(dao.executeSql("delete from junit  where code like ?", "%2%"));
		Tools.out(dao.executeSql("drop table junit"));

	}

	

}
