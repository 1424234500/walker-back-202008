package com.walker.dao;

import com.walker.common.util.Tools;
import com.walker.mode.Man;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ManRepositoryTest {
    @Autowired
    ManRepository manRepository;
    @Test
    public void test(){
        Man man = new Man();
        man.setId("1").setSex(0).setPwd("pwd").setName("name");
        Tools.out(manRepository.save(man));

        List<Man> mans = new ArrayList<>();
        for(int i = 0; i < 32; i++){
            mans.add(new Man().setId("id_" + i).setSex(i%2).setPwd("pwd").setName("name"));
//            Tools.out(manRepository.save(mans.get(i));

        }
//        Tools.out(manRepository.saveAll(mans));

        Tools.out(manRepository.getOne("1"));


    }

}