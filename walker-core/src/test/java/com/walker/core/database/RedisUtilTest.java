package com.walker.core.database;

import com.walker.box.TaskThreadPie;
import com.walker.common.util.ThreadUtil;
import com.walker.common.util.Tools;
import com.walker.core.aop.FunArgsReturn;
import com.walker.core.mode.Emp;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RedisUtilTest {
    @Test
    public void tryLock() {
        List<String> ok = new ArrayList<>();
        String key = "kkkk", key1 = "kk";
        String v = "", v1 = "", v2 = "";
        RedisUtil.releaseLock(key1, v1);
        v1 = RedisUtil.tryLock(key1, 500000, 10000);
        if(v1.length() > 0){
            RedisUtil.releaseLock(key1, v1);
        }
        v1 = RedisUtil.tryLock(key1, 5000, 0);

        new TaskThreadPie(100) {
            @Override
            public void onStartThread(int threadNo) throws IOException, Exception {//抢占资源后 加锁 占用1s
                String val = "";
                try {
                    val = RedisUtil.tryLock(key, 5000, 500);//可以等待1s获取锁
                    if(val.length() > 0){
                        Tools.out("t" + threadNo + " getLock ok !!!!! " + key + " " + val);
                        ok.add(threadNo + "");
                    }

                    ThreadUtil.sleep(val.length() > 0 ? 1000 : 100);    //有锁耗时大
                }finally {
                    if(val.length() > 0)
                        RedisUtil.releaseLock(key, val);
                }
            }
        }.setThreadSize(10).start();

        Tools.out(ok.size(), ok);

    }



    @Test
    public void setDbAndClearCache() {
        String key = "i";
        List<String> fromdb = new ArrayList<>();
        new TaskThreadPie(1000){

            @Override
            public void onStartThread(int threadNo) throws IOException, Exception {
                Object res = RedisUtil.getCacheOrDb(key, 5000, 20, new FunArgsReturn<String, String>() {
                    @Override
                    public String make(String obj) {
                        ThreadUtil.sleep(1000); //查询耗时1s
                        fromdb.add(threadNo + "");
                        Tools.out("-----------get from db thread:" + threadNo);
                        return "db res " + obj + " from thread:" + threadNo;
                    }
                });
                ThreadUtil.sleep(10);
                Tools.out("getCacheOrDb "+ res);
            }
        }.setThreadSize(10).start();

        Tools.out(fromdb.size(), fromdb);
        ThreadUtil.sleep(2000);
    }

    @Test
    public void get() {
        String key = "0";
        final Emp emp = new Emp().setName("name").setId("id");
        Tools.out( "aaaa", emp);

        Redis.doJedis(new Redis.Fun<Object>() {
            @Override
            public Object make(Jedis jedis) {
                RedisUtil.put(jedis, key, emp, 0L);
                return emp;
            }
        });
        Emp eemp = Redis.doJedis(new Redis.Fun<Emp>() {
            @Override
            public Emp make(Jedis jedis) {
                return RedisUtil.get(jedis, key, null);
            }
        });

        Tools.out( "aaaa", eemp);
    }
}