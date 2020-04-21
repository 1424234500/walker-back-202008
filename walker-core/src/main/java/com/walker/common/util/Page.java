package com.walker.common.util;

import com.walker.core.aop.FunArgsS;

import java.io.Serializable;
import java.util.List;

public class Page implements Serializable{
	private static final long serialVersionUID = 1L;
	static int showNumDefault = 10;
	/**
	 * 总数据条数
	 */
	private long num = 0;
	/**
	 * 每页数量
	 */
	private int shownum = 5;
	/**
	 * 当前页码
	 */
	private int nowpage = 1;
	/**
	 * 总页数
	 */
	private int pagenum = 0;
	/**
	 * 排序	id, name desc, time asc 空则不排序
	 */
	private String order = "";
	public String toString() {
		return this.toBean().toString();
	}
	public Page(){
		shownum = 10;
	}
	
	public Page(int shownum, long allNum){
		this.shownum = shownum;
		this.setNum(allNum);
	}
	
	public Bean toBean(){
		return new Bean().put("num", num).put("shownum", shownum).put("nowpage", nowpage).put("order", order);
	}
	
//	/**
//	 * 通过request获取 查询第几页 每页多少条
//	 */
//	public static Page getPage(HttpServletRequest request){
//		Page res = new Page();
//		res.setNowpage(request.getParameter("nowpage"));
//		res.setShownum(request.getParameter("shownum"));
//		res.setOrder(request.getParameter("order"));
//		return res;
//	}
	/**
	 * 通过request获取 查询第几页 每页多少条
	 */
	public static Page getPage(Bean bean){
		Page res = new Page();
		res.setNowpage(bean.get("nowpage", "0"));
		res.setShownum(bean.get("shownum", "0"));
		res.setOrder(bean.get("order", ""));
		return res;
	}
	
	public int getStart(){
		return (nowpage -1) * shownum;
	}
	public int getStop(){
		return nowpage * shownum;
	}
	public long getNum() {
		return num;
	} 
	/**
	 * 设置预期数据的总数量 并根据页显示数量更新总页数 
	 * @param num
	 */
	public Page setNum(long num) {
		this.num = num;
		this.pagenum = (int) Math.ceil( 1.0 * num / this.shownum);
		return this;
	}

	public int getShownum() {
		return shownum;
	}

	public Page setShownum(Object eachPageNum) {
		int defaultShowNum = Page.showNumDefault;
		this.shownum = LangUtil.turn(eachPageNum, Page.showNumDefault);
		if(this.shownum <= 0){
			this.shownum = defaultShowNum;
		}
		return this;
	}

	public int getNowpage() {
		return nowpage;
	}

	public Page setNowpage(int nowPage){
		this.nowpage = nowPage;
		if(this.nowpage < 1){
			this.nowpage = 1;
		}
		return this;
	}

	public Page setNowpage(String nowPage) {
		return setNowpage(Tools.parseInt(nowPage, 1));
	}

	public int getPagenum() {
		return pagenum;
	}

	public Page setPagenum(Object pageNum) {
		this.pagenum = LangUtil.turn(pageNum, 0);return this;
	}
	public String getOrder(String defaultValue) {
		String res = this.getOrder();
		if(res.length() <= 0) {
			res = defaultValue;
		}
		return res;
	}
	public String getOrder() {
		return order;
	}

	/**
	 * 过滤非法字符串 sql注入
	 * @param order
	 * @return
	 */
	public Page setOrder(String order) {
		order = order.replace('\'', ' ');
		order = order.replace('&', ' ');
		order = order.replace('|', ' ');
		order = order.replace('"', ' ');
		this.order = order;
		return this;
	}


	/**
	 * 分页回调
	 * @param collection
	 * @param batchSize
	 * @param fun
	 * @param <T>
	 * @return
	 */
	public static <T> int batch(List<T> collection, int batchSize, FunArgsS<List<T>, Integer> fun){
		int res = 0;
		assert collection != null;
		assert collection.size() > 0;
		assert batchSize > 0;

		if(batchSize <= 0){
			Tools.out("batch batchSize is null ");
		}else if(collection == null || collection.size() <= 0){
			Tools.out("batch collection is null ");
		}else{
			res = (int) Math.ceil(1d * collection.size() / batchSize);
			if(fun != null){
//				for(int i = 0; i < collection.size(); i+=batchSize){
//					fun.make(collection.subList(i, Math.min(i+batchSize, collection.size())));
//				}
				for(int pageNow = 0; pageNow < res; pageNow++){
					fun.make(collection.subList(pageNow*batchSize, Math.min(pageNow*batchSize+batchSize, collection.size())), pageNow);
				}
			}
		}
		return res;
	}


}