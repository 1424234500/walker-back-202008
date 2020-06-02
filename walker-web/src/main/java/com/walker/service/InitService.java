package com.walker.service;


import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.walker.common.util.*;
import com.walker.config.MakeConfig;
import com.walker.dao.ConfigDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 初始化数据服务
 * 初始化sentinel限流
 */
@Service("initService")
public class InitService {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    ScheduleService scheduleService;

    @Autowired
    MakeConfig makeConfig;
    @Autowired
    ConfigDao configDao;

    /**
     * 启动后挂载初始化
     */
    public void initOnStart(){
        //异步初始化
        ThreadUtil.execute(new Runnable() {
           @Override
           public void run() {
               initSentinel();
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

    /**
     * sentinel限流熔断框架初始话
     * 配置见dml.sql
     */
    public void initSentinel() {
        log.info(Config.getPre() + "initSentinel!!! ");
//        FlowRuleManager.loadRules(List<FlowRule> rules); // 修改流控规则
//        DegradeRuleManager.loadRules(List<DegradeRule> rules); // 修改降级规则
//        SystemRuleManager.loadRules(List<SystemRule> rules); // 修改系统规则
//        AuthorityRuleManager.loadRules(List<AuthorityRule> rules); // 修改授权规则
        List<FlowRule> rules = new ArrayList<>();

        String url = "/comm/getColsMap.do";
        double qps = configDao.get(url, 1D);
        if(qps >= 0) {
            FlowRule rule = new FlowRule();
            rule.setResource(url);
            rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
            rule.setCount(1);// Set limit QPS to 20.
            rules.add(rule);
        }

        FlowRuleManager.loadRules(rules);


    }

}
