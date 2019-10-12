package com.walker.service.impl;

import com.walker.ApplicationProviderTests;
import com.walker.service.BaseService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class BaseServiceImplTest extends ApplicationProviderTests {
    @Autowired
    BaseService baseService;
//sharding 拦截了 INFORMATION_SCHEMA表?
//    @Test
//    public void getColumnsByTableName() {
//        out(baseService.getColumnsByTableName("TEACHER"));
//    }
//    @Test
//    public void getColumnsMapByTableName() {
//        out(baseService.getColumnsMapByTableName("USER"));
//    }
    @Test
    public void getColumnsBySql() {
        out(baseService.getColumnsBySql("select 'a' a, 'b' b from dual "));
    }
}