package com.walker.dao;

import com.walker.ApplicationTests;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.mode.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

public class MessageRepositoryTest extends ApplicationTests {
    @Autowired
    MessageRepository messageRepository;

    @Test
    public void test(){
        Tools.out(messageRepository.save(new Message().setId("1").setText("hello_" + TimeUtil.getTimeYmdHmss())));
        List<Message> mans = new ArrayList<>();
        for(int i = 0; i < 32; i++){
            mans.add(new Message().setId("id_" + i).setText("hello" + i + "_" + TimeUtil.getTimeYmdHmss()));
        }
        Tools.out("saveAll", messageRepository.saveAll(mans));

        Tools.formatOut(messageRepository.findAll());
        Tools.out(messageRepository.findById("1"));
//        Tools.out(manRepository.getOne("1")); //特殊功能?

    }




}