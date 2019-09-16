package com.walker.dao;

import com.walker.ApplicationProviderTests;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.mode.MessageUser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class MessageUserRepositoryTest extends ApplicationProviderTests {
    @Autowired
    MessageUserRepository messageUserRepository;

    @Test
    public void test(){
        Tools.out(messageUserRepository.save(new MessageUser().setId("1").setMsgId("1").setSmtime(TimeUtil.getTimeYmdHmss()).setUserFrom("from").setUserTo("to")));
        List<MessageUser> mans = new ArrayList<>();
        for(int i = 0; i < 32; i++){
            mans.add(new MessageUser().setId("id_" + i).setMsgId("id_" + i % 3).setSmtime(TimeUtil.getTimeYmdHmss()).setUserFrom("from").setUserTo("to"));
        }
        Tools.out("saveAll", messageUserRepository.saveAll(mans));

        Tools.formatOut(messageUserRepository.findAll());
        Tools.out(messageUserRepository.findById("1"));
//        Tools.out(manRepository.getOne("1")); //特殊功能?

    }




}