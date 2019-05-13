package com.walker.common.mode;

import java.util.*;


import com.walker.common.util.Tools;

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
	public Watch(String info) {
		this.times = new ArrayList<Long>();
		this.times.add(System.currentTimeMillis());
		sb = new StringBuilder();
	}
	public Watch put(Object str) {
		this.sb.append(str);
		return this;
	}
	public Watch put(Object key, Object value) {
		this.sb.append(" " + key + ":" + value);
		return this;
	}
	/**
	 * 分key耗时 查询操作耗时50ms
	 * @param key
	 * @return
	 */
	public long cost(Object key) {
		long now = System.currentTimeMillis();
		long last = times.get(0);
		long deta = now - last;
		this.put(key + " cost", deta);
		times.add(0, now);
		return deta;
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
	 * @return
	 */
	public Watch res() {
		long now = System.currentTimeMillis();
		long last = times.get(times.size() - 1);
		long deta = now - last;
		this.put("cost", deta);
		times.add(0, now);
		return this;
	}
	
	/**
	 * 正常结果
	 * @param res
	 * @return
	 */
	public Watch res(Object res) {
		this.put("res", res);
		this.res();
		return this;
	}
	/**
	 * 异常结果
	 * @param e
	 * @return
	 */
	public Watch exception(Throwable e) {
		this.put("exception", Tools.toString(e));
		this.res();
		return this;
	}
	
	public String toString() {
		return this.sb.toString();
	}

}
