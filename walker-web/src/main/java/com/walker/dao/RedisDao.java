package com.walker.dao;
import com.walker.common.util.LangUtil;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.mode.Key;
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
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
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
     * 尝试Lock 成功后返回密钥\
     *
     *
     * 1/ 非重入，等待锁时使用线程sleep
     *
     * 2/使用  redis的  SETNX   带过期时间的方法
     *
     * 3/使用ThreadLocal保存锁的值，在锁超时时，防止删除其他线程的锁，使用lua 脚本保证原子性；
     *
     * @param lockName
     * @param millisecondsToExpire
     * @return
     */
    public String tryLock(String lockName, long millisecondsToExpire, long millisecondsToWait) {

        String lockKey = Key.getLockRedis(lockName);
        String value = Thread.currentThread().getName() + ":" + LangUtil.getTimeSeqId(); // Thread
        byte[] keyByte = lockName.getBytes(StandardCharsets.UTF_8);
        byte[] valueByte = value.getBytes(StandardCharsets.UTF_8);

        long startTime = System.currentTimeMillis();
        int cc = 0;
        Object result = null;
        while(cc++ < 1000) {	//自循环等待 硬限制最多1000次
            try {
//        		String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
                result = redisTemplate.execute(new RedisCallback() {
                    @Override
                    public Object doInRedis(RedisConnection connection) throws DataAccessException {
                        try{
                            Object nativeConnection = connection.getNativeConnection();
                            String resultString = "";
//                            JedisCommands commands = (JedisCommands) connection.getNativeConnection();
//                            resultString = commands.set(lockKey, value, "NX", "PX", millisecondsToExpire);
                            if(nativeConnection instanceof RedisAsyncCommands){
                                RedisAsyncCommands command = (RedisAsyncCommands) nativeConnection;
                                resultString = command
                                        .getStatefulConnection()
                                        .sync()
                                        .set(keyByte, valueByte, SetArgs.Builder.nx().ex((long) Math.ceil(millisecondsToExpire/1000)));
                            }else if(nativeConnection instanceof RedisAdvancedClusterAsyncCommands){
                                RedisAdvancedClusterAsyncCommands clusterAsyncCommands = (RedisAdvancedClusterAsyncCommands) nativeConnection;
                                resultString = clusterAsyncCommands
                                        .getStatefulConnection()
                                        .sync()
                                        .set(keyByte, valueByte, SetArgs.Builder.nx().ex((long) Math.ceil(millisecondsToExpire/1000)));
                            }
                            return resultString;
                        }finally {
                            connection.close();
                        }
                    }
                });
                if (String.valueOf(result).toUpperCase().contains("OK")) {
                    log.debug(Tools.objects2string("tryLock ok", cc, lockKey, lockName, value, result, millisecondsToExpire, millisecondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime)));
                    return value;
                }
            } catch (Exception e) {
                log.error(Tools.objects2string("tryLock exception", cc, lockKey, lockName, value, result, millisecondsToExpire, millisecondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime)), e);
            }
            if(System.currentTimeMillis() > startTime + millisecondsToWait){
                log.warn(Tools.objects2string("tryLock error wait timeout", cc, lockKey, lockName, value, result, millisecondsToExpire, millisecondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime)) );
                break;
            }
            try {//1000ms等待锁，共轮询10次
                Thread.sleep(Math.max(millisecondsToWait/4, 10));
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        return "";
    }

    /**
     * 释放锁
     * 有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
     * 使用lua脚本删除redis中匹配value的key
     * @param lockName
     * @param identifier 密钥
     * @return false:   锁已不属于当前线程  或者 锁已超时
     */
    @SuppressWarnings("unchecked")
    public boolean releaseLock(String lockName, String identifier) {
        String lockKey = Key.getLockRedis(lockName);

        byte[] keyBytes = lockKey.getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = identifier.getBytes(StandardCharsets.UTF_8);
        Object[] keyParam = new Object[]{keyBytes};

        Object result = redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                try{
                    Object nativeConnection = connection.getNativeConnection();

//                    List<String> keys = new ArrayList<>();
//                    keys.add(lockKey);
//                    List<String> args = new ArrayList<>();
//                    args.add(identifier);
//                    // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
//                    // 集群模式
//                    if (nativeConnection instanceof JedisCluster) {
//                        return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, args);
//                    }
//
//                    // 单机模式
//                    else if (nativeConnection instanceof Jedis) {
//                        return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, args);
//                    }
//                    return 0L;
//
                    if (nativeConnection instanceof RedisScriptingAsyncCommands) {
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
        boolean res = result != null && (Long)result > 0;
        if (res) {
            log.debug(Tools.objects2string("release lock ok", lockKey, lockName, identifier, result));
        }else{
            log.error(Tools.objects2string("release lock error", lockKey, lockName, identifier, result));
        }
        return res;
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
    public <T> T get(final String key, final T defaultValue) {
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