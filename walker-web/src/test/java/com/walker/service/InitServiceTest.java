package com.walker.service;

import com.alibaba.fastjson.JSON;
import com.walker.common.util.JsonFastUtil;
import com.walker.common.util.Tools;
import org.junit.Test;

import static org.junit.Assert.*;

public class InitServiceTest extends InitService{

    @Test
    public void getTest() {
        Object s = getCityRoot();
        String res = JsonFastUtil.toStringFormat(s);
        Tools.out(res);

    }
    @Test
    public void testZave(){
        updateAreaGov();
    }
}