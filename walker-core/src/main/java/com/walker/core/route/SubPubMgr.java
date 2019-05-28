package com.walker.core.route;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.walker.core.aop.TestAdapter;

/**
 * 发布订阅控制器 
 *
 */
public class SubPubMgr extends TestAdapter{
	private static Logger log = Logger.getLogger("subpub"); 

	private static ConcurrentHashMap<String, SubPub<?,?>> index = new ConcurrentHashMap<>();
	
	private SubPubMgr() {}
 
	/**
	 * 指定通道 共用
	 * @param key
	 * @param threadCoreSize
	 * @return
	 */
	public static <T,V> SubPub<T,V> getSubPub(String key, Integer threadCoreSize) {
		@SuppressWarnings("unchecked")
		SubPub<T,V> subPub =  (SubPub<T,V>) index.get(key);
		if(subPub == null) {
			subPub = new SubPubMapImpl<T,V>();
			index.put(key, subPub);
			subPub.init(threadCoreSize);
		}
		return subPub;
	}
	
	/**
	 * 中转消费线程数量
	 * @param threadCoreSize
	 * @return
	 */
	public static <T,V> SubPub<T,V> getSubPub(Integer threadCoreSize) {
		SubPub<T,V> subPub =  new SubPubMapImpl<T,V>();
		subPub.init(threadCoreSize);
		return subPub;
	}
	
	

	public boolean doTest() {
		return SubPubMgr.getSubPub(1) == null;
	}


	


}
