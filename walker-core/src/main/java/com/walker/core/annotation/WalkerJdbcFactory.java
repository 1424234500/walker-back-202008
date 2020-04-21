package com.walker.core.annotation;

import com.walker.core.aop.ProxyUtil;
import com.walker.core.database.BaseDao;
import com.walker.core.database.BaseJdbc;
import com.walker.core.database.Dao;
import org.springframework.lang.UsesJava7;

import java.lang.annotation.AnnotationFormatError;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * 获取代理对象
 *
 */

public class WalkerJdbcFactory {

	public static <T> T getInstance(Class<T> interfac, BaseDao dao){
		return ProxyUtil.getProxy(interfac, new WalkerJdbcProxy().setBaseDao(dao));
	}


}




