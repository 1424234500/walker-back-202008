package com.walker.core.aop;
/**
 * 通用泛型回调接口
 * 返回值采取协变? 或者 泛型定义
 * @param <A>
 */
public interface Fun<A>{ 
	public <T> T make(A obj) ;
}

