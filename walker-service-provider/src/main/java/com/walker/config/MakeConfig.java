package com.walker.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 自定义配置文件
 *
 用@Configuration注解该类，等价于XML中配置beans；用@Bean标注方法等价于XML中配置bean。
 */
@Configuration
@PropertySource({"classpath:make.properties"})
//@Component("makeConfig")
public class MakeConfig {

    private Logger log = LoggerFactory.getLogger(getClass());


    @Value("${push.jpush.APP_KEY}")
    public String pushJpushAppKey;

    @Value("${push.jpush.MASTER_SECRET}")
    public String pushJpushMasterSecret;
//    push.jpush.APP_KEY=
//    push.jpush.MASTER_SECRET=


    @Value("${test:testhello}")
    public String test;

    /**
     * 全局变量读取设置
     */
    public static String TEST;
    @Value("${test}")
    public void setExamplePath(String test) {
        MakeConfig.TEST = test;
    }

    public MakeConfig(){
        log.info(Config.getPre() + "MakeConfig " );
        log.info(Arrays.asList(pushJpushAppKey, pushJpushMasterSecret, test, TEST).toString());

    }

}
