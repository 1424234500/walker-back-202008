package com.walker.design.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 代理实现
 *
 *
 *
 */
public class UserMapperInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        try{
            if(method.getName().startsWith("getName")){
                return "walker";
            }else if(method.getName().startsWith("hello")){
                return "world";
            }else{
                return method.invoke(this, args);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
