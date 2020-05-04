package com.walker.intercept;

import com.google.common.util.concurrent.RateLimiter;
import com.walker.config.Context;
import com.walker.config.ControllerConfig;
import com.walker.core.cache.CacheMgr;
import com.walker.mode.User;
import com.walker.service.Config;
import com.walker.util.RequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 限流
 *
 */
public class RateLimitInterceptor implements HandlerInterceptor{
    private Logger log = LoggerFactory.getLogger(getClass());

    RateLimiter rateLimiterAll = RateLimiter.create(1);



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

//        修饰符和类型	方法和描述
//        double	acquire()
//        从RateLimiter获取一个许可，该方法会被阻塞直到获取到请求
//        double	acquire(int permits)
//        从RateLimiter获取指定许可数，该方法会被阻塞直到获取到请求
//        static RateLimiter	create(double permitsPerSecond)
//        根据指定的稳定吞吐率创建RateLimiter，这里的吞吐率是指每秒多少许可数（通常是指QPS，每秒多少查询）
//        static RateLimiter	create(double permitsPerSecond, long warmupPeriod, TimeUnit unit)
//        根据指定的稳定吞吐率和预热期来创建RateLimiter，这里的吞吐率是指每秒多少许可数（通常是指QPS，每秒多少个请求量），在这段预热时间内，RateLimiter每秒分配的许可数会平稳地增长直到预热期结束时达到其最大速率。（只要存在足够请求数来使其饱和）
//        double	getRate()
//        返回RateLimiter 配置中的稳定速率，该速率单位是每秒多少许可数
//        void	setRate(double permitsPerSecond)
//        更新RateLimite的稳定速率，参数permitsPerSecond 由构造RateLimiter的工厂方法提供。
//        String	toString()
//        返回对象的字符表现形式
//        boolean	tryAcquire()
//        从RateLimiter 获取许可，如果该许可可以在无延迟下的情况下立即获取得到的话
//        boolean	tryAcquire(int permits)
//        从RateLimiter 获取许可数，如果该许可数可以在无延迟下的情况下立即获取得到的话
//        boolean	tryAcquire(int permits, long timeout, TimeUnit unit)
//        从RateLimiter 获取指定许可数如果该许可数可以在不超过timeout的时间内获取得到的话，或者如果无法在timeout 过期之前获取得到许可数的话，那么立即返回false （无需等待）
//        boolean	tryAcquire(long timeout, TimeUnit unit)
//        从RateLimiter 获取许可如果该许可可以在不超过timeout的时间内获取得到的话，或者如果无法在timeout 过期之前获取得到许可的话，那么立即返回false（无需等待）


//        if() {
//            RequestUtil.echoErr(response, HttpServletResponse.SC_, token);
//            return false;
//        }
        return true;  
    }  
  
}  