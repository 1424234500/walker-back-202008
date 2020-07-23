package com.walker.core.database;

import com.walker.common.util.LangUtil;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.core.aop.FunArgsReturn;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * zookeeper工具
 */
public class ZookeeperModel {
    private static Logger log = LoggerFactory.getLogger(ZookeeperModel.class);
    String host = "localhost:2181";
    Integer secondsTimeout = 10;

    public String getHost() {
        return host;
    }

    public ZookeeperModel setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getSecondsTimeout() {
        return secondsTimeout;
    }

    public ZookeeperModel setSecondsTimeout(Integer secondsTimeout) {
        this.secondsTimeout = secondsTimeout;
        return this;
    }

    public ZooKeeper getZookeeperInstanse(){
        try {
            ZooKeeper zookeeper = new ZooKeeper(host, secondsTimeout, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    log.info(watchedEvent.toString());
                }
            });
            return zookeeper;
        } catch (IOException e) {
            log.error("zook:" + host + ",timeout:" + secondsTimeout + ",exception:" + e.getMessage(),  e);
        }
        return null;
    }


    public Map<String, String> getCols(){
        Map<String, String> res = new LinkedHashMap<>();
        res.put("URL", "路径");
        res.put("CHILD_SIZE", "子节点数");
        res.put("DATA", "值");
        return res;
    }

    public <T> T doZookeeper(FunArgsReturn<ZooKeeper, T> funArgsReturn){
        ZooKeeper zookeeper = getZookeeperInstanse();
        try {
            if (zookeeper != null && funArgsReturn != null) {
                return funArgsReturn.make(zookeeper);
            }
        }finally {
            if(zookeeper != null){
                try {
                    zookeeper.close();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return null;
    }


    /**
     *
     * @param urll  /
     * @return  [
     *      {URL:/dubbo, DATA:null, CHILD_SIZE:999, (childs) },...
     * ]
     */
    public List<LinkedHashMap<String, String>> findPage(final String urll){
        return doZookeeper(new FunArgsReturn<ZooKeeper, List<LinkedHashMap<String, String>>>() {
            @Override
            public List<LinkedHashMap<String, String>> make(ZooKeeper zookeeper) {
                List<LinkedHashMap<String, String>> res = new ArrayList<>();

                String url = urll;

                if(url == null ){
                    url = "/";
                }
                try {
                    List<String> childrens = zookeeper.getChildren(url, false);
                    log.debug("getChildren " + url + " " + childrens);

                    for(String key : childrens){
                        key = (url.endsWith("/") ? url : (url + "/") ) + key;
                        LinkedHashMap<String, String> line = new LinkedHashMap<>();
                        int size = zookeeper.getChildren(url, false).size();
                        byte[] bs = zookeeper.getData(url, false, null);
                        String data = new String(bs == null ? new byte[]{} : bs);

                        line.put("URL", key);
                        line.put("CHILD_SIZE", "" + size);
                        line.put("DATE", data);

                        res.add(line);
                    }
                } catch (Exception e) {
                    log.error(url + " " + e.getMessage(), e);
                }
                return res;
            }
        });

    }
    public Object create(String url, String value){
        return doZookeeper(new FunArgsReturn<ZooKeeper, Object>() {
            @Override
            public Object make(ZooKeeper zooKeeper) {
                try {
                    if(zooKeeper.exists(url, false) != null) {
                        zooKeeper.create(url, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL.EPHEMERAL);
                        log.info("zooeeper create " + url + " " + value);
                        return value;
                    }
                } catch (Exception e) {
                    log.error("zooeeper create " + url + " " + value + " " + e.getMessage(), e);
                }
                return null;
            }
        });
    }
    public int delete(Set<String> urls){
        return doZookeeper(new FunArgsReturn<ZooKeeper, Integer>() {
            @Override
            public Integer make(ZooKeeper zooKeeper) {
                int res = 0;
                try {
                    for(String url : urls) {
                        if (zooKeeper.exists(url, false) != null) {
                            zooKeeper.delete(url, -1);
                            res++;
                        }
                    }
                } catch (Exception e) {
                    log.error("zooeeper deletes " + urls + " " + res + " " + e.getMessage(), e);
                }
                return res;
            }
        });
    }




}
