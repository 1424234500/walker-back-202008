package com.walker.dao;

import com.walker.ApplicationProviderTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class RedisDaoTest extends ApplicationProviderTests {
    @Autowired
    RedisDao redisDao;

    @Test
    public void tryLock() {
        String key = "kkkk", key1 = "kk";
        String v = "", v1 = "", v2 = "";
        v = redisDao.tryLock(key, 4);
        out(key, v, redisDao.releaseLock(key, v));

        v = redisDao.tryLock(key, 4);
        v1 = redisDao.tryLock(key1, 4);

        v2 = redisDao.tryLock(key, 4);

        out(key, v1, redisDao.releaseLock(key, v1));

        out(key, v, redisDao.releaseLock(key, v));
        out(key1, v1, redisDao.releaseLock(key1, v1));


    }

    @Test
    public void releaseLock() {
    }

    @Test
    public void isLocked() {
    }
}