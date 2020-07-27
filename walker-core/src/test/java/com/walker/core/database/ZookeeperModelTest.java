package com.walker.core.database;

import com.walker.common.util.Tools;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public class ZookeeperModelTest {
    ZookeeperModel zookeeperModel = new ZookeeperModel();

    @Test
    public void findPage() {
        String url = "/test";
        Tools.out("clear", zookeeperModel.delete(new HashSet<>(Arrays.asList(url))));

        Tools.out("----findPage-------");
        Tools.formatOut(zookeeperModel.findPage(url));

        boolean res = false;
        res = zookeeperModel.create(url, url);
        Tools.out("create", res, "isExists?", zookeeperModel.exists(url));
        Assert.assertTrue(res);
        res = zookeeperModel.create(url, url);
        Tools.out("create2", res, "isExists?", zookeeperModel.exists(url));
        Assert.assertTrue(res==false);
        Tools.out("----findPage-------");
        Tools.formatOut(zookeeperModel.findPage(url));

        res = zookeeperModel.createOrUpdateVersion(url, "2222");
        Tools.out("createOrUpdateVersion", res, "isExists?", zookeeperModel.exists(url));
        Assert.assertTrue(res==true);

        res = zookeeperModel.createOrUpdateVersion(url, "2222", 998);
        Tools.out("createOrUpdateVersion", res, "isExists?", zookeeperModel.exists(url));
        Assert.assertTrue(res==false);
        Tools.out("----findPage-------");
        Tools.formatOut(zookeeperModel.findPage(url));

        for(int i = 0; i < 3; i++) {
            res = zookeeperModel.createOrUpdateVersion(url + "/node" + i, "n" + i);
            Tools.out("createOrUpdateVersion" + i, res);
            Assert.assertTrue(res == true);
        }
        Tools.out("----findPage-------");
        Tools.formatOut(zookeeperModel.findPage(url));

        int dsize = zookeeperModel.delete(new HashSet<>(Arrays.asList("/test/node0", "/test/node1")));
        Tools.out("delete", dsize);
        Assert.assertTrue(dsize == 2);

        Tools.out("----findPage-------");
        Tools.formatOut(zookeeperModel.findPage(url));
        Tools.formatOut(zookeeperModel.findPage("/"));




    }

}