package com.walker.web.controller;

import com.walker.common.util.Bean;
import com.walker.common.util.MapListUtil;
import com.walker.common.util.Page;
import com.walker.common.util.Tools;
import com.walker.service.StudentService;
import com.walker.web.RequestUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/** 
 * 测试AngularJs的后台
 * @author Walker
 *
 */
@Controller
@RequestMapping("/angular")
public class AngularControll extends BaseControll{   
	public AngularControll() {
		super(AngularControll.class, "");
	}

	@Autowired
	@Qualifier("studentServiceHibernate") 
	StudentService studentServiceHibernate;

	

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/statis.do") 
	public void statis(HttpServletRequest request, HttpServletResponse response) { 
//		        legend: {      data: ['线条1', '线条2']   },
//		        xAxis: {  data: xNames  }, 
//		        series: [   
//		                 { name: lineName,    type: 'line',   data: lineValues,  },
//		                 { name: lineName,    type: 'line',   data: lineValues,  }, 
		
		//x1, x2, x3, x4
		
		List listXs = null;
 		if(baseService.getDs().equals("oracle")) {
 			listXs = MapListUtil.toArrayAndTurn(baseService.find("SELECT LPAD(LEVEL, 2, '0') LEV FROM DUAL CONNECT BY LEVEL <=12 ")).get(0);
 		}else {
 			listXs = MapListUtil.toArrayAndTurn(baseService.find(" SELECT LPAD(LEVEL, 2, '0') LEV FROM (SELECT  (@I/*'*/:=/*'*/@I+1) LEVEL FROM  INFORMATION_SCHEMA.COLUMNS T ,(SELECT   @I/*'*/:=/*'*/0) IT ) T  WHERE LEVEL<=12  ")).get(0) ;
 		}
		
		List listLineNames = MapListUtil.array().build();
		int yearFrom = Tools.parseInt(request.getParameter("TIMEFROM"));
		int yearTo = Tools.parseInt(request.getParameter("TIMETO")); 
		List listSeries = MapListUtil.array().build(); 
		for(int i = yearFrom; i <= yearTo; i++){
			
			if(baseService.getDs().equals("oracle")) {
				//yi-1, yi-2, yi-3, yi-4 只查询y轴序列
				listSeries = MapListUtil.listAdd(listSeries,  MapListUtil.toArrayAndTurn(baseService.find(
						"SELECT NVL(T1.Y, '0') Y FROM ( SELECT T.XS X,COUNT(*) Y FROM ( SELECT S.*,TO_CHAR(S.TIME, 'MM') XS FROM STUDENT S WHERE TO_CHAR(S.TIME, 'YYYY')=?  ) T GROUP BY T.XS ) T1,( SELECT LPAD(LEVEL, 2, '0') LEV FROM DUAL CONNECT BY LEVEL <=12   ) T2 WHERE T1.X(+) = T2.LEV ORDER BY T2.LEV  "
						, i)) ); 
			}else {
				//yi-1, yi-2, yi-3, yi-4 只查询y轴序列
				listSeries = MapListUtil.listAdd(listSeries,  MapListUtil.toArrayAndTurn(baseService.find(
						"SELECT IFNULL(T2.Y, '0') Y FROM "
								+" ( SELECT LPAD(LEVEL, 2, '0') LEV FROM (SELECT  (@I/*'*/:=/*'*/@I+1) LEVEL FROM  INFORMATION_SCHEMA.COLUMNS T ,(SELECT   @I/*'*/:=/*'*/0) IT ) T  WHERE LEVEL<=12   ) T1"
								+" LEFT JOIN "
								+" ( SELECT T.MON X,COUNT(*) Y FROM ( SELECT S.*,SUBSTR(S.S_MTIME, 6,2) MON FROM STUDENT S WHERE SUBSTR(S.S_MTIME,1,4)=?  ) T GROUP BY T.MON ) T2"
								+" ON"
								+" T1.LEV=T2.X ORDER BY T1.LEV" 
						, i)) ); 
//SELECT ifnull(t2.y, '0') y FROM 
//( select lpad(level, 2, '0') lev from (select  (@i/*'*/:=/*'*/@i+1) level from  information_schema.COLUMNS t ,(select   @i/*'*/:=/*'*/0) it ) t  where level<=12   ) t1
//left join 
//( SELECT t.mon x,count(*) y FROM ( SELECT s.*,substr(s.s_mtime, 6,2) mon FROM student s where substr(s.s_mtime,1,4)='2019'  ) t group by t.mon ) t2
//on
//t1.lev=t2.x order by t1.lev 
				
	 		}
			
			
			listLineNames.add(i + ""); 
		}
		//共x轴 多线数据 
		//x1, x2, x3, x4
		//y1, y2, y3, y4 
		//y5, y6, y7, y8   
		String type = "bar";	
		Map title = MapListUtil.map().put("text", "注册统计").build();		//标题
		Map legend = MapListUtil.map().put("data", listLineNames).build();   //线条名字集合
		Map xAxis = MapListUtil.map().put("data", listXs).build();  	//x坐标集合 多线条共x轴
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
				.put("tooltip", new Bean()) //若无则不能预览
				.put("xAxis", xAxis) 
				.put("yAxis",  new Bean()) //若无则报错YAxis 0 not found
				.put("series", series) 
				.build();
		 

		Map res = MapListUtil.getMap()
				.put("res", "true")
				.put("option", option) 
				.put("info", RequestUtil.getRequestBean(request)).build(); 
		log(res);
		echo(res);
	}	
	
	
	@RequestMapping("/login.do") 
	public void login(HttpServletRequest request, HttpServletResponse response) { 
		Map res = MapListUtil.getMap().put("res", "true").put("info",RequestUtil.getRequestBean(request)).build(); 
		log(res);
		echo(res);
	}	
	 
	@RequestMapping("/update.do")
	public void update(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("ID"); 
		String name = request.getParameter("NAME");
		String time = request.getParameter("TIME");
	    
	    Map res = null;
	    if(studentServiceHibernate.find(id).isEmpty()){
	    	res = MapListUtil.getMap().put("res",studentServiceHibernate.add(name, time)).build();
	    }else{
	    	res = MapListUtil.getMap().put("res",studentServiceHibernate.update(id, name, time)).build();
	    }
		echo(res);
	}
	@RequestMapping("/delete.do")
	public void delete(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("ID");
	    
	    Map res = MapListUtil.getMap().put("res",studentServiceHibernate.delete(id)).build();
		echo(res);
	}	
	@RequestMapping("/get.do")
	public void get(HttpServletRequest request, HttpServletResponse response) {
		//Map res = MapListHelp.getMap().put("id", "id-001").put("key", "key-001").put("username", "username-001").build();
		String id = request.getParameter("id");   
		Map res = studentServiceHibernate.find(id );
		log(res); 
		echo(res);
	}	
	@RequestMapping("/list.do")
	public void list(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("ID");
		String name = request.getParameter("NAME");
		String timefrom = request.getParameter("TIMEFORM");
		String timeto = request.getParameter("TIMETO");
		
		Page page = Page.getPage(request);
		List<Map<String, Object>> list = studentServiceHibernate.finds(id, name, Context.YES, timefrom, timeto, page);
		log(list, page);
		echo(list, page);
	}
	@RequestMapping("/listrecent.do")
	public void listRecent(HttpServletRequest request, HttpServletResponse response) {
	} 
	
	static public Logger logger = Logger.getLogger(AngularControll.class); 

	@Override
	public void log(Object... objs) {
		 logger.info(Tools.objects2string(objs));
	}
    
}