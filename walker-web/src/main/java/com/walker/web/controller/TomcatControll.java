package com.walker.web.controller;

import com.walker.common.util.*;
import com.walker.core.cache.Cache;
import com.walker.core.cache.CacheMgr;
import com.walker.web.RequestUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * Tomcat监控后台
 * @author Walker
 *
 */
@Controller
@RequestMapping("/tomcat")
public class TomcatControll extends BaseControll{    
  
	public TomcatControll( ) {
		super(TomcatControll.class, "");
	}
	
	Cache<String> cache =  CacheMgr.getInstance();
	
	/**
	 * 缓存监控 map实现
	 */
	@RequestMapping("/addCacheMap.do") 
	public void addCacheMap(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Bean args = RequestUtil.getParam(request, new String[]{"URL", "KEY", "VALUE", "EXPIRE", "TYPE"});
		String url = args.get("URL", "");
		String key = args.get("KEY", "");
		Object value = args.get("VALUE", new Bean());
		long expire = args.get("EXPIRE", 10000L);
		int type = args.get("TYPE", 1); // str / obj
		if(type == 1){
			value = JsonUtil.get(value.toString());
		}
		String res = cache.put(url, key, value, expire);
		if(res.equals("true")){
			echo(value);
		}else{
			echo(false, res);
		}
		
	}
	@RequestMapping("/deleteCacheMap.do") 
	public void deleteCacheMap(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String url = RequestUtil.getKey(request, "URL");
		String key = RequestUtil.getKey(request, "KEY");
		
		String res = cache.remove(url, key);
		if(res.equals("true")){
			echo("true");
		}else{
			echo(false, res);
		}
	}
	@RequestMapping("/listCacheMap.do") 
	public void listCacheMap(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Bean map = RequestUtil.getParam(request); //, new String[]{"URL", "KEY", "VALUE", "EXPIRE", "TYPE"}
		Page page = Page.getPage(request);
		Bean res = cache.findCacheList(map);
		page.setNUM(res.get("size", 0));
		res.put("page", page);
		echo(res);
	}


	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/statis.do") 
	public void statis(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String url = request.getParameter("URL"); 
		
	 	List list = null;
	 	if(Tools.notNull(url) && !url.toLowerCase().equals("undefined") && !url.toLowerCase().equals("null")){
	 		if(baseService.getDs().equals("oracle")) {
		 		list = MapListUtil.toArrayAndTurn(baseService.find(" SELECT LEV, NVL(TIME, '0') TIME FROM  ( SELECT HOUR, CAST(SUM(COSTTIME)/SUM(COUNT)/1000 AS NUMBER(8, 3)) TIME FROM (  SELECT  TO_CHAR(LT.TIME, 'HH24') HOUR, LT.COUNT, LT.COSTTIME FROM W_LOG_TIME LT WHERE 1=1 AND LT.URL=?  )GROUP BY HOUR   ) T1,  ( SELECT LPAD(LEVEL, 2, '0') LEV FROM DUAL CONNECT BY LEVEL <= 24    ) T2 WHERE T1.HOUR(+) = T2.LEV  ORDER BY LEV ", url)) ;
	 		}else {
		 		list = MapListUtil.toArrayAndTurn(baseService.find(
		 				" SELECT T2.LEV, IFNULL(T1.TIME, '0') TIME FROM  "
				 				+" ( SELECT HOUR, (0 + SUM(COSTTIME)/SUM(COUNT)/1000) TIME FROM (  SELECT  SUBSTR(LT.TIME,12,2) HOUR, LT.COUNT, LT.COSTTIME FROM W_LOG_TIME LT WHERE 1=1 AND LT.URL=?  ) T GROUP BY HOUR   ) T1"
				 				+" RIGHT JOIN "
				 				+" ( SELECT LPAD(LEVEL, 2, '0') LEV FROM (SELECT  (@I/*'*/\\:=/*'*/@I+1) LEVEL FROM  INFORMATION_SCHEMA.COLUMNS T ,(SELECT   @I/*'*/\\:=/*'*/0) IT ) T  WHERE LEVEL<=24  ) T2" 
				 				+" ON"
				 				+" T1.HOUR=T2.LEV ORDER BY LEV	"
		 				
		 				, url)) ;
		 		
//		 		SELECT t2.lev, ifnull(t1.time, '0') time FROM  
//		 		( SELECT hour, (0 + sum(costtime)/sum(count)/1000) time FROM (  SELECT  substr(lt.time,12,2) hour, lt.count, lt.costtime FROM W_LOG_TIME lt where 1=1 and lt.url='/table/list.do'  ) t group by hour   ) t1
//		 		right join 
//		 		( select lpad(level, 2, '0') lev from (select  (@i/*'*/:=/*'*/@i+1) level from  information_schema.COLUMNS t ,(select   @i/*'*/:=/*'*/0) it ) t  where level<=24  ) t2 
//		 		on
//		 		t1.hour=t2.lev ORDER BY t2.lev
	 		}
	 	}else{
	 		if(baseService.getDs().equals("oracle")) {
		 		list = MapListUtil.toArrayAndTurn(baseService.find("SELECT URL,CAST(SUM(COSTTIME)/SUM(COUNT)/1000 AS NUMBER(8, 3)) TIME FROM W_LOG_TIME WHERE 1=1 GROUP BY URL ORDER BY URL ")) ;
	 		}else {
		 		list = MapListUtil.toArrayAndTurn(baseService.find("SELECT URL,(0 + SUM(COSTTIME)/SUM(COUNT)/1000) TIME FROM W_LOG_TIME WHERE 1=1 GROUP BY URL ORDER BY URL ")) ;
	 		}
	 	} 
		 
	 	 
	 	List listLineNames = MapListUtil.array().add("action").build();
		List listSeries =  MapListUtil.array().add(list.size() > 0 ? (List) list.get(1) : new Bean()).build();
		String type = "bar";	 
		Map title = MapListUtil.map().put("text", "操作耗时统计").build();		//标题
		Map legend = MapListUtil.map().put("data", listLineNames).build();   //线条名字集合
		Map xAxis = MapListUtil.map().put("data", list.size() > 0 ? (List) list.get(0) : new Bean()).build();  	//x坐标集合 多线条共x轴
		List series = MapListUtil.array().build();
		for(int i = 0; i < listSeries.size(); i++){
			//type = i / 2 == 0 ? "bar" : "line"; 
			series.add(MapListUtil.map()
					.put("name", listLineNames.get(i))	//该线条的名字
					.put("type", type)					//该线条的显示方式line bar pie
					.put("data", listSeries.get(i))			//该线条的y值集合
					.build() 
				);
		} 
		Map option = MapListUtil.map()
				.put("title", title)  
				.put("legend", legend)		
				.put("tooltip", new Bean())  
				.put("xAxis", xAxis) 
				.put("yAxis", new HashMap()) //若无报错YAxis 0 not found
				.put("series", series) 
				.build();
		 

		Map res = MapListUtil.getMap()
				.put("res", "true")
				.put("option", option) 
				.put("info", RequestUtil.getRequestBean(request)).build(); 
		log(res);
		echo(res);
	}	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/statiscount.do") 
	public void statiscount(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String url = request.getParameter("URL"); 
		
	 	List list = null;
	 	if(Tools.notNull(url) && !url.toLowerCase().equals("undefined") && !url.toLowerCase().equals("null")){
	 		if(baseService.getDs().equals("oracle")) {
	 			list = MapListUtil.toArrayAndTurn(baseService.find("  SELECT LEV, NVL(COUNT, '0') SUMCOUNT FROM  ( SELECT HOUR, SUM(COUNT) COUNT FROM (  SELECT  TO_CHAR(LT.TIME, 'HH24') HOUR, LT.URL, LT.COUNT FROM W_LOG_TIME LT WHERE 1=1 AND LT.URL=?  )GROUP BY HOUR ) T1,  ( SELECT LPAD(LEVEL, 2, '0') LEV FROM DUAL CONNECT BY LEVEL <= 24    ) T2 WHERE T1.HOUR(+) = T2.LEV  ORDER BY LEV ", url)) ;
	 		}else {
	 			list = MapListUtil.toArrayAndTurn(baseService.find("  "
	 					+" SELECT T2.LEV, IFNULL(T1.COUNT, '0') SUMCOUNT FROM  "
	 					+" ( SELECT HOUR, SUM(COUNT) COUNT FROM (  SELECT  SUBSTR(LT.TIME,6,2) HOUR, LT.URL, LT.COUNT FROM W_LOG_TIME LT WHERE 1=1 AND LT.URL=?  ) T GROUP BY HOUR ) T1"
	 					+" RIGHT JOIN" 
	 					+" ( SELECT LPAD(LEVEL, 2, '0') LEV FROM (SELECT  (@I/*'*/\\:=/*'*/@I+1) LEVEL FROM  INFORMATION_SCHEMA.COLUMNS T ,(SELECT   @I/*'*/\\:=/*'*/0) IT ) T  WHERE LEVEL<=12  ) T2" 
	 					+" ON"
	 					+" T1.HOUR = T2.LEV  ORDER BY T2.LEV"
	 					+ "", url)) ;
	 		}
//	 		SELECT t2.lev, ifnull(t1.count, '0') sumcount FROM  
//	 		( SELECT hour, sum(count) count FROM (  SELECT  substr(lt.time,6,2) hour, lt.url, lt.count FROM W_LOG_TIME lt where 1=1 and lt.url='/table/list.do'  ) t group by hour ) t1
//	 		right join 
//	 		( select lpad(level, 2, '0') lev from (select  (@i/*'*/\\:=/*'*/@i+1) level from  information_schema.COLUMNS t ,(select   @i/*'*/\\:=/*'*/0) it ) t  where level<=12  ) t2 
//	 		on
//	 		t1.hour = t2.lev  ORDER BY t2.lev
 		}else{
	 		if(baseService.getDs().equals("oracle")) {
	 			list = MapListUtil.toArrayAndTurn(baseService.find(" SELECT URL,SUM(COUNT) SUMCOUNT FROM W_LOG_TIME WHERE 1=1 GROUP BY URL ORDER BY URL ")) ;
	 		}else {
	 			list = MapListUtil.toArrayAndTurn(baseService.find(" SELECT URL,SUM(COUNT) SUMCOUNT FROM W_LOG_TIME WHERE 1=1 GROUP BY URL ORDER BY URL ")) ;
	 		}
 		}  
		
	 	 
	 	List listLineNames = MapListUtil.array().add("action").build();
		List listSeries =  MapListUtil.array().add(list.size() > 0 ? (List) list.get(1) : new Bean()).build();
		String type = "bar";	 
		Map title = MapListUtil.map().put("text", "操作频率统计").build();		//标题
		Map legend = MapListUtil.map().put("data", listLineNames).build();   //线条名字集合
		Map xAxis = MapListUtil.map().put("data", list.size() > 0 ? (List) list.get(0) : new Bean()).build();  	//x坐标集合 多线条共x轴
		List series = MapListUtil.array().build();
		for(int i = 0; i < listSeries.size(); i++){
			//type = i / 2 == 0 ? "bar" : "line"; 
			series.add(MapListUtil.map()
					.put("name", listLineNames.get(i))	//该线条的名字
					.put("type", type)					//该线条的显示方式line bar pie
					.put("data", listSeries.get(i))			//该线条的y值集合
					.build() 
				);
		} 
		Map option = MapListUtil.map()
				.put("title", title)  
				.put("legend", legend)		
				.put("tooltip", new HashMap()) 
				.put("xAxis", xAxis) 
				.put("yAxis", new HashMap()) //若无报错YAxis 0 not found
				.put("series", series) 
				.build();
		 

		Map res = MapListUtil.getMap()
				.put("res", "true")
				.put("option", option) 
				.put("info", RequestUtil.getRequestBean(request)).build(); 
		log(res);
		echo(res);
	}	
	
}