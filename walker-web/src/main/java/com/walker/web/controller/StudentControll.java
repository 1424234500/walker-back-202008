package com.walker.web.controller;

import com.walker.common.util.Page;
import com.walker.service.BaseService;
import com.walker.service.StudentService;
import com.walker.web.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;


/**
 * 样例 student表 id-name-time属性 增删查改
 * 原始pw 和 跳转版本
 * @author Walker
 * 
 */  
@Controller
@RequestMapping("/student")
public class StudentControll extends BaseControll{
	public StudentControll() {
		super(StudentControll.class, "STUDENT");
	}


	@Autowired
	@Qualifier("baseService") 
	protected BaseService baseService;
	@Autowired
	@Qualifier("studentServiceHibernate") 
	StudentService studentServiceHibernate;
	@Autowired
	@Qualifier("studentServiceMybatis") 
	StudentService studentServiceMybatis;
	
	@RequestMapping("/student_cols.do")
	public void cols(HttpServletRequest request, HttpServletResponse response)  {
		String tableName = request.getParameter("TABLE_NAME");
		List<String> res = baseService.getColumnsByTableName(tableName);
		echo(res);
	}
	@RequestMapping("/student_list.do")
	public void listh(HttpServletRequest request, HttpServletResponse response)  {
		String id = request.getParameter("ID");
		String name = request.getParameter("NAME");
		String timefrom = request.getParameter("TIMEFROM");
		String timeto = request.getParameter("TIMETO");
		Page page = Page.getPage(request);
		
		
	    List<Map<String, Object>> res = studentServiceHibernate.finds(id, name, Context.YES, timefrom, timeto, page);
	   // logger.info(MapListHelp.list2string(res));
		echo(res, page);
	}
	@RequestMapping("/student_update.do")
	public void updateh(HttpServletRequest request,  PrintWriter pw) {
		String id = request.getParameter("ID"); 
		String name = request.getParameter("NAME");
		String time = request.getParameter("TIME");
	    log(request); 
	    
		int res = studentServiceHibernate.update(id, name, time);
		echo(res);
	}
	@RequestMapping("/student_delete.do")
	public void deleteh(HttpServletRequest request,  PrintWriter pw) {
		String id = request.getParameter("ID");  
	    log(request);

		int res = studentServiceHibernate.delete(id );
	   echo(res);
	}
	@RequestMapping("/student_add.do")
	public void addh(HttpServletRequest request,  PrintWriter pw) {
		String name = request.getParameter("NAME");
		String time = request.getParameter("TIME");
	    log(request);

		int res = studentServiceHibernate.add(name, time);
	    echo(res);
	}
	@RequestMapping("/student_get.do")
	public void geth(HttpServletRequest request,  PrintWriter pw) {
		String id = request.getParameter("ID");  
	    log(request);

		Map map = studentServiceHibernate.find(id );
		
	    echo(map);
	}
	   
	 
	@RequestMapping("/listm.do") 
	public void listm(HttpServletRequest request, Map<Object,Object> map)  {
		String id = request.getParameter("ID");
		String name = request.getParameter("NAME");
		String timefrom = request.getParameter("TIMEFROM");
		String timeto = request.getParameter("TIMETO");
	     
		Page page = Page.getPage(request);
		map.putAll(RequestUtil.getRequestBean(request));

	    List<Map<String, Object>> res = studentServiceMybatis.finds(id, name, Context.YES, timefrom, timeto, page);
		map.put("PAGE", page);

	   // logger.info(MapListHelp.list2string(res));
		map.put("res", res );
 
		echo(map);
	}
	@RequestMapping("/updatem.do")
	public void updatem(HttpServletRequest request,  PrintWriter pw) {
		String id = request.getParameter("ID"); 
		String name = request.getParameter("NAME");
		String time = request.getParameter("TIME");
	    log(request);

		int res = studentServiceMybatis.update(id, name, time);
		echo(res);
	}
	@RequestMapping("/deletem.do")
	public void deletem(HttpServletRequest request,  PrintWriter pw){
		String id = request.getParameter("ID");  
	    log(request);

		int res = studentServiceMybatis.delete(id );
	    echo(res);
	}
	@RequestMapping("/addm.do")
	public void addm(HttpServletRequest request,  PrintWriter pw){
		String name = request.getParameter("NAME");
		String time = request.getParameter("TIME");
	    log(request); 
	    
		int res = studentServiceMybatis.add(name, time);
	    echo(res);
	}
	@RequestMapping("/getm.do")
	public void getm(HttpServletRequest request,  PrintWriter pw){
		String id = request.getParameter("ID");  
	    log(request);

		Map map = studentServiceMybatis.find(id );
		echo(map);
	}
	    
	 
	public void log(HttpServletRequest request){
	    //logger.info(WebHelp.getRequestBean(request).toString()); 
	}
}