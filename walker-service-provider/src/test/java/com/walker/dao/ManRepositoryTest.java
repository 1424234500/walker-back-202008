package com.walker.dao;

import com.walker.ApplicationTests;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.mode.Man;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ManRepositoryTest extends ApplicationTests {
    @Autowired
    ManRepository manRepository;
    @Test
    public void test(){
        Man man = new Man();
        man.setId("1").setSex(0).setPwd("pwd").setName("name").setTime(TimeUtil.getTimeYmdHmss());
        Tools.out("save", manRepository.save(man));

        List<Man> mans = new ArrayList<>();
        for(int i = 0; i < 32; i++){
            mans.add(new Man().setId("id_" + i).setSex(i%2).setPwd("pwd").setName("name").setTime(TimeUtil.getTimeYmdHmss()));
//            Tools.out("batch", i, manRepository.save(mans.get(i)));
        }
        Tools.out("saveAll", manRepository.saveAll(mans));

        Tools.formatOut(manRepository.findAll());
        Tools.out(manRepository.findById("1"));
//        Tools.out(manRepository.getOne("1")); //特殊功能?


    }

}