package com.walker.core.database;

import static org.junit.Assert.*;

import com.walker.common.util.Bean;
import com.walker.common.util.Tools;
import org.junit.Test;

import com.walker.common.util.Context;
import com.walker.core.cache.CacheMgr;

import java.util.HashMap;
import java.util.Map;

public class SqlUtilTest {

	@Test
	public void test() {
	
	}

    @Test
    public void makeSqlCreate() {
	    Map bean = new Bean().set("id", "1234").set("name", "2323");
        Tools.out(SqlUtil.makeSqlCreate("hell", bean));
        Tools.out(SqlUtil.makeSqlInsert("hell", bean.keySet()));
    }
}
