package com.walker.intercept;

import com.google.common.util.concurrent.RateLimiter;
import com.walker.common.util.Bean;
import com.walker.config.Context;
import com.walker.config.ControllerConfig;
import com.walker.core.cache.CacheMgr;
import com.walker.dao.ConfigDao;
import com.walker.dao.Limiter;
import com.walker.dao.RateLimiterDao;
import com.walker.dao.SentinelLimitDao;
import com.walker.mode.User;
import com.walker.service.Config;
import com.walker.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * 单例
 *
 *
 * 限流
 *
 * 默认配置限流
 * 在线动态配置限流
 *
 *
 *
 *
 */
public class RateLimitInterceptor implements HandlerInterceptor{
    private Logger log = LoggerFactory.getLogger(getClass());



    @Autowired
    ConfigDao configDao;
    @Autowired
    RateLimiterDao rateLimiterDao;
    @Autowired
    SentinelLimitDao sentinelLimitDao;
    /** 
     * 在渲染视图之后被调用； 
     * 可以用来释放资源 
     */   
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object method, Exception e)    throws Exception {  
        
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
        User user = Context.getUser();
        //登录用户操作日志 记录 用户id,操作url权限?,用户操作ip/mac/端口
        String id = user == null ? "" : user.getID();
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String url = requestUri.substring(contextPath.length());  //[/student/listm]
        //sequenceid time userid url ip host 端口
        String ip = request.getRemoteAddr();//返回发出请求的IP地址
        String params = RequestUtil.getRequestBean(request).toString();
        String host=request.getRemoteHost();//返回发出请求的客户机的主机名
        int port =request.getRemotePort();//返回发出请求的客户机的端口号。
        String way = request.getMethod();

//        按ip  用户 url粒度限流
//        每秒只允许 x个请求通过 令牌桶
//        同时只允许最多y个请求执行 线程池 连接数
//        配置中心 集群 redis？
        List<Limiter> limiters = Arrays.asList(rateLimiterDao, sentinelLimitDao);

        for(Limiter limiter : limiters) {
            String info = limiter.tryAcquire(url, id);
            if (info.length() > 0) {
                RequestUtil.echoErr(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, new Bean().put("info", info).toString());
                return false;
            }
        }
        return true;
    }
  
}