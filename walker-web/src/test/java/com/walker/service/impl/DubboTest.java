package com.walker.service.impl;

import com.walker.ApplicationWebTests;
import com.walker.service.MessageService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DubboTest extends ApplicationWebTests {

    @Autowired
    MessageService messageService;
    @Test
    public void testDubboConsumer(){
//        Tools.out("dubbo-comsumer-sizeMsg", messageService.sizeMsg());
        out("no implemnets");
    }


}
