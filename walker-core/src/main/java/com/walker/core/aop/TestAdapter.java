package com.walker.core.aop;

import org.apache.log4j.Logger;

import com.walker.common.util.Constant;
import com.walker.core.exception.ErrorException;

/**
 * 测试接口
 * 用于需要启动测试的模块 
 * 抛出runtime异常   TestException
 * 
 * eg：
 * 		缓存启动 自检
 * 		redis连接启动 自检
 * 
 */


public abstract class TestAdapter implements Test{
	protected Logger log = Logger.getLogger("test"); 

	@Override
	public void test() {
		log.warn(Constant.beginTip(getClass()));
		
		if(doTest()) {
			log.warn(Constant.okTip(getClass()));
		}else {
			throw new ErrorException(Constant.errorTip(getClass()));
		}

		log.warn(Constant.endTip(getClass()));
	}

	/**
	 * 返回false则抛出异常
	 * @return
	 */
	public abstract boolean doTest() ;

}


