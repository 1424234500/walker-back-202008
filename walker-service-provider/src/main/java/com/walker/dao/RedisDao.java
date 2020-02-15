package com.walker.dao;
import com.walker.common.util.LangUtil;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.async.RedisScriptingAsyncCommands;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * 常用redis操作 template实现
 */
@Repository
public class RedisDao {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisTemplate redisTemplate;

    final static String UNLOCK_LUA = "" +
            "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
            "then " +
            "    return redis.call(\"del\",KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end ";

    /**
     * 尝试Lock 成功后返回密钥
     * @param key
     * @param expireSeconds
     * @return
     */
    @SuppressWarnings("unchecked")
    public String tryLock(String key, long expireSeconds) {
        String value = "lock:redis:value:" + LangUtil.getGenerateId();
        byte[] keyByte = key.getBytes(StandardCharsets.UTF_8);
        byte[] valueByte = value.getBytes(StandardCharsets.UTF_8);

        Object result = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                try{
                    Object nativeConnection = connection.getNativeConnection();

                    String resultString = "";
                    if(nativeConnection instanceof RedisAsyncCommands){
                        RedisAsyncCommands command = (RedisAsyncCommands) nativeConnection;
                        resultString = command
                                .getStatefulConnection()
                                .sync()
                                .set(keyByte, valueByte, SetArgs.Builder.nx().ex(expireSeconds));
                    }else if(nativeConnection instanceof RedisAdvancedClusterAsyncCommands){
                        RedisAdvancedClusterAsyncCommands clusterAsyncCommands = (RedisAdvancedClusterAsyncCommands) nativeConnection;
                        resultString = clusterAsyncCommands
                                .getStatefulConnection()
                                .sync()
                                .set(keyByte, keyByte, SetArgs.Builder.nx().ex(expireSeconds));
                    }
                    return resultString;
                }finally {
                    connection.close();
                }
            }
        });
        log.info("tryLock " + key + " " + expireSeconds + " result:" + result);
        boolean eq = "OK".equals(result);
        if(eq) {
            return value;
        }
        return "";
    }

    /**
     * 释放锁
     * 有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
     * 使用lua脚本删除redis中匹配value的key
     * @param key
     * @param value 密钥
     * @return false:   锁已不属于当前线程  或者 锁已超时
     */
    @SuppressWarnings("unchecked")
    public boolean releaseLock(String key, String value) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = value.getBytes(StandardCharsets.UTF_8);
        Object[] keyParam = new Object[]{keyBytes};

        Object result = redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                try{
                    Object nativeConnection = connection.getNativeConnection();
                    if (nativeConnection instanceof RedisScriptingAsyncCommands) {
                        /**
                         * 不要问我为什么这里的参数这么奇怪
                         */
                        RedisScriptingAsyncCommands<Object,byte[]> command = (RedisScriptingAsyncCommands<Object,byte[]>) nativeConnection;
                        RedisFuture future = command.eval(UNLOCK_LUA, ScriptOutputType.INTEGER, keyParam, valueBytes);
                        try {
                            return (Long)future.get();
                        } catch (InterruptedException e) {
                            return -1L;
                        } catch (ExecutionException e) {
                            return -2L;
                        }
                    }
                    return 0L;
                }finally {
                    connection.close();
                }
            }
        });
        log.info("releaseLock " + key + " " + value + " result:" + result);

        return result != null && (Long)result > 0;
    }

    /**
     * 查看是否加锁
     * @param key
     * @return
     */
    public boolean isLocked(String key) {
        Object o = redisTemplate.opsForValue().get(key);
        return o!=null;
    }

    /**
     * 写入缓存
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 写入缓存设置时效时间
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value, Long expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 写入时效时间 s
     * @param key
     * @return
     */
    public boolean expire(final String key, Long expireSeconds) {
        boolean result = false;
        try {
            redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 批量删除对应的value
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0)
            redisTemplate.delete(keys);
    }
    /**
     * 删除对应的value
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }
    /**
     * 判断缓存中是否有对应的value
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }
    /**
     * 读取缓存
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }
    /**
     * 读取缓存
     * @param key
     * @return
     */
    public <T> T getConfig(final String key, final T defaultValue) {
        T result = null;
        ValueOperations<Serializable, T> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        if(result != null)
            return result;
        else{
            return defaultValue;
        }
    }
    /**
     * 哈希 添加
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, Object hashKey, Object value){
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key,hashKey,value);
    }

    /**
     * 哈希获取数据
     * @param key
     * @param hashKey
     * @return
     */
    public Object hmGet(String key, Object hashKey){
        HashOperations<String, Object, Object>  hash = redisTemplate.opsForHash();
        return hash.get(key,hashKey);
    }

    /**
     * 列表添加
     * @param k
     * @param v
     */
    public void lPush(String k,Object v){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(k,v);
    }

    /**
     * 列表获取
     * @param k
     * @param l
     * @param l1
     * @return
     */
    public List<Object> lRange(String k, long l, long l1){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k,l,l1);
    }

    /**
     * 集合添加
     * @param key
     * @param value
     */
    public void add(String key,Object value){
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key,value);
    }

    /**
     * 集合获取
     * @param key
     * @return
     */
    public Set<Object> setMembers(String key){
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加
     * @param key
     * @param value
     * @param scoure
     */
    public <T> void zAdd(String key, T value,double scoure){
        ZSetOperations<String, T> zset = redisTemplate.opsForZSet();
        zset.add(key,value,scoure);
    }
    /**
     * score 不存在就会异常？
     * @param key
     * @param value
     */
    public <T> Double zScore(String key, T value){
        Double res = -1d;
        try {
            ZSetOperations<String, T> zset = redisTemplate.opsForZSet();
            res = zset.score(key, value);
            if(res == null ){
                res = -2d;
            }
        }catch (Exception e){
            res = -3d;
        }
        return res;
    }

    /**
     * 有序集合获取
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
    public Set<Object> rangeByScore(String key,double scoure,double scoure1){
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }
}