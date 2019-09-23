package com.walker.config;

import com.walker.mode.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 权限管理
 */
@Configuration
@EnableCaching
public class ShiroConfig {

    private Logger log = LoggerFactory.getLogger(getClass());


    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(authRealm);


        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setSecurityManager(securityManager);

        filterFactoryBean.setLoginUrl("/loginPage");

        // 成功登陆后的界面
        //filterFactoryBean.setSuccessUrl("/indexPage");

        // 没有权限访问的界面
        //filterFactoryBean.setUnauthorizedUrl("/unauthorized");

        HashMap<String, String> filterMap = new HashMap<>();

        // 配置不会被拦截的链接 顺序判断
        filterMap.put("/static/**", "anon");
        filterMap.put("/login", "anon");
        filterMap.put("/SubmitRegistInformation", "anon");
        filterMap.put("/SendMessageCode", "anon");
        // 配置退出过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filterMap.put("/logout", "logout");

        // <!-- 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 -->:这是一个坑呢，一不小心代码就不好使了;
        // <!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        filterMap.put("/**", "authc");
        // 所有的页面都需要user这个角色
        filterMap.put("/**", "roles[user]");

        filterFactoryBean.setFilterChainDefinitionMap(filterMap);
        return filterFactoryBean;
    }


    @Bean("securityManager")
    public SecurityManager securityManager() {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(new AuthorizingRealm() {

            //添加角色权限
            @Override
            protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
                //获取用户
                User user = (User) principalCollection.getPrimaryPrincipal();
                //获取权限列表
                List<String> roles = Arrays.asList("role01");
                //添加角色和权限
                SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
                roles.forEach(item -> simpleAuthorizationInfo.addRole(item));
                return simpleAuthorizationInfo;
            }

            //登录认证
            @Override
            protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
                UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;

                String username = usernamePasswordToken.getUsername();

                User user = new User().setName(username);
                if (user == null) {
                    return null;
                } else {
                    SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(user, user.getPwd(), getName());
                    return simpleAuthenticationInfo;
                }
            }


        });
        return manager;
    }


    @Value("${password.algorithmName:md5}") //这个可以在application.properites里自行配置 如（md5，sha，base64）等等
    private String algorithmName;
    @Value("${password.hashIterations:1}")//这个是加密的次数
    private int hashIterations;

    /**
     * 返回加密后的密码
     *
     * @param password
     * @param userId
     * @return
     */
    public String getEncryptionPassword(String password, String userId) {
        //四个参数的讲解 1，加密类型，2，原始密码，3，盐值，可以是固定的，这里我门采用用户的userId作为用户的盐值进行加盐，4，加密的迭代次数
        return new SimpleHash(algorithmName, password, userId, hashIterations).toString();


    }
}