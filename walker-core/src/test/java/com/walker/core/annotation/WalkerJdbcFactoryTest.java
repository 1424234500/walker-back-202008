package com.walker.core.annotation;

import com.sun.org.apache.xpath.internal.axes.WalkerFactory;
import com.walker.common.util.Page;
import com.walker.common.util.Tools;
import com.walker.core.database.Dao;
import com.walker.core.database.walkerjdbc.StudentWalkerJdbc;
import org.junit.Test;

import static org.junit.Assert.*;

public class WalkerJdbcFactoryTest {

    @Test
    public void getInstance() {

        StudentWalkerJdbc studentWalkerJdbc = WalkerJdbcFactory
                .getInstance(StudentWalkerJdbc.class, new Dao().setDs("mysql"));
        Tools.out(studentWalkerJdbc.updateNameById("2", "1"));

        Page page = new Page().setShownum(5).setNowpage(1);
        Tools.out(page);
        Tools.formatOut(studentWalkerJdbc.find(page, "%%"));
        Tools.out(page);

    }
}