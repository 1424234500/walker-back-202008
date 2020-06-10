package com.walker.core.database;

public interface Locker {

    String tryLock(String lockName, Integer secondsToExpire, Integer secondsToWait);

    Boolean releaseLock(String lockName, String identifier);

}
