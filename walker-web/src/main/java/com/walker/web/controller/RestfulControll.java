package com.walker.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * restful模式接口
 */
@Controller
@RequestMapping("/restful")
public class RestfulControll extends BaseControll{
 
	public RestfulControll() {
		super(RestfulControll.class, "STUDENT");

	}

	@RequestMapping(value="/{id}/make.do",method=RequestMethod.GET)
	public void get(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws IOException{
	    echo(baseService.findOne("select * from STUDENT where ID=? ", id));
	}

	@RequestMapping(value="/make.do",method=RequestMethod.POST) //, produces = "application/json")
	public void post(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String id = getValue(request, "ID");
		String name = getValue(request, "NAME");
		String time = getValue(request, "TIME");
		echo(baseService.executeSql("insert into STUDENT(ID,NAME) values(?,?)", id, name));
	}
	
	@RequestMapping(value="/make.do",method=RequestMethod.PUT)
	public void put(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String id = getValue(request, "ID");
		String name = getValue(request, "NAME");
		String time = getValue(request, "TIME");
		echo(baseService.executeSql("update STUDENT set ID=?,NAME=? where ID=? ", id, name, id));
	}

	@RequestMapping(value="/{id}/make.do",method=RequestMethod.DELETE)
	public void delete(HttpServletRequest request, HttpServletResponse response, @PathVariable String id) throws IOException{
	    echo(baseService.executeSql("delete from STUDENT where ID=? ", id));
	}

    
}