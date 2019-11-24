package com.walker.common.util;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ClassUtilTest {

    @Test
    public void getPackageClassBean() {
        List<?> list = ClassUtil.getPackageClassBean("", true);
        Tools.formatOut(list);
    }

    @Test
    public void getPackageClass() {
        List<?> list = ClassUtil.getPackageClass("", true);
        Tools.formatOut(list);


    }
}