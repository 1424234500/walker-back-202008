package com.walker.core.cache;

public  abstract   class CacheAdapter<K> implements Cache<K>{

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
