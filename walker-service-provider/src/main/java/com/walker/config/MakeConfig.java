package com.walker.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 自定义配置文件
 */
@Configuration
@PropertySource({"classpath:make.properties"})
@Component("makeConfig")
public class MakeConfig {

    private Logger log = LoggerFactory.getLogger(getClass());



    @Value("${test}")
    public String test;

    public String toString(){
        String res = "test:" + test;
        log.info(res);
        return res;
    }


}
