package com.walker.core.mode;

import java.util.*;

import org.apache.log4j.Logger;

import com.walker.common.util.Tools;
import com.walker.core.exception.ErrorException;

/**
 * 耗时监控 提示信息工具
 * new Watch("sql").put(sql).put("size", size).exception(e).toString()
 * new Watch("sql").put(sql).put("size", size).res(count).toString()
 * 
 * 
 * 
 * @author Walker
 * 
 */
public class Watch {
	List<Long> times;
	StringBuilder sb;
	public Watch() {
		this("");
	}
	public Watch(Object...infos) {
		this.times = new ArrayList<Long>();
		this.times.add(System.currentTimeMillis());
		sb = new StringBuilder(Tools.objects2string(infos));
	}
	public Watch put(String str) {
		this.sb.append(str);
		return this;
	}
	public Watch putln(String str) {
		this.sb.append(str).append("\n");
		return this;
	}
	public Watch put(Object key, Object...values) {
		this.sb.append(" " + key + ":" + Tools.objects2string(values));
		return this;
	}
	public Watch putln(Object key, Object...values) {
		put(key, values);
		this.put("\n");
		return this;
	}
	/**
	 * 分key耗时 查询操作耗时50ms
	 * @param key
	 * @return
	 */
	public long cost(Object...keys) {
		long now = System.currentTimeMillis();
		long last = times.get(0);
		long deta = now - last;
		this.put( (keys.length > 0 ? Tools.objects2string(keys)+" " : "" )+ "cost", deta);
		times.add(0, now);
		return deta;
	}
	public long costln(Object...keys) {
		long res = cost(keys);
		this.put("\n");
		return res;
	}
	/**
	 * 获取分片耗时
	 * @return
	 */
	public long getTimeSplit() {
		long now =  times.get(0);
		long last = times.get(times.size() > 1 ? 1 : 0);
		long deta = now - last;
		return deta;
	}
	/**
	 * 获取总耗时
	 * @return
	 */
	public long getTimeAll() {
		long now = times.get(0);
		long last = times.get(times.size() - 1);
		long deta = now - last;
		return deta;
	}
	/**
	 * 总耗时存入
	 * 若传入log则记录日志
	 * @return
	 */
	public Watch res( Logger...logs) {
		long now =  System.currentTimeMillis();
		long last = times.get(times.size() - 1);
		long deta = now - last;
		this.put("total", deta);

		for(Logger log : logs) {
			log.info(this);
		}
		return this;
	}
	
	/**
	 * 正常结果
	 * @param res
	 * @return
	 */
	public Watch res(Object res,  Logger...logs) {
		this.put("res", res);
		this.res(logs);
		return this;
	}
	/**
	 * 异常结果
	 * @param e
	 * @return
	 */
	public Watch exceptionWithThrow(Throwable e,  Logger...log) {
		this.put("exception", Tools.toString(e));
		this.res(log);
		throw new ErrorException(this);
	}

	/**
	 * 异常结果 不抛出异常
	 * @param e
	 * @return
	 */
	public Watch exception(Throwable e,  Logger...log) {
		this.put("exception", Tools.toString(e));
		this.res(log);
//		throw new ErrorException(this);
		return this;
	}
	public String toString() {
		return this.sb.toString();
	}

}
