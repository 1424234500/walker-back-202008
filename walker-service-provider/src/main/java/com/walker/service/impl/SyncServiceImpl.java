package com.walker.service.impl;


import com.walker.common.util.*;
import com.walker.core.database.RedisUtil;
import com.walker.dao.RedisDao;
import com.walker.mode.Area;
import com.walker.service.AreaService;
import com.walker.service.Config;
import com.walker.service.SyncAreaService;
import com.walker.service.SyncService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 初始化数据服务 调度器
 */
@Service("syncService")
public class SyncServiceImpl implements SyncService {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    SyncAreaService syncAreaService;
    @Autowired
    RedisDao redisDao;

    @Override
    public Bean syncArea(Bean args) {
        Bean res = new Bean().set("TIME", TimeUtil.getTimeYmdHmss());
        String key = getClass() + ".syncArea";
        String value = redisDao.tryLock(key, 3600 * 10);
        res.set("value", value);
        if(value != null && value.length() > 0){
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    syncAreaService.syncArea();
                    if(! redisDao.releaseLock(key, value)){
                         log.error("unlock error ? " + redisDao.get(key) + " no my value:" + value);
                    }
                }
            });
            res.set("info", "bean sync");
        }else{
            res.set("info", "redis lock error, have being syncing ? ");
        }
        return res;

    }

    @Override
    public Bean syncDept(Bean args) {
        return new Bean().set("TIME", TimeUtil.getTimeYmdHmss());
    }

    @Override
    public Bean syncUser(Bean args) {
        return new Bean().set("TIME", TimeUtil.getTimeYmdHmss());
    }
}
