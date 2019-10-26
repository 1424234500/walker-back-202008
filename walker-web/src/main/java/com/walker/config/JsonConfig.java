package com.walker.config;


import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.util.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration//说明是一个配置类
public class JsonConfig {
    private Logger log = LoggerFactory.getLogger(getClass());


    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
/*
    https://segmentfault.com/a/1190000015634321?utm_source=tag-newest
        Json数据的时候经常有首字母是大写
        根据get方法获取字段名异常
        DEPT_ID       DEPT_ID
        S_MTIME         s_MTIME
        lombok注解 get方法异常
*/
        TypeUtils.compatibleWithJavaBean = true;    //前两个字母大写 DEXXX
        TypeUtils.compatibleWithFieldName = true;   // 第二个字母非大写 S_XXX


        log.info(Config.PRE + "--------fastJsonHttpMessageConverters");
        // 1.定义一个converters转换消息的对象
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        // 2.添加fastjson的配置信息，比如: 是否需要格式化返回的json数据
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat, SerializerFeature.BrowserCompatible);


        // 3.在converter中添加配置信息
        fastConverter.setFastJsonConfig(fastJsonConfig);
        // 4.将converter赋值给HttpMessageConverter
        HttpMessageConverter<?> converter = fastConverter;
        // 5.返回HttpMessageConverters对象
        return new HttpMessageConverters(converter);
    }
}