package com.walker.core.database;

import com.walker.box.TaskThreadPie;
import com.walker.common.util.ThreadUtil;
import com.walker.common.util.Tools;
import com.walker.core.aop.FunArgsReturn;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;

import static org.junit.Assert.*;

public class RedisUtilTest {
    @Test
    public void tryLock() {
        String key = "kkkk", key1 = "kk";
        String v = "", v1 = "", v2 = "";
        RedisUtil.releaseLock(key1, v1);
        v1 = RedisUtil.tryLock(key1, 5000, 1000);
        if(v1.length() > 0){
            RedisUtil.releaseLock(key1, v1);
        }
        v1 = RedisUtil.tryLock(key1, 5000, 1000);

        new TaskThreadPie(100) {
            @Override
            public void onStartThread(int threadNo) throws IOException, Exception {//抢占资源后 加锁 占用1s
                String val = "";
                try {
                    val = RedisUtil.tryLock(key, 5000, 1000);//可以等待1s获取锁
                    if(val.length() > 0){
                        Tools.out("t" + threadNo + " getLock ok !!!!! " + key + " " + val);
                    }

                    ThreadUtil.sleep(val.length() > 0 ? 3000 : 500);    //有锁耗时大
                }finally {
                    if(val.length() > 0)
                        RedisUtil.releaseLock(key, val);
                }
            }
        }.setThreadSize(10).start();



    }



    @Test
    public void setDbAndClearCache() {
        String key = "i";
//        ThreadUtil.execute(new Runnable() {
//            @Override
//            public void run() {

                new TaskThreadPie(100){

                    @Override
                    public void onStartThread(int threadNo) throws IOException, Exception {
                        Object res = RedisUtil.getCacheOrDb(key, 10000, 1000, new FunArgsReturn<String, String>() {
                            @Override
                            public String make(String obj) {
                                ThreadUtil.sleep(1000); //查询耗时1s
                                Tools.out("get from db " + threadNo);
                                return obj + " res " + threadNo;
                            }
                        });
                        Tools.out("getCacheOrDb "+ res);
                    }
                }.setThreadSize(10).start();

//            }
//        });
        ThreadUtil.sleep(2000);
//        ThreadUtil.execute(new Runnable() {
//            @Override
//            public void run() {

                new TaskThreadPie(2){

                    @Override
                    public void onStartThread(int threadNo) throws IOException, Exception {
                        RedisUtil.setDbAndClearCache(key, 1000, new FunArgsReturn<String, String>() {
                            @Override
                            public String make(String obj) {
                                ThreadUtil.sleep(2000);//写入耗时2s
                                Tools.out("write to db " + threadNo);
                                return obj + " res " + threadNo;
                            }
                        });
                    }
                }.setThreadSize(100).start();

//            }
//        });

        new TaskThreadPie(100){

            @Override
            public void onStartThread(int threadNo) throws IOException, Exception {
                Object res = RedisUtil.getCacheOrDb(key, 10000, 1000, new FunArgsReturn<String, String>() {
                    @Override
                    public String make(String obj) {
                        ThreadUtil.sleep(1000); //查询耗时1s
                        Tools.out("get from db " + threadNo);
                        return obj + " res " + threadNo;
                    }
                });
                Tools.out("getCacheOrDb "+ res);
            }
        }.setThreadSize(10).start();

//        ThreadUtil.sleep(60*1000);
    }

}