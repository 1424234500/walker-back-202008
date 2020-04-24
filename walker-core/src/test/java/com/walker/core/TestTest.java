package com.walker.core;

import com.walker.common.util.Tools;
import com.walker.core.database.walkerjdbc.StudentWalkerJdbc;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestTest {

    @Test
    public void testClass() {
        Class<?> clz = StudentWalkerJdbc.class;

        Tools.out("clz.getName()", clz.getName());
        Tools.out("clz.getSimpleName()", clz.getSimpleName());
        Tools.out("clz.getCanonicalName()", clz.getCanonicalName());


    }
}