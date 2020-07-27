package com.walker.core.database;

import com.walker.common.util.Tools;
import org.junit.Test;

public class LockerZookeeperTest {
    LockerZookeeper zookeeper = new LockerZookeeper().setZookeeperModel(new ZookeeperModel().setHost("localhost:8096").setMillsecondsTimeout(10000));

    @Test
    public void tryLock() {
        Tools.formatOut(zookeeper.getZookeeperModel().findPage(LockerZookeeper.KEY));
        String id = zookeeper.tryLock("hellozookeeper", 50, 1);
        Tools.out("tryLock " + id);
        Tools.formatOut(zookeeper.getZookeeperModel().findPage(LockerZookeeper.KEY));
        Tools.out("releaseLock " +  zookeeper.releaseLock("hellozookeeper", id) );
        Tools.formatOut(zookeeper.getZookeeperModel().findPage(LockerZookeeper.KEY));


    }

}