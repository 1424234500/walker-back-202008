package com.walker.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 自定义配置文件
 *
 用@Configuration注解该类，等价于XML中配置beans；用@Bean标注方法等价于XML中配置bean。
 */
@Configuration
@ImportResource({"classpath:dubbo-service-config.xml"})
//@PropertySource({"classpath:dubbo-service-config.xml"})
public class DubboConfig {

    private Logger log = LoggerFactory.getLogger(getClass());

    public DubboConfig(){
        log.info(Config.PRE + " dubbo ");
    }



}