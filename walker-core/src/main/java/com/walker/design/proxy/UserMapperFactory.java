package com.walker.design.proxy;

import java.lang.reflect.Proxy;

/**
 * 代理模式
 *
 *
 *
 */
public class UserMapperFactory {

    public static UserMapper getUserMapper(){
        Class<?> clz = UserMapper.class;
        return (UserMapper) Proxy.newProxyInstance(
                clz.getClassLoader()
                , clz.getInterfaces()
                , new UserMapperInvocationHandler()
        );
    }
    public static <T> T getMapper(Class<T> clz){
        return (T) Proxy.newProxyInstance(
                clz.getClassLoader()
                , clz.getInterfaces()
                , new UserMapperInvocationHandler()
        );
    }

}
