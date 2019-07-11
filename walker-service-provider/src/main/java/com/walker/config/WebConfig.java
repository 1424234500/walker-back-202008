package com.walker.config;


import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.walker.event.intercept.LogInterceptors;
import com.walker.event.intercept.LoginInterceptors;
import com.walker.event.listener.ContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * 配置
 *
 * 过滤器
 * 过滤器是什么?
 * 简单的来说，过滤器就是过滤的作用，在web开发中过滤一些我们指定的url。
 * 过滤器主要做什么？
 * 过滤掉一些不需要的东西，例如一些错误的请求。
 * 也可以修改请求和相应的内容。
 *
 * 过滤器的代码实现
 * 过滤器(filter)有三个方法，其中初始化（init）和摧毁（destroy）方法一般不会用到，主要用到的是doFilter这个方法。
 * 而至于怎么过滤呢？
 * 如果过滤通过，则在doFilter执行filterChain.doFilter(request,response);该方法。
 *
 * 拦截器
 *
 * 拦截器是什么?
 * 简单的来说，就是一道阀门，拦截不需要的东西。
 * 拦截器主要做什么？
 * 对正在运行的流程进行干预。
 *
 * 拦截器的代码实现。
 * 拦截器也主要有三个方法，其中
 * preHandle是在请求之前就进行调用，如果该请求需要被拦截，则返回false，否则true;
 * postHandle是在请求之后进行调用，无返回值;
 * afterCompletion是在请求结束的时候进行调用，无返回值。
 *
 * 监听器
 * 监听项目启动
 *
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("addInterceptors");
        registry.addInterceptor(new LogInterceptors() )
                .excludePathPatterns(
                        Arrays.asList(new String[]{
                                "/webjars/*"
                        })
                )
//            .addPathPatterns(
//                Arrays.asList(new String[]{
//                        "*.do",
//                })
//            )
        ;
        registry.addInterceptor(new LoginInterceptors())
            .excludePathPatterns(
                Arrays.asList(new String[]{
                        "/*/*onlogin.do",
                        "/*/*loginin.do"
                })
            )
        ;


//        <!-- 拦截器已采用url过滤.do模式只拦截controller，此处无效？ -->
//	<!-- 装配拦截器  实现aop 事务 日志 权限 限流 -->
//    <mvc:interceptors>
//  		<!-- 日志拦截访问url 外层bean拦截所有 -->
//     	<bean class="com.walker.event.intercept.LogInterceptors" />
//     	<!-- 登录拦截例外跳转 -->
//    	<mvc:interceptor>
// 	 	   <mvc:mapping path="/*/**" />
//	 	   <!-- 资源拦截例外 -->
//           <!--  <mvc:exclude-mapping path="/**/*.css"/> -->
//           <mvc:exclude-mapping path="/include/**"/>
//   	 	   <mvc:exclude-mapping path="/*/**.ajax" />
//           <!-- 登录拦截例外 -->
//	 	   <mvc:exclude-mapping path="/*/*onlogin.do" />
//	 	   <mvc:exclude-mapping path="/*/*loginin.do" />
//	       <bean class="com.walker.event.intercept.LoginInterceptors" />
//      	</mvc:interceptor>
//    </mvc:interceptors>
    }

    @Bean
    public ServletListenerRegistrationBean listenerRegist() {
        log.info("ServletListenerRegistrationBean");
        ServletListenerRegistrationBean srb = new ServletListenerRegistrationBean();
        srb.setListener(new ContextListener());
        return srb;
    }
}