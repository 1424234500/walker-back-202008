package com.walker.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 自定义配置文件
 *
 用@Configuration注解该类，等价于XML中配置beans；用@Bean标注方法等价于XML中配置bean。
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
