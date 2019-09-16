package com.walker.service;

import com.walker.common.util.ThreadUtil;
import com.walker.common.util.Tools;
import com.walker.dubbo.DubboMgr;

public class ApplicationConsumer {
    ApplicationConsumer(){
        DubboMgr.getInstance().setDubboXml("dubbo-service-config.xml").start();

        EchoService service  = DubboMgr.getService("echoService");
        Tools.out("dubbo", service.echo("hello"));
//
//        ScheduledExecutorService sch = Executors.newSingleThreadScheduledExecutor();
//        sch.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                Tools.out("--------------");
//                MessageService service  = DubboMgr.getService("messageService");
//
//                Tools.out("dubbo", "messageService", service.sizeMsg());
//
//            }
//        }, 1000, 10000, TimeUnit.MILLISECONDS);
        ThreadUtil.sleep(22222222222L);
    }

    public static void main(String[] argv){
        new ApplicationConsumer();
    }
}
