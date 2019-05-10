package com.walker.core.exception;

import com.walker.common.util.Tools;

/**
 * 场景:
 * 		查询用户
 * 		正常返回用户
 * 		异常则抛出信息 调用方捕获处理  同时拿到了异常信息 而不用修改正常流程
 * 
 * 提示异常 必须处理或者上抛出 用于多种返回状态  
 * 
 * eg:
 * 		
 * 		try{
 * 			user = find(xxx);
 * 			return setOk(user);
 * 		}catch(InfoException e){
 * 			return setError("error" + e.getMessage());
 * 		}
 * 
 * @author walker
 *
 */
public class InfoException extends Exception{
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