package com.walker.controller;


import com.walker.Response;
import com.walker.common.util.Bean;
import com.walker.common.util.Page;
import com.walker.core.cache.Cache;
import com.walker.core.cache.CacheMgr;
import com.walker.core.database.SqlUtil;
import com.walker.dao.JdbcDao;
import com.walker.event.RequestUtil;
import com.walker.service.BaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/*
测试 jap teacherService

 */
@Api(value = "共用工具mysql ddl ")
@Controller
@RequestMapping("/common")
public class CommonController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("baseService")
    private BaseService baseService;

    /**
     * shardingjdbc拦截mysql其他用户 为默认用户
     */
    @Autowired
    @Qualifier("jdbcDao")
    private JdbcDao jdbcDao;


    @ApiOperation(value = "获取需要前段展示的表列名 备注名", notes = "")
    @ResponseBody
    @RequestMapping(value = "/getColsMap.do", method = RequestMethod.GET)
    public Response getColsMap(
            @RequestParam(value = "tableName", required = true, defaultValue = "W_TEACHER") String tableName
    ) {
        return Response.makeTrue(tableName, this.getColsMapCache(tableName));
    }
    public Map<String, String> getColsMapCache(String tableName){
        return CacheMgr.getInstance().getFun("jdbcDao.getColumnsMapByTableName:" + tableName, new Cache.Function() {
            @Override
            public Map<String, String> cache() {
                Map<String, String> map = jdbcDao.getColumnsMapByTableName(tableName);
                Map<String, String> res = new LinkedHashMap<>();
                for(String key : map.keySet()){
                    res.put(key.toUpperCase(), map.get(key));
                }
                log.info(tableName);
                log.info(res.toString());
                return res;
            }
        });
    }


    @ApiOperation(value = "获取表列表", notes = "")
    @ResponseBody
    @RequestMapping(value = "/getTables.do", method = RequestMethod.GET)
    public Response getTables(
            @RequestParam(value = "_DATABASE_", required = true, defaultValue = "") String database
    ) {
        Object res = CacheMgr.getInstance().getFun("jdbcDao.showTables:" + database , new Cache.Function() {
            @Override
            public Object cache() {
                List<Map<String, Object>> t = jdbcDao.find(
                        "SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA =? "
                        , database);
                List<Object> res = new ArrayList<>();
                for(Map<String, Object> item : t){
                    res.add(item.get("table_name"));
                }
                log.info(res.toString());
                return res;
            }
        });

        return Response.makeTrue("", res);
    }

    @ApiOperation(value = "获取数据库/用户列表", notes = "")
    @ResponseBody
    @RequestMapping(value = "/getDatabases.do", method = RequestMethod.GET)
    public Response getDatabases(  ) {
        Object res = CacheMgr.getInstance().getFun("jdbcDao.showDatabases:" , new Cache.Function() {
            @Override
            public Object cache() {
//                List<String> res = jdbcDao.getColumnsBySql("show databases ");
//                log.info(res.toString());
//                return res;
                return Arrays.asList("walker,walker0,walker1,walker2,walker3".split(","));
            }
        });

        return Response.makeTrue("", res);
    }

    public static final String KEY_DB = "_DATABASE_";
    public static final String KEY_TABLE = "_TABLE_NAME_";
//    public static final String KEY_ID = "ID";
    @ApiOperation(value = "post 保存 更新/添加 ", notes = "通用存储")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public Response save(HttpServletRequest request) {
        Bean bean = RequestUtil.getRequestBean(request);
        String database = bean.get(KEY_DB, "");
        String tableName = bean.get(KEY_TABLE, "");
        if(tableName.length() <= 0){
            return Response.makeFalse(KEY_TABLE + "为空");
        }
        this.delet(request);    //先删除再插入
        if(database.length() > 0) tableName = database + "." + tableName;
        bean.remove(KEY_TABLE);
        bean.remove(KEY_DB);

        Bean colsMap = new Bean(this.getColsMapCache(tableName));
        //insert into student(ID, NAME, TIME) values(?,?,?)
        StringBuilder sb = new StringBuilder("insert into " + tableName + "(");
        StringBuilder info = new StringBuilder();
        List<Object> args = new ArrayList<>();
        for(Object key : bean.keySet()){
            if(!colsMap.containsKey(key)){
                info.append(key + ",");
            }
            sb.append(key + ",");
            args.add(bean.get(key));
        }
        if(args.size() <= 0){
            return Response.makeFalse("没有参数");
        }else {
            sb.setLength(sb.length() - 1);
        }
        sb.append(") values(").append(SqlUtil.makePosition("?", args.size())).append(")");
        log.info("make sql " + sb.toString());
        int res = jdbcDao.executeSql(sb.toString(), args.toArray());
        return Response.makeTrue((info.length() > 0 ? "WARING 不存在的键值" + info : "") + SqlUtil.makeSql(sb.toString(), args.toArray()), res);
    }

    @ApiOperation(value = "delete 删除", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/delet.do", method = RequestMethod.GET)
    public Response delet(HttpServletRequest request) {
        Bean bean = RequestUtil.getRequestBean(request);
        bean.remove("nowPage");
        bean.remove("showNum");
        bean.remove("order");
        String database = bean.get(KEY_DB, "");
        String tableName = bean.get(KEY_TABLE, "");
        if(tableName.length() <= 0){
            return Response.makeFalse(KEY_TABLE + "为空");
        }
        if(database.length() > 0) tableName = database + "." + tableName;
        bean.remove(KEY_TABLE);
        bean.remove(KEY_DB);
        Bean colsMap = new Bean(this.getColsMapCache(tableName));
        //delete from student where 1=1 and id=? and name=?
        StringBuilder sb = new StringBuilder("delete from " + tableName + " where 1=1 ");
        StringBuilder info = new StringBuilder();
        List<Object> args = new ArrayList<>();
        for(Object key : bean.keySet()){
            if(!colsMap.containsKey(key)){
                info.append(key + ",");
            }
            sb.append("and " + key + "=? ");
            args.add(bean.get(key));
        }
        if(args.size() <= 0){
            return Response.makeFalse("没有参数");
        }else {
            sb.setLength(sb.length() - 1);
        }
        log.info("make sql " + sb.toString());
        int res = jdbcDao.executeSql(sb.toString(), args.toArray());
        return Response.makeTrue((info.length() > 0 ? "WARING 不存在的键值" + info : "") + SqlUtil.makeSql(sb.toString(), args.toArray()), res);
    }

    @ApiOperation(value = "get findPage 分页查询", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response findPage(HttpServletRequest request) {
        Page page = new Page()
                .setNowpage(request.getParameter("nowPage"))
                .setShownum(request.getParameter("showNum"))
                .setOrder(request.getParameter("order"));


        Bean bean = RequestUtil.getRequestBean(request);
        bean.remove("nowPage");
        bean.remove("showNum");
        bean.remove("order");
        String database = bean.get(KEY_DB, "");
        String tableName = bean.get(KEY_TABLE, "");
        if(tableName.length() <= 0){
            return Response.makeFalse(KEY_TABLE + "为空");
        }
        if(database.length() > 0) tableName = database + "." + tableName;
        bean.remove(KEY_TABLE);
        bean.remove(KEY_DB);

        Bean colsMap = new Bean(this.getColsMapCache(tableName));
        //select * from student where 1=1 and id=? and name=?
        StringBuilder sb = new StringBuilder("select * from " + tableName + " where 1=1 ");
        StringBuilder info = new StringBuilder();
        List<Object> args = new ArrayList<>();
        for(Object key : bean.keySet()){
            if(!colsMap.containsKey(key)){
                info.append(key + ",");
            }
            if(String.valueOf(bean.get(key)).length() > 0) {
                sb.append("and " + key + "=? ");
                args.add(bean.get(key));
            }
        }
        if(args.size() <= 0){
        }else {
            sb.setLength(sb.length() - 1);
        }
        log.info("make sql " + sb.toString());
        List<?> res = jdbcDao.findPage(page, sb.toString(), args.toArray());
        return Response.makePage((info.length() > 0 ? "WARING 不存在的键值" + info : "") + SqlUtil.makeSql(sb.toString(), args.toArray()), page, res);
    }



}
