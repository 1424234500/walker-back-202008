package com.walker.event.intercept;

import com.walker.config.Context;
import com.walker.util.RequestUtil;
import com.walker.util.SpringContextUtil;
import com.walker.service.LogService;
import org.apache.log4j.NDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 拦截器 日志 登录/访问权限 事务  监控所有访问地址和参数打印 
 * 实现应用之性能监控
 * 拦截器是实现成单例的，因此不管用户请求多少次都只访问同一个拦截器实现，即线程不安全。
 * 解决方案是：使用ThreadLocal，它是线程绑定的变量，提供线程局部变量（一个线程一个ThreadLocal，
 * A线程的ThreadLocal只能看到A线程的ThreadLocal，不能看到B线程的ThreadLocal）。
 * @author Walker
 *
 */
public class LogInterceptors implements HandlerInterceptor{
    private Logger log = LoggerFactory.getLogger(getClass());

//    @Autowired
//	  @Qualifier("logService")
//    LogService logService;
    //此处不能自动注入? 扫描注入包配置问题
    LogService logService = SpringContextUtil.getBean("logService");


    // 统计应用性能
    private NamedThreadLocal startTimeThreadLocal = new NamedThreadLocal("ThreadLocal-Action-Stop-Start-Time");

	
    /** 
     * 在渲染视图之后被调用； 
     * 可以用来释放资源 
     */   
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object method, Exception e)    throws Exception {  
    	// log.info("==============执行顺序: 3、afterCompletion================");
        //结束时间   统计应用的性能
        long endTime = System.currentTimeMillis();
        // 得到线程绑定的局部变量（开始时间）
        long beginTime = (long) startTimeThreadLocal.get();
        long time = endTime - beginTime;

        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String url = requestUri.substring(contextPath.length());  //[/student/listm]
        String params = RequestUtil.getRequestBean(request).toString();


        // 此处认为处理时间超过500毫秒的请求为慢请求
        if(time > 3000){
            log.error("after [" + url + "] [ cost " + time + "] " + params);
        }else if(time > 1000){
            log.warn("after [" + url + "] [ cost " + time + "] " + params);
        }else{
            log.info("after [" + url + "] [ cost " + time + "] " + params);
        }
        logService.saveStatis(url, params, time);

        NDC.pop();

        Context.clear();
        
    }  
    /** 
     * 该方法在目标方法调用之后，渲染视图之前被调用； 
     * 可以对请求域中的属性或视图做出修改 
     *  
     */  
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object method, ModelAndView modelAndView) throws Exception {  
    	// log.info("==============执行顺序: 2、postHandle================");
    }  
  
    /** 
     * 可以考虑作权限，日志，事务等等 
     * 该方法在目标方法调用之前被调用； 
     * 若返回TURE,则继续调用后续的拦截器和目标方法 
     * 若返回FALSE,则不会调用后续的拦截器和目标方法 
     * 
     *  
     */  
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object method) throws Exception {  
        //log.info("==============执行顺序: 1、preHandle================");
//    	String s = request.getCharacterEncoding();
    	request.setCharacterEncoding("UTF-8");
    	response.setCharacterEncoding("UTF-8");
    	Object params = RequestUtil.getRequestBean(request).toString();

        // 设置开始时间  线程绑定变量（该数据只有当前请求的线程可见）
        startTimeThreadLocal.set(System.currentTimeMillis());
        
        //log4j 日志栈控制 配置输出 %X{uid} %X{remoteAddr}
        NDC.push(request.getRemoteAddr() + ":" + request.getRemotePort() + " " + params);

        Context.setRequest(request);
        Context.setResponse(response);
        Context.setTimeStart();             //设置上下文
        
        
 
        String requestUri = request.getRequestURI();  
        String contextPath = request.getContextPath();  
        String url = requestUri.substring(contextPath.length());  //[/student/listm]
         
        String name = "", cla = "";
        if(method != null && method instanceof HandlerMethod){
        	try{
	        	HandlerMethod handlerMethod = (HandlerMethod) method; 
	        	name = handlerMethod.getMethod().getName();
	            cla = handlerMethod.getBean().toString();
	            cla = cla.substring(0, cla.indexOf("@"));
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        }
        //日志 记录 输出       
//        log.info("++++++++ ");
	    log.info("before [" + url + "] [" + cla + "." + name + "]" + params);

        return true;  
    }  
  
}  