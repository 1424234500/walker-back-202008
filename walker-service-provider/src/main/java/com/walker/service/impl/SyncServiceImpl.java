package com.walker.service.impl;


import com.walker.common.util.*;
import com.walker.config.MakeConfig;
import com.walker.core.database.RedisUtil;
import com.walker.core.encode.Pinyin;
import com.walker.dao.RedisDao;
import com.walker.mode.Area;
import com.walker.mode.Dept;
import com.walker.mode.Key;
import com.walker.mode.User;
import com.walker.service.*;
import io.swagger.models.auth.In;
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
import java.util.Random;

/**
 * 初始化数据服务 调度器
 */
@Service("syncService")
public class SyncServiceImpl implements SyncService {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    SyncAreaService syncAreaService;


    @Autowired
    UserService userService;
    @Autowired
    DeptService deptService;
    @Autowired
    AreaService areaService;
    @Autowired
    RoleService roleService;


    @Autowired
    RedisDao redisDao;
    @Autowired
    MakeConfig makeConfig;

    @Override
    public Bean syncArea(Bean args) {
        Bean res = new Bean().set("TIME", TimeUtil.getTimeYmdHmss());
        String key = Key.getLockRedis(getClass().getName() + ".syncArea");
        String value = redisDao.tryLock(key, makeConfig.expireLockRedisSyncArea);
        res.set("KEY", key);
        res.set("VALUE", value);
        if(value != null && value.length() > 0){
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        syncAreaService.syncArea();
                    }finally {
                        if(! redisDao.releaseLock(key, value)){
                            log.error("unlock error expire ? key:" + key + ", value:" + redisDao.get(key) + " should be value:" + value);
                        }
                    }
                }
            });
            res.set("INFO", "ok, sync in thread ");
            log.info("have being running " + res + " args:" + args);
        }else{
            res.set("INFO", "waring, redis lock error, have being syncing ? ");
            log.warn("have being locked " + res + " args:" + args);
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

    /**
     * 造数
     * @param args
     * @return
     */
    @Override
    public Bean makeUser(Bean args) {
        Bean res = new Bean().set("TIME", TimeUtil.getTimeYmdHmss());
        String key = Key.getLockRedis(getClass().getName() + ".makeUser");
        String value = redisDao.tryLock(key, makeConfig.expireLockRedisSyncArea);
        res.set("KEY", key);
        res.set("VALUE", value);
        if(value != null && value.length() > 0){
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    Watch watch = new Watch(key);

                    try {
                        /**
                         * 造用户数据
                         * 5线程 共计造1000用户
                         */

                        int countMakeUser = makeConfig.countMakeUser;
                        List<User> list = new ArrayList<>();
                        long day = Integer.valueOf(TimeUtil.getTime(TimeUtil.ymd1)) * countMakeUser;//20200215
                        String time = TimeUtil.getTimeYmdHms();

                        Dept dept = new Dept().setS_FLAG(Config.TRUE);
                        int needDeptSize = 10;
                        int deptSize = deptService.count(dept);
                        int pageSize = (int) Math.ceil(1D * deptSize / needDeptSize);
                        List<Dept> listDepts = deptService.finds(
                                dept , new Page().setShownum(needDeptSize).setPagenum((int)(Math.random() * pageSize))
                        );
                        listDepts = listDepts == null || listDepts.size() == 0 ? Arrays.asList(new Dept().setID("0000000")): listDepts;
                        watch.cost("findDept", listDepts.size());

                        Area area = new Area().setS_FLAG(Config.TRUE);
                        int needAreaSize = 10;
                        int areaSize = areaService.count(area);
                        int pageSizeArea = (int) Math.ceil(1D * areaSize / needAreaSize);
                        List<Area> listAreas = areaService.finds(
                                area, new Page().setShownum(needAreaSize).setPagenum((int)(Math.random() * pageSizeArea))
                        );
                        listAreas = listAreas == null || listAreas.size() == 0 ? Arrays.asList(new Area().setID("0000000")): listAreas;
                        watch.cost("findArea", listAreas.size());

                        for(int i = 0; i < countMakeUser; i++){
                            String id = (day + i) + "";
                            String deptId = listDepts.get((int)(Math.random() * listDepts.size())).getID();
                            String areaId = listAreas.get((int)(Math.random() * listAreas.size())).getID();
                            String name= Pinyin.getRandomName(1, null);

                            User user = new User()
                                    .setID(id)
                                    .setDEPT_ID(deptId)
                                    .setAREA_ID(areaId)
                                    .setNAME(name)
                                    .setSIGN(name + "'s " +  Pinyin.getRandomName(10, 1, null))
                                    .setS_ATIME(time)
                                    .setS_MTIME(time)
                                    .setS_FLAG(Config.TRUE)
                                    .setSEX(  Math.random() * 10 > 5 ? Config.TRUE : Config.FALSE )
                                    ;
                            list.add(user);
                            if(list.size() > Context.getDbOnce() - 1){
                                userService.saveAll(list);
                                list.clear();
                            }

                        }
                        if(list.size() > 0){
                            userService.saveAll(list);
                            list.clear();
                        }
                        watch.cost("makeData", countMakeUser);



                    }finally {
                        watch.res();
                        log.info(watch.toPrettyString());

                        if(! redisDao.releaseLock(key, value)){
                            log.error("unlock error expire ? key:" + key + ", value:" + redisDao.get(key) + " should be value:" + value);
                        }
                    }
                }
            });
            res.set("INFO", "ok, sync in thread ");
            log.info("have being running " + res + " args:" + args);
        }else{
            res.set("INFO", "waring, redis lock error, have being syncing ? ");
            log.warn("have being locked " + res + " args:" + args);
        }
        return res;
    }

    @Override
    public Bean makeDept(Bean args) {
        return new Bean().set("TIME", TimeUtil.getTimeYmdHmss());
    }

    @Override
    public Bean makeRole(Bean args) {
        return new Bean().set("TIME", TimeUtil.getTimeYmdHmss());
    }


}
