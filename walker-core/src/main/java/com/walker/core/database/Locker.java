package com.walker.core.database;

import java.util.LinkedHashMap;

public interface Locker {

    String tryLock(String lockName, Integer secondsToExpire, Integer secondsToWait);

    Boolean releaseLock(String lockName, String identifier);

    LinkedHashMap<String, Object> getLocks();

}
