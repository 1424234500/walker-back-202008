package com.walker.intercept;

import com.walker.config.Context;
import com.walker.config.ShiroConfig;
import com.walker.core.cache.Cache;
import com.walker.core.cache.CacheMgr;
import com.walker.mode.User;
import com.walker.util.RequestUtil;
import com.walker.util.SpringContextUtil;
import com.walker.service.LogService;
import com.walker.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器 日志 登录/访问权限 事务   监控所有用户操作和登录并记录日志数据库
 * 拦截器是在上下文容器 Spring Context 初始化之前执行，所以没有办法直接在拦截器中注入Service对象
 * @author Walker
 *
 */
public class UserInterceptors implements HandlerInterceptor{
	private Logger log = LoggerFactory.getLogger(getClass());

	Cache<String> cache = CacheMgr.getInstance();

	LoginService loginService = SpringContextUtil.getBean("loginService");

	LogService logService = SpringContextUtil.getBean("logService");
	ShiroConfig shiroConfig = SpringContextUtil.getBean("shiroConfig");

	/**
	 *  redis缓存的有效时间单位是秒 默认过期时间：1 hours
	 */
	@Value("${session.redis.expiration:1800}")
	private long sessionRedisExpiration;
    /** 
     * 在渲染视图之后被调用； 
     * 可以用来释放资源 
     */   
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object method, Exception e)    throws Exception {  
    	// log.info("==============执行顺序: 3、afterCompletion================");
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
     */  
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object method) throws Exception {  
       // log.info("==============执行顺序: 1、preHandle================");

		Object tokenObj = request.getSession().getAttribute("TOKEN");
//		request.getHeader("TOKEN")
		String token = tokenObj == null ? "" : String.valueOf(tokenObj);
		if(token.length() == 0){
			token = request.getHeader("TOKEN");
			token = token == null ? "" : token;
			log.debug("session is null, then userAgent token " + token);
		}
//	    Map<String, Object> map =  cache.get(LoginService.CACHE_KEY, new HashMap<String, Object>());
//		Map<String, String> user = redisDao.hmGet(token);
		User user = token.length() > 0 ? shiroConfig.getOnlineUser(token) : null;

		if( user != null ){
//			log.info(info + " token:" + tokenObj + " go ");
			shiroConfig.keeponUser(token);
			Context.setToken(token);
			Context.setUser(user);
	    }else{
			String requestUri = request.getRequestURI();
			String contextPath = request.getContextPath();
			String url = requestUri.substring(contextPath.length());  //[/student/listm]
			log.info("ipport:" + request.getRemoteAddr() + ":" + request.getRemotePort() + " " + url + " token:" + tokenObj + " go login 401");
			RequestUtil.echo401(response, token);
//			RequestUtil.sendRedirect(response, "/");
	    	return false;
	    }

        return true;  
    }  
  
}  