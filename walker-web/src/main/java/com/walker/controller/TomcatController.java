package com.walker.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.walker.Response;
import com.walker.dao.JdbcDao;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.walker.common.util.Bean;
import com.walker.common.util.JsonUtil;
import com.walker.common.util.MapListUtil;
import com.walker.common.util.Page;
import com.walker.common.util.Tools;
import com.walker.core.cache.Cache;
import com.walker.core.cache.CacheMgr;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Tomcat监控后台
 * @author Walker
 *
 */
@Controller
@RequestMapping("/tomcat")
public class TomcatController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    JdbcDao jdbcDao;
    
    @ApiOperation(value = "统计mc访问", notes = "")
    @ResponseBody
    @RequestMapping(value = "/statics.do", method = RequestMethod.GET)
    public Response statics(
            @RequestParam(value = "url", required = false, defaultValue = "") String url
    ){

        List list = null;
        if (Tools.notNull(new Object[]{url}) && !url.toLowerCase().equals("undefined") && !url.toLowerCase().equals("null")) {
            if (jdbcDao.getDs().equals("oracle")) {
                list = MapListUtil.toArrayAndTurn(jdbcDao.find(" SELECT LEV, NVL(TIME, '0') TIME FROM  ( SELECT HOUR, CAST(SUM(COSTTIME)/SUM(COUNT)/1000 AS NUMBER(8, 3)) TIME FROM (  SELECT  TO_CHAR(LT.TIME, 'HH24') HOUR, LT.COUNT, LT.COSTTIME FROM W_LOG_TIME LT WHERE 1=1 AND LT.URL=?  )GROUP BY HOUR   ) T1,  ( SELECT LPAD(LEVEL, 2, '0') LEV FROM DUAL CONNECT BY LEVEL <= 24    ) T2 WHERE T1.HOUR(+) = T2.LEV  ORDER BY LEV ", new Object[]{url}));
            } else {
                list = MapListUtil.toArrayAndTurn(jdbcDao.find(" SELECT T2.LEV, IFNULL(T1.TIME, '0') TIME FROM   ( SELECT HOUR, (0 + SUM(COSTTIME)/SUM(COUNT)/1000) TIME FROM (  SELECT  SUBSTR(LT.TIME,12,2) HOUR, LT.COUNT, LT.COSTTIME FROM W_LOG_TIME LT WHERE 1=1 AND LT.URL=?  ) T GROUP BY HOUR   ) T1 RIGHT JOIN  ( SELECT LPAD(LEVEL, 2, '0') LEV FROM (SELECT  (@I/*'*/\\:=/*'*/@I+1) LEVEL FROM  INFORMATION_SCHEMA.COLUMNS T ,(SELECT   @I/*'*/\\:=/*'*/0) IT ) T  WHERE LEVEL<=24  ) T2 ON T1.HOUR=T2.LEV ORDER BY LEV\t", new Object[]{url}));
            }
        } else if (jdbcDao.getDs().equals("oracle")) {
            list = MapListUtil.toArrayAndTurn(jdbcDao.find("SELECT URL,CAST(SUM(COSTTIME)/SUM(COUNT)/1000 AS NUMBER(8, 3)) TIME FROM W_LOG_TIME WHERE 1=1 GROUP BY URL ORDER BY URL ", new Object[0]));
        } else {
            list = MapListUtil.toArrayAndTurn(jdbcDao.find("SELECT URL,(0 + SUM(COSTTIME)/SUM(COUNT)/1000) TIME FROM W_LOG_TIME WHERE 1=1 GROUP BY URL ORDER BY URL ", new Object[0]));
        }

        List listLineNames = MapListUtil.array().add("action").build();
        List listSeries = MapListUtil.array().add(list.size() > 0 ? (List)list.get(1) : new Bean()).build();
        String type = "bar";
        Map title = MapListUtil.map().put("text", "操作耗时统计").build();
        Map legend = MapListUtil.map().put("data", listLineNames).build();
        Map xAxis = MapListUtil.map().put("data", list.size() > 0 ? (List)list.get(0) : new Bean()).build();
        List series = MapListUtil.array().build();

        for(int i = 0; i < listSeries.size(); ++i) {
            series.add(MapListUtil.map().put("name", listLineNames.get(i)).put("type", type).put("data", listSeries.get(i)).build());
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
                .build();
        log.info(res.toString());

        return Response.makeTrue(url, res);
    }

    @ApiOperation(value = "统计mc访问耗时", notes = "")
    @ResponseBody
    @RequestMapping(value = "/staticscount.do", method = RequestMethod.GET, produces = "application/json")
    public Response staticscount(
            @RequestParam(value = "url", required = false, defaultValue = "") String url
    ){
        List list = null;
        if (Tools.notNull(new Object[]{url}) && !url.toLowerCase().equals("undefined") && !url.toLowerCase().equals("null")) {
            if (jdbcDao.getDs().equals("oracle")) {
                list = MapListUtil.toArrayAndTurn(jdbcDao.find("  SELECT LEV, NVL(COUNT, '0') SUMCOUNT FROM  ( SELECT HOUR, SUM(COUNT) COUNT FROM (  SELECT  TO_CHAR(LT.TIME, 'HH24') HOUR, LT.URL, LT.COUNT FROM W_LOG_TIME LT WHERE 1=1 AND LT.URL=?  )GROUP BY HOUR ) T1,  ( SELECT LPAD(LEVEL, 2, '0') LEV FROM DUAL CONNECT BY LEVEL <= 24    ) T2 WHERE T1.HOUR(+) = T2.LEV  ORDER BY LEV ", new Object[]{url}));
            } else {
                list = MapListUtil.toArrayAndTurn(jdbcDao.find("   SELECT T2.LEV, IFNULL(T1.COUNT, '0') SUMCOUNT FROM   ( SELECT HOUR, SUM(COUNT) COUNT FROM (  SELECT  SUBSTR(LT.TIME,6,2) HOUR, LT.URL, LT.COUNT FROM W_LOG_TIME LT WHERE 1=1 AND LT.URL=?  ) T GROUP BY HOUR ) T1 RIGHT JOIN ( SELECT LPAD(LEVEL, 2, '0') LEV FROM (SELECT  (@I/*'*/\\:=/*'*/@I+1) LEVEL FROM  INFORMATION_SCHEMA.COLUMNS T ,(SELECT   @I/*'*/\\:=/*'*/0) IT ) T  WHERE LEVEL<=12  ) T2 ON T1.HOUR = T2.LEV  ORDER BY T2.LEV", new Object[]{url}));
            }
        } else if (jdbcDao.getDs().equals("oracle")) {
            list = MapListUtil.toArrayAndTurn(jdbcDao.find(" SELECT URL,SUM(COUNT) SUMCOUNT FROM W_LOG_TIME WHERE 1=1 GROUP BY URL ORDER BY URL ", new Object[0]));
        } else {
            list = MapListUtil.toArrayAndTurn(jdbcDao.find(" SELECT URL,SUM(COUNT) SUMCOUNT FROM W_LOG_TIME WHERE 1=1 GROUP BY URL ORDER BY URL ", new Object[0]));
        }

        List listLineNames = MapListUtil.array().add("action").build();
        List listSeries = MapListUtil.array().add(list.size() > 0 ? (List)list.get(1) : new Bean()).build();
        String type = "bar";
        Map title = MapListUtil.map().put("text", "操作频率统计").build();
        Map legend = MapListUtil.map().put("data", listLineNames).build();
        Map xAxis = MapListUtil.map().put("data", list.size() > 0 ? (List)list.get(0) : new Bean()).build();
        List series = MapListUtil.array().build();

        for(int i = 0; i < listSeries.size(); ++i) {
            series.add(MapListUtil.map().put("name", listLineNames.get(i)).put("type", type).put("data", listSeries.get(i)).build());
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
                .build();

        log.info(res.toString());

        return Response.makeTrue(url, res);
    }

}