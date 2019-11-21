package com.walker.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.walker.common.util.Bean;
import com.walker.common.util.HttpBuilder;
import com.walker.common.util.Page;
import com.walker.common.util.ThreadUtil;
import com.walker.config.Config;
import com.walker.config.MakeConfig;
import com.walker.core.scheduler.Task;
import com.walker.dao.JdbcDao;
import com.walker.mode.Dept;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 缓存服务
 */
@Service("cacheService")
public class CacheService {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    JdbcDao jdbcDao;


    /**
     * 使用redis sb 缓存
     * @param dbOrUser
     * @param tableName
     * @return
     */
    @Cacheable(keyGenerator="keyGenerator",value="cache-getColsMapCache")
    public Map<String, String> getColsMapCache(String dbOrUser, String tableName){
        return jdbcDao.getColumnsMapByTableName(dbOrUser, tableName);
//        return CacheMgr.getInstance().getFun("jdbcDao.getColumnsMapByTableName:" + dbOrUser + ":" + tableName, new Cache.Function() {
//            @Override
//            public Map<String, String> cache() {
//                return jdbcDao.getColumnsMapByTableName(dbOrUser, tableName);
//            }
//        });
    }
    @Cacheable(keyGenerator="keyGenerator",value="cache-getColumnsByTableName")
    public List<String> getColsCache(String dbOrUser, String tableName){
        return jdbcDao.getColumnsByTableName(dbOrUser, tableName);
//        return CacheMgr.getInstance().getFun("jdbcDao.getColumnsByTableName:" + dbOrUser + ":" + tableName, new Cache.Function() {
//            @Override
//            public List<String> cache() {
//                return jdbcDao.getColumnsByTableName(dbOrUser, tableName);
//            }
//        });
    }

}
