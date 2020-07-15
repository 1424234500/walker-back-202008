package com.walker.core.database;

import com.walker.common.util.LangUtil;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class LockerZookeeper implements Locker{
    private static Logger log = LoggerFactory.getLogger(LockerZookeeper.class);
    private static final String KEY_LOCK = "lock:zk:";
    private static final AtomicLong lockNo = new AtomicLong(0);

    ZooKeeper zookeeper;
    public LockerZookeeper(String host, Integer secondsTimeout){
        try {
            this.zookeeper = new ZooKeeper(host, secondsTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    log.info(watchedEvent.toString());
                }
            });
        } catch (IOException e) {
            log.error("zook:" + host + ",timeout:" + secondsTimeout + ",exception:" + e.getMessage(),  e);
        }
    }

    @Override
    public String tryLock(String lockName, Integer secondsToExpire, Integer secondsToWait) {
        String lockKey =  KEY_LOCK + lockName;
        String value = Thread.currentThread().getName() + ":" + LangUtil.getTimeSeqId() + ":" + lockNo; // Thread
        long startTime = System.currentTimeMillis();
        String result = "";
        int cc = 0;
        while(cc++ < 1000) {	//自循环等待 硬限制最多1000次
            try {
                if(this.zookeeper.exists(lockKey, false) != null) {
                    this.zookeeper.create(lockKey, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL.EPHEMERAL);
                    lockNo.addAndGet(1);
                    log.debug(Tools.objects2string("tryLock ok", cc, lockKey, lockName, value, result, secondsToExpire, secondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime)), lockNo);
                    return value;
                }
            } catch (Exception e) {
                log.error(Tools.objects2string("tryLock exception", cc, lockKey, lockName,value, result, secondsToExpire, secondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime), lockNo, e.getMessage()), e);
            }
            if(System.currentTimeMillis() > startTime + secondsToWait){
                log.warn(Tools.objects2string("tryLock error wait timeout", cc, lockKey, lockName,value, result, secondsToExpire, secondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime), "now locked" , lockNo));
                break;
            }
            try {//1000ms等待锁，共轮询10次
                TimeUnit.SECONDS.sleep(Math.max(secondsToWait/4, 10));
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        return "";
    }

    @Override
    public Boolean releaseLock(String lockName, String identifier) {
        String lockKey = KEY_LOCK + lockName;

        try {
            if(this.zookeeper.exists(lockKey, false) != null){
                byte[] v = this.zookeeper.getData(lockKey, false, null);
                if (v.equals(identifier.getBytes())) {
                    this.zookeeper.delete(lockKey, -1);
                    log.debug(Tools.objects2string("release lock ok", lockKey, lockName, identifier));
                    return true;
                }else{
                    log.error(Tools.objects2string("release lock error have timeout?", lockKey, lockName, identifier, "lockRes:" + false, "but identifier should be " + new String(v)));
                }
            }
        }catch (Exception e){
            log.error(Tools.objects2string("release lock exception", lockKey, lockName, identifier, e.getMessage()), e);
        }
        return false;

    }
}
