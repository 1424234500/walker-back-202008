package com.walker.config;


import com.walker.event.intercept.LogInterceptors;
import com.walker.event.intercept.LoginInterceptors;
import com.walker.event.listener.ContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private Logger log = LoggerFactory.getLogger(getClass());

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/zxc/foo").setViewName("foo");
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("addInterceptors");
        registry.addInterceptor(new LogInterceptors());
        registry.addInterceptor(new LoginInterceptors()).excludePathPatterns(
                Arrays.asList(new String[]{
                        "/*/*onlogin.do",
                        "/*/*loginin.do"
                }))
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

//    @SuppressWarnings({"rawtypes", "unchecked"})
//    @Bean
//    public FilterRegistrationBean filterRegist() {
//        log.info("filterRegist");
//        FilterRegistrationBean frBean = new FilterRegistrationBean();
//        frBean.setFilter(new MyFilter());
//        frBean.addUrlPatterns("/*");
//        System.out.println("filter");
//        return frBean;
//    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public ServletListenerRegistrationBean listenerRegist() {
        log.info("ServletListenerRegistrationBean");
        ServletListenerRegistrationBean srb = new ServletListenerRegistrationBean();
        srb.setListener(new ContextListener());
        return srb;
    }
}