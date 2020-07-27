package com.walker.core.database;

import com.walker.common.util.Tools;
import com.walker.core.aop.FunArgsReturn;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * zookeeper主要是为了统一分布式系统中各个节点的工作状态
 * ，在资源冲突的情况下协调提供节点资源抢占
 * ，提供给每个节点了解整个集群所处状态的途径。
 * 这一切的实现都依赖于zookeeper中的
 *
 *      事件监听和通知机制
 *      https://www.cnblogs.com/shamo89/p/9787176.html
 *
 * zookeeper工具
 *
 * version参数问题  CAS
 *      -1  代表以zk服务端实际为准    Atomic方式
 *          可累加 (不关心是否有改动,串行保证不漏)
 *      stat.getVersion()   以上次exists查询出来的版本号为准
 *          可预防查询出来和保存期间的其他端版本改动,避免ABA修改 (保证改动一致性 避免脏读改动)
 *
 *  watch问题 轻量级的，其实就是本地JVM的Callback
 *      watch一次性触发
 *      watch触发后没有再次设置Watcher之前 期间的通知丢失 Zookeeper只能保证最终的一致性，而无法保证强一致性。  不能期望能够监控到节点每次的变化
 *
 *  维护了两个Watch列表，一个节点数据Watch列表，另一个是子节点Watch列表。getData()和exists()设置数据Watch，getChildren()设置子节点Watch。两者选其一，可以让我们根据不同的返回结果选择不同的Watch方式，getData()和exists()返回节点的内容，getChildren()返回子节点列表。因此，setData()触发内容Watch，create()触发当前节点的内容Watch或者是其父节点的子节点Watch。delete()同时触发父节点的子节点Watch和内容Watch，以及子节点的内容Watch。
 *      setData         触发  当前节点内容watch
 *      create/delete   触发  当前节点内容watch和父节点的子watch
 *
 */
public class ZookeeperModel implements Watcher{
    private static Logger log = LoggerFactory.getLogger(ZookeeperModel.class);
    Boolean watch = false;
    String host = "localhost:8096"; //2181
    Integer millsecondsTimeout = 10000;


    /**用于等待zookeeper连接建立之后 通知阻塞程序继续向下执行 */
    private CountDownLatch signal = new CountDownLatch(1);


    public String getHost() {
        return host;
    }

    public ZookeeperModel setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getMillsecondsTimeout() {
        return millsecondsTimeout;
    }

    public ZookeeperModel setMillsecondsTimeout(Integer millsecondsTimeout) {
        this.millsecondsTimeout = millsecondsTimeout;
        return this;
    }

    public ZooKeeper getZookeeperInstanse(){
        try {
            ZooKeeper zookeeper = new ZooKeeper(host, millsecondsTimeout, this);
            try {
                signal.await(millsecondsTimeout, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
            return zookeeper;
        } catch (IOException e) {
            log.error("zook:" + host + ",timeout:" + millsecondsTimeout + ",exception:" + e.getMessage(),  e);
        }
        return null;
    }


    public Map<String, String> getCols(){
        Map<String, String> res = new LinkedHashMap<>();
        res.put("URL", "路径");
        res.put("DATA", "值");
        res.put("STAT", "详情");
        res.put("CHILD_SIZE", "子节点数");
        res.put("CHILD", "子节点");

        res.put("ACL", "权限");

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
                    Stat stat = zookeeper.exists(url, watch);
                    if(stat != null) {
                        List<String> childrens = zookeeper.getChildren(url, watch);
                        log.debug("getChildren " + url + " " + childrens);

                        for (String key : childrens) {
                            key = (url.endsWith("/") ? url : (url + "/")) + key;
                            LinkedHashMap<String, String> line = new LinkedHashMap<>();
                            Stat stat1 = zookeeper.exists(url, watch);
                            List<String> listChildren = zookeeper.getChildren(url, watch);
                            int size = listChildren.size();
                            byte[] bs = zookeeper.getData(url, watch, null);
                            String data = new String(bs == null ? new byte[]{} : bs);
                            String info = stat1.toString();
                            List<ACL> acls = zookeeper.getACL(url, stat);
                            line.put("URL", key);
                            line.put("DATE", data);
                            line.put("STAT", info);
                            line.put("ACL", String.valueOf(acls));
                            line.put("CHILD_SIZE", "" + size);
                            line.put("CHILD", String.valueOf(listChildren));

                            res.add(line);
                        }
                    }else{
                        log.warn(url + " zk findpage stat is null, not exists?");
                    }
                } catch (Exception e) {
                    log.error(url + " " + e.getMessage(), e);
                }
                return res;
            }
        });

    }
    public Boolean create(String url, String value){
        return doZookeeper(new FunArgsReturn<ZooKeeper, Boolean>() {
            @Override
            public Boolean make(ZooKeeper zooKeeper) {
                try {
                    if(zooKeeper.exists(url, watch) == null) {
                        zooKeeper.create(url, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                        log.info("zooeeper create " + url + " " + value);
                        return true;
                    }
                } catch (Exception e) {
                    log.error("zooeeper create " + url + " " + value + " " + e.getMessage(), e);
                }
                return false;
            }
        });
    }

    public Boolean createOrUpdateVersion(String url, String value) {
        return createOrUpdateVersion(url, value, -1);
    }

    public Boolean createOrUpdateVersion(String url, String value, int version){
        return doZookeeper(new FunArgsReturn<ZooKeeper, Boolean>() {
            @Override
            public Boolean make(ZooKeeper zooKeeper) {
                try {
                    Stat stat = zooKeeper.exists(url, watch);
                    if(stat == null) {
                        zooKeeper.create(url, value.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                        log.info("zooeeper createOrUpdate " + url + " " + value);
                        return true;
                    }else{
                        int newVersion = version;
                        if(newVersion <= 0) {
                            newVersion = stat.getVersion();
                        }
                        zooKeeper.setData(url, value.getBytes(), newVersion);
                        log.info("zooeeper createOrUpdate " + url + " " + value);
                        return true;
                    }
                } catch (Exception e) {
                    log.error("zooeeper createOrUpdate " + url + " " + value + " " + e.getMessage(), e);
                }
                return false;
            }
        });
    }

    public Boolean exists(String url){
        return doZookeeper(new FunArgsReturn<ZooKeeper, Boolean>() {
            @Override
            public Boolean make(ZooKeeper zooKeeper) {
                try {
                    return zooKeeper.exists(url, watch) != null;
                } catch (Exception e) {
                    log.error("zooeeper exists " + url + " " + e.getMessage(), e);
                }
                return false;
            }
        });
    }

    /**
     * 递归删除字节点
     */
    private int delete(ZooKeeper zooKeeper, String url){
        int res = 0;
        try {
            Stat stat = zooKeeper.exists(url, watch);
            if (stat != null) {
                List<String> childrens = zooKeeper.getChildren(url, watch);
                for (String key : childrens) {
                    key = (url.endsWith("/") ? url : (url + "/")) + key;
                    res += (delete(zooKeeper, key) > 0 ? 1 : 0);
                }
                zooKeeper.delete(url, -1);
                res++;
            }
        } catch (Exception e) {
            log.error("zooeeper delete " + url + " " + " " + e.getMessage(), e);
        }
        return res;
    }
    public int delete(Set<String> urls){
        return doZookeeper(new FunArgsReturn<ZooKeeper, Integer>() {
            @Override
            public Integer make(ZooKeeper zooKeeper) {
                int res = 0;
                try {
                    for(String url : urls) {
                        Stat stat = zooKeeper.exists(url, watch);
                        if (stat != null) {
                            res += (delete(zooKeeper, url) > 0 ? 1 : 0);
                        }
                    }
                } catch (Exception e) {
                    log.error("zooeeper deletes " + urls + " " + res + " " + e.getMessage(), e);
                }
                return res;
            }
        });
    }


    /**
     * 收到来自Server的Watcher通知后的处理。
     */
    @Override
    public void process(WatchedEvent event) {
//        Tools.out("zk 进入 process 。。。。。event = " + event);
        if (event == null) {
            return;
        }

        // 连接状态
        Event.KeeperState keeperState = event.getState();
        // 事件类型
        Event.EventType eventType = event.getType();
        // 受影响的path
        String path = event.getPath();

        if (Event.KeeperState.SyncConnected == keeperState) {
            // 成功连接上ZK服务器
            if (Event.EventType.None == eventType) {
                Tools.out("zk Connected " + event);
                signal.countDown();
            }
            //创建节点
            else if (Event.EventType.NodeCreated == eventType) {
                Tools.out("zk NodeCreated " + event);
            }
            //更新节点
            else if (Event.EventType.NodeDataChanged == eventType) {
                Tools.out("zk NodeDataChanged " + event);
            }
            //更新子节点
            else if (Event.EventType.NodeChildrenChanged == eventType) {
                Tools.out("zk NodeChildrenChanged " + event);
            }
            //删除节点
            else if (Event.EventType.NodeDeleted == eventType) {
                Tools.out("zk NodeDeleted " + event);
            }
            else {
                Tools.out("zk Node other type " + event);
            };
        }
        else if (Event.KeeperState.Disconnected == keeperState) {
            Tools.out("zk Disconnected " + event);
        }
        else if (Event.KeeperState.AuthFailed == keeperState) {
            Tools.out("zk AuthFailed " + event);
        }
        else if (Event.KeeperState.Expired == keeperState) {
            Tools.out("zk Expired " + event);
        }
        else {
            Tools.out("zk other " + event);
        }
    }
}
//
//
//
//
//创建节点
////同步方式
//        create(final String path, byte data[], List<ACL> acl,CreateMode createMode)
////异步方式
//        create(final String path, byte data[], List<ACL> acl,CreateMode createMode,  StringCallback cb, Object ctx)
//        参数说明：
//        path：-znode路径
//        data：节点数据内容
//        acl：访问控制列表
//        createMode：节点的类型，枚举类
//                          CreateMode.EPHEMERAL.EPHEMERAL  程序退出自动删除的临时节点？
//                          CreateMode.PERSISTENT   持久化
//        cb：异步回调接口
//        ctx：传递上下文参数
////权限列表： world:anyone:adrwa //节点类型：持久化节点
//        zooKeeper.create("/create","create".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//        world授权:
//        List<ACL> acls = new ArrayList<ACL>();
//        Id id = new Id("world", "anyone");
//        acls.add(new ACL(ZooDefs.Perms.READ,id));
//        acls.add(new ACL(ZooDefs.Perms.WRITE,id));
//        zooKeeper.create("/create/node2","node2".getBytes(), acls, CreateMode.PERSISTENT);
//        ip授权：
//        List<ACL> acls = new ArrayList<ACL>();
//        Id id = new Id("ip", "192.168.10.132");
//        acls.add(new ACL(ZooDefs.Perms.ALL,id));
//        zooKeeper.create("/create/node3","node3".getBytes(), acls, CreateMode.PERSISTENT);
//        auth授权：
////添加授权用户
//        zooKeeper.addAuthInfo("digest","admin:admin".getBytes());<br>　　　　　//给予所有权限
//        zooKeeper.create("/create/node4","node4".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
//        digest授权：
////授权模式和授权对象
//        Id id = new Id("digest", "wj:64ibjjwm94195LPhuzhUdkIjOl0=");
////所有权限
//        acls.add(new ACL(ZooDefs.Perms.ALL,id));
//        zooKeeper.create("/create/node5","node5".getBytes(), acls, CreateMode.PERSISTENT);