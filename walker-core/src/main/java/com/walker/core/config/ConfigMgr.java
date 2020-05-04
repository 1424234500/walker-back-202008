package com.walker.core.config;

import com.walker.common.util.Bean;
import com.walker.common.util.LangUtil;
import com.walker.common.util.TimeUtil;
import com.walker.core.aop.FunArgsReturn;
import com.walker.core.database.Dao;
import com.walker.core.database.RedisUtil;
import com.walker.core.database.SqlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * 配置抽象
 *
 *  ID VALUE   ABOUT   S_FLAG  S_MTIME
 *
 */
public class ConfigMgr {
    private static Logger log = LoggerFactory.getLogger(ConfigMgr.class);

    private ConfigMgr() {
    }
    private static class SingletonFactory{
        private static ConfigMgr instanse;
        static {
            log.warn("singleton instance construct " + SingletonFactory.class);
            instanse = new ConfigMgr();
            instanse.reload();
        }
    }

    public static ConfigMgr getInstance() {
        return SingletonFactory.instanse;
    }


    String CONF_ID = "config";


    /**
     * 从数据库初始化 预热
     * 避免集体失效 分布过期时间
     * @return
     */
    public int reload(){
        int res = -1;
        res = RedisUtil.initCacheFromDb(CONF_ID, 24 * 3600, 30, 10 * 60, new FunArgsReturn<String, Map<String, Object>>() {
            @Override
            public Map<String, Object> make(String obj) {
                Map<String, Object> res = new LinkedHashMap<>();

                List<Map<String, Object>> line = new Dao().find("select ID, VALUE from W_SYS_CONFIG where S_FLAG='1' order by S_MTIME ");
                for(Map<String, Object> item : line){
                    log.debug(item.toString());
                    res.put(String.valueOf(item.get("ID")), item.get("VALUE"));
                }
                log.info("load cache " + CONF_ID + " " + res );

                return res;
            }
        }).intValue();


        return res;
    }


    /**
     *
     * 穿透？  等待锁超时怎么办
     *
     * 并发访问 等待获取数据库 加锁 等待多一些时间
     *
     * 配置缓存10小时 或者永不过期？ 等待变化时通知清理缓存
     * 并发访问配置时 多等待充足时间 等待其他进程线程查询 10s
     *
     * @param key
     * @param defaultValue
     * @param <T>
     * @return
     */
    public <T> T get(String key, T defaultValue){

        T res = RedisUtil.getCacheOrDb(CONF_ID, key, 3600, 8, new FunArgsReturn<String, T>() {
            @Override
            public T make(String obj) {
                T v = defaultValue;
                Map<String, Object> line = new Dao().findOne("select VALUE from W_SYS_CONFIG where ID=? and S_FLAG='1' ", key);
                if(line != null){
                    Object vv = line.get(key);
                    v = LangUtil.turn(vv, defaultValue);
                }
                return v;
            }
        });

        return res;
    }



    public Integer set(String ID, String VALUE, String ABOUT, String S_FLAG, String S_MTIME){

        Integer res = RedisUtil.setDbAndClearCache(CONF_ID, ID, 3600,  new FunArgsReturn<String, Integer>() {
            @Override
            public Integer make(String obj) {
                Integer rr = 0;
                boolean isexist = false;
                Map<String, Object> line = new Dao().findOne("select * from W_SYS_CONFIG where ID=?  ", ID);
                if(line != null) {
                    isexist = true;
                }
                if(line == null){
                    line = new HashMap<>();
                }
                isexist = true;
                if(VALUE != null ){
                    line.put("VALUE", VALUE);
                }
                if(ABOUT != null ){
                    line.put("ABOUT", ABOUT);
                }
                if(S_FLAG != null ){
                    line.put("S_FLAG", S_FLAG);
                }
                if(S_MTIME != null ){
                    line.put("S_MTIME", S_MTIME);
                }else{
                    line.put("S_MTIME", TimeUtil.getTimeYmdHmss());
                }
                if(isexist){
                    rr = new Dao().executeSql("update W_SYS_CONFIG set ID=?,VALUE=?,ABOUT=?,S_FLAG=?,S_MTIME=? where ID=? ", line.get("ID"), line.get("VALUE"), line.get("ABOUT"), line.get("S_FLAG"), line.get("S_MTIME"), line.get("ID"));
                }else{
                    rr = new Dao().executeSql("insert into W_SYS_CONFIG values(" + SqlUtil.makePosition("?", line.size()) + ")", line.get("ID"), line.get("VALUE"), line.get("ABOUT"), line.get("S_FLAG"), line.get("S_MTIME") );
                }
                return  (rr);
            }
        });

        return res;
    }










}
