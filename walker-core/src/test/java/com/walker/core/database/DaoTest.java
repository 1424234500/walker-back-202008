package com.walker.core.database;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.walker.common.util.Tools;

public class DaoTest {
 
	@Test
	public void testFind() {
		BaseDao dao = new Dao();

		List list = dao.find("select * from test");
		Tools.out(list);
		 list = dao.findPage("select * from test", 1, 10);
		Tools.out(list);
		assertTrue(list.size() > 0);
	}

}
