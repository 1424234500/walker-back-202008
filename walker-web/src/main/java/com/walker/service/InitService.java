package com.walker.service;


import com.walker.common.util.*;
import com.walker.config.MakeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 初始化数据服务
 */
@Service("initService")
public class InitService {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    ScheduleService scheduleService;

    @Autowired
    MakeConfig makeConfig;
    @Autowired
    DeptService deptService;

    /**
     * 启动后挂载初始化
     */
    public void initOnStart(){
        //异步初始化
        ThreadUtil.execute(new Runnable() {
           @Override
           public void run() {
           }
        });
        //异步初始化
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {

            }
        });

        //同步初始化
        initQuartz();

    }
    public void initQuartz(){
        log.info("start quartz ");
        try {
            scheduleService.start();
//            scheduleService.add(new Task("com.walker.job.JobTest","sb quartz scheduler tools out", "0 0/2 * * * ?", "0 0/3 * * * ?"));
//            scheduleService.add(new Task("com.walker.job.JobTest2","sb quartz scheduler tools out2", "0 0/5 * * * ?", "0 0/30 * * * ?"));
//            scheduleService.add(new Task("com.walker.job.JobUpdateArea","update area from meituan", "0 0 1 * * ?"));
            log.info("start quartz ok");
        }catch (Exception e){
            log.error("start quartz error", e);
        }
    }



}
