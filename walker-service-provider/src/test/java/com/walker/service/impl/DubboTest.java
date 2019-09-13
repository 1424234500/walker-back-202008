package com.walker.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.walker.ApplicationTests;
import com.walker.common.util.Tools;
import com.walker.service.MessageService;
import org.junit.Test;

public class DubboTest extends ApplicationTests {

    @Reference(version = "1.0.0")
    public MessageService messageService;
    @Test
    public void testDubboConsumer(){
        Tools.out("dubbo-comsumer-sizeMsg", messageService.sizeMsg());
    }


}
