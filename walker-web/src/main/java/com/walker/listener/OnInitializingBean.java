package com.walker.listener;

import com.walker.config.Config;
import com.walker.core.scheduler.Task;
import com.walker.job.JobUpdateArea;
import com.walker.service.InitService;
import com.walker.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 生命周期
 *
 * spring容器启动后执行
 *
 */
@Component
public class OnInitializingBean implements InitializingBean {
    private Logger log = LoggerFactory.getLogger(getClass());


    @Autowired
    InitService initService;
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info(Config.PRE + "OnInitBean afterPropertiesSet " + String.valueOf(initService));

    }
}