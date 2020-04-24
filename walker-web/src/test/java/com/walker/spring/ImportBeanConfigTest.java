package com.walker.spring;

import com.walker.ApplicationTests;
import com.walker.common.util.Page;
import com.walker.common.util.Tools;
import com.walker.core.annotation.WalkerJdbcFactory;
import com.walker.core.database.Dao;
import com.walker.mapper.StudentWalkerJdbc;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ImportBeanConfigTest extends ApplicationTests {

    @Autowired //("studentWalkerJdbc")
    StudentWalkerJdbc studentWalkerJdbc;

    @Test
    public void registerBeanDefinitions() {
//        com.walker.core.database.walkerjdbc.StudentWalkerJdbc studentWalkerJdbc = WalkerJdbcFactory
//                .getInstance(com.walker.core.database.walkerjdbc.StudentWalkerJdbc.class, new Dao().setDs("mysql"));
        Tools.out(studentWalkerJdbc);
        Tools.out(studentWalkerJdbc.getClass());

        Tools.out(studentWalkerJdbc.updateNameById("2", "1"));

        Page page = new Page().setShownum(5).setNowpage(1);
        Tools.out(page);
        Tools.formatOut(studentWalkerJdbc.find(page, "%%"));
        Tools.out(page);

    }
}