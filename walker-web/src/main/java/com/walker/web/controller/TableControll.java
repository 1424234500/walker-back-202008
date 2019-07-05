package com.walker.web.controller;

import com.walker.common.util.Bean;
import com.walker.common.util.Page;
import com.walker.common.util.Tools;
import com.walker.core.database.SqlUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/table")
public class TableControll extends BaseControll {

	
	public TableControll() {
		super(TableControll.class, "");
	} 
	
	
	
	@RequestMapping("/statis.do") 
	public void statis(HttpServletRequest request, HttpServletResponse response) throws IOException { 
	}	
	@RequestMapping("/find.do") 
	public void exe(HttpServletRequest request, HttpServletResponse response){ 
			String sql = getValue(request, "sql");
			sql = SqlUtil.filter(sql);
			try {
				String sqlm = sql.toUpperCase();
				if(sqlm.indexOf("UPDATE") >= 0 || sqlm.indexOf("DELETE") >= 0) {
					int res = baseService.executeSql(sql);
					echo(true, "", new Bean().set("info", "执行结果: " + res));
				}else {
					Page page = Page.getPage(request);
					List<String> cols = baseService.getColumnsBySql(sql);
					List<Map<String, Object>> list = baseService.findPage(page, sql);
					echo(true, "查询结果: ", new Bean().set("cols", cols).set("list", list).set("page", page));
				}
			}catch(Exception e) {
				String info = "sql:    \n" + sql + "\n    Exception:    \n" + Tools.toString(e);
				log.info(info);
				echo(false, info);
			}
		
	}
	
}