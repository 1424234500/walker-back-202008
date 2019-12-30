package com.walker.service.impl;

import com.walker.common.util.Bean;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.common.util.Watch;
import com.walker.mode.Msg;
import com.walker.mode.UserSocket;
import com.walker.service.MessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageServiceShardingImplTest {
    @Autowired
    @Qualifier("messageServiceSharding")
    MessageService messageServiceSharding;


    @Test
    public void test(){

//            messageServiceShardingImpl messageServiceSharding = new messageServiceShardingImpl();

            int saveCount = 3;
            int size = 3;
            Watch w = new Watch("test merge and no merge", size);

            w.costln("sizeMsg", messageServiceSharding.sizeMsg());
            w.costln("sizeMsgUser", messageServiceSharding.sizeMsgUser());


            String id = "000";
            String id1 = "001";
            String id2 = "002";
            String id3 = "003";
            String id4 = "004";
            List<String> scores = new ArrayList<String>();
            for(int i = 0; i < saveCount; i++) {
                Msg msg = new Msg();
                msg.setType("TEST_" + getClass().getSimpleName());
                msg.setTo("");
                msg.setData(new Bean().set("count", i));
                msg.setUserFrom(new UserSocket().setId(id).setName("name"));
                msg.setTimeDo(System.currentTimeMillis());
                msg.addUserTo(id1);
                msg.addUserTo(id2);
                msg.addUserTo(id3);
                msg.addUserTo(id4);

                Long score = messageServiceSharding.save(msg.getUserTo(), msg);
                String t = TimeUtil.getTime(score, "yyyy-MM-dd HH:mm:ss:SSS");
                Tools.out(score, t);
                scores.add(t);
            }
            w.costln("save",saveCount);
            w.costln("sizeMsg", messageServiceSharding.sizeMsg());
            w.costln("sizeMsgUser", messageServiceSharding.sizeMsgUser());

            Tools.formatOut(scores);
            //查接收者的离线消息
            List<Msg> list = messageServiceSharding.findAfter(id1, scores.get(0), 20);
            Tools.formatOut(list);

            //查发送者和接受者会话的历史消息
            List<Msg> list1 = messageServiceSharding.findBefore(id, id1, scores.get(scores.size() - 1), 20);
            Tools.formatOut(list1);

            for(int i = 0;i < size; i++) {
//                Tools.out(i);
                messageServiceSharding.findBefore(id, id1, scores.get(scores.size() - 1), i % 20);
            }
            w.costln("findBefore",size);
            for(int i = 0;i < size; i++) {
//                Tools.out(i);
                messageServiceSharding.findBeforeByMerge(id, id1, scores.get(scores.size() - 1), i % 20);
            }
            w.costln("findBeforeByMerge",size);
            w.res();
            Tools.out(w);
    }
}