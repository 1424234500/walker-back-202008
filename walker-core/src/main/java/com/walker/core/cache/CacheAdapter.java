package com.walker.core.cache;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.walker.core.database.RedisUtil;

import java.nio.charset.Charset;

public  abstract   class CacheAdapter<K> implements Cache<K>{
//    // 预计元素个数
//    private static long size = 10 * 10000;
//    private static BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), size, 0.01);
    /**
     * 集成bloom过滤器  分布式锁
     *
     * 缓存击穿     大量访问cacheFunction数据库
     * 缓存穿透     null对象缓存
     *
     * @param key
     * @param cacheFunction
     * @param <V>
     * @return
     */
    @Override
    public <V> V getFun(K key, Cache.Function cacheFunction) {
        V res = this.get(key);
        if(res == null){
            res = cacheFunction.cache();
            log.info("cache is null new cache " + res);
            this.put(key, res);
        }else{
            log.debug(("get cache key:" + key + " res:" + res));
        }
        return res;
    }

}
