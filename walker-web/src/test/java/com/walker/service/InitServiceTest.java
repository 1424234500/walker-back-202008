package com.walker.service;

import com.walker.common.util.Tools;
import com.walker.mode.Area;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class InitServiceTest extends InitService{
//    @Autowired
//    AreaService areaService;
    @Test
    public void getTest() {

//        Area root = getCityRoot();
        //此处按照一级省 分别递归获取树
        Area root = getCityRootChina();
        getCity(root, false, null);
        for(int i = 0; i < root.getChilds().size() && i < 2; i++){
            Area item = root.getChilds().get(i);
            getCity(item, true, null);  //html获取构建省 树
            List<Area> list = tree2list(item, root.getID(), root.getPATH(), root.getPATH_NAME());   //递归构建树
            Tools.formatOut(list);
            //   areaService.saveAll(list);
        }

    }
    @Test
    public void testZave(){

//        Area root = getCityRoot();
        //此处按照一级省 分别递归获取树
        Area root = getCityRootChina();
        getCity(root, false, null);
        for(int i = 1; i < root.getChilds().size() && i < 2; i++){
            Area item = root.getChilds().get(i);
            getCity(item, true, null);  //html获取构建省 树
            List<Area> list = tree2list(item, root.getID(), root.getPATH(), root.getPATH_NAME());   //递归构建树
            Tools.formatOut(list);
//            areaService.saveAll(list);
        }

    }

}