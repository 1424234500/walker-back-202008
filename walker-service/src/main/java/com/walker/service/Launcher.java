package com.walker.service;

import com.walker.common.util.Tools;
import com.walker.dubbo.DubboMgr;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Launcher {
    Launcher(){
        DubboMgr.getInstance().setDubboXml("dubbo-service-config.xml");


        MessageService service  = DubboMgr.getService("messageService");
        Tools.out("dubbo", service.sizeMsg());

        ScheduledExecutorService sch = Executors.newSingleThreadScheduledExecutor();
        sch.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Tools.out("--------------");
                MessageService service  = DubboMgr.getService("messageService");

                Tools.out("dubbo", "messageService", service.sizeMsg());

            }
        }, 1000, 10000, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] argv){
        new Launcher();
    }
}
