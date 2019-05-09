package com.walker.core.exception;

import com.walker.common.util.Tools;

/**
 * 场景:
 * 		查询用户
 * 		正常返回用户
 * 		异常则抛出信息 调用方捕获处理  同时拿到了异常信息 而不用修改正常流程
 * 
 * 提示异常 通常不用处理 透传给上级做提示拦截处理 返回结果的第二种方式 
 * 
 * eg:
 * 		查询失败: 用户不存在
 * 
 * @author walker
 *
 */
public class InfoException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InfoException(Object object) {
		super(String.valueOf(object));
	}
	public InfoException(Object...objects) {
		super(Tools.objects2string(objects));
	}

}