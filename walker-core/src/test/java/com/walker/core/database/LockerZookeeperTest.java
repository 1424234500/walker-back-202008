package com.walker.core.database;

import com.walker.common.util.Tools;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class LockerZookeeperTest {

    @Test
    public void tryLock() {
        LockerZookeeper zookeeper = new LockerZookeeper("localhost:8096", 10000);
        Tools.out("\n" + showNodes(zookeeper.getNodes("/")));
//        Tools.out("start " + showNodes(zookeeper.getNodes("/zookeeper")));
//        String id = zookeeper.tryLock("hellozookeeper", 5, 1);
//        Tools.out("tryLock " + showNodes(zookeeper.getLocks()));
//        zookeeper.releaseLock("hellozookeeper", id);
//        Tools.out("releaseLock " + showNodes(zookeeper.getLocks()));



    }
    public String showNodes(Map<String, Object> map){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("" + map.get("path") + ":" + map.get("data") + " " + map.get("size"));
        if(map.get("size") != null){
            Map<String, Object> c = (Map<String, Object>) map.get("childs");
            for(String key : c.keySet()){
                Map<String, Object> dc = (Map<String, Object>) c.get(key);
                stringBuilder.append("\n\t" + showNodes(dc));
            }
        }
        return stringBuilder.toString();
    }

    @Test
    public void releaseLock() {
    }

    @Test
    public void getLocks() {
    }
}