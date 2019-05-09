package com.walker.common.util;

import org.apache.log4j.Logger;

/**
 * 单例
 * Singleton.getInstance()
 */
public class SingletonTemp {
	private static Logger log = Logger.getLogger(SingletonTemp.class); 

	/**
	 * 私有构造器
	 */
	private SingletonTemp(){}
	
    /**
     * 私有静态内部类
     */
    private static class SingletonFactory{           
        private static SingletonTemp instance;
        static {
        	log.warn("singleton instance construct " + SingletonFactory.class);
        	instance = new SingletonTemp();  
        }
    }
    /**
     * 内部类模式 单例实现
     */
    public static SingletonTemp getInstance(){           
        return SingletonFactory.instance;           
    }  
    
}
