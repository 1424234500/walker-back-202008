package com.walker.controller;


import com.walker.Response;
import com.walker.common.util.Bean;
import com.walker.common.util.LangUtil;
import com.walker.common.util.Page;
import com.walker.common.util.TimeUtil;
import com.walker.core.cache.Cache;
import com.walker.core.cache.CacheMgr;
import com.walker.core.database.SqlUtil;
import com.walker.dao.JdbcDao;
import com.walker.service.CacheService;
import com.walker.util.RequestUtil;
import com.walker.service.BaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/*
测试 jap teacherService

 */
@Api(value = "公用工具mysql ddl ")
@Controller
@RequestMapping("/common")
public class CommonController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("baseService")
    private BaseService baseService;
    @Autowired
    @Qualifier("cacheService")
    private CacheService cacheService;

    /**
     * shardingjdbc拦截mysql其他用户 为默认用户
     */
    @Autowired
    @Qualifier("jdbcDao")
    private JdbcDao jdbcDao;

    @ApiOperation(value = "sql执行", notes = "")
    @ResponseBody
    @RequestMapping(value = "/exeSql.do", method = RequestMethod.GET)
    public Response exeSql(
            @RequestParam(value = "_SQL_", required = true, defaultValue = "select 1 from dual") String sql,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);

//        select xxx  update xxxx insert xxxx delete from xxxx
        Map<String, Object> res = new HashMap<>();

        String up = sql.toUpperCase();
        int s = up.indexOf("SELECT") ;
        int w = up.indexOf("WITH") ;
        int u = up.indexOf("UPDATE") ;
        int i = up.indexOf("INSERT") ;
        int d = up.indexOf("DELETE") ;

        try {
            if (
                    (s > 0 && s > u && s > i && s > d)  //
                            || (u < 0 && i < 0 && d < 0)    //无修改
            ) {
                List<?> list = null;

                List<String> cols = baseService.getColumnsBySql(sql);
                Map<String, String> colMap = new LinkedHashMap<>();
                for (String str : cols) {
                    colMap.put(str, str);
                }
                list = baseService.findPage(page, sql);
                page.setNum(baseService.count(sql));

                res.put("data", list);
                res.put("page", page);
                res.put("colMap", colMap);
                res.put("colKey", colMap.keySet().toArray()[0]);
            } else {
                String info = "";
                info = "" + baseService.executeSql(sql);
                res.put("info", info);

            }
        }catch (Exception e){
            res.put("info", e.getMessage());
        }
        return Response.makeTrue(sql, res);
    }



    @ApiOperation(value = "获取需要前段展示的表列名 备注名", notes = "")
    @ResponseBody
    @RequestMapping(value = "/getColsMap.do", method = RequestMethod.GET)
    public Response getColsMap(
            @RequestParam(value = "dbOrUser", required = false, defaultValue = "") String dbOrUser,
            @RequestParam(value = "tableName", required = true, defaultValue = "W_TEACHER") String tableName
    ) {
        Map<String, String> colMap = cacheService.getColsMapCache(dbOrUser, tableName);
        Map<String, Object> res = new HashMap<>();
        res.put("colMap", colMap);
        res.put("colKey", colMap.keySet().toArray()[0]);
        return Response.makeTrue(tableName, res);
    }


    @ApiOperation(value = "获取表列表", notes = "")
    @ResponseBody
    @RequestMapping(value = "/getTables.do", method = RequestMethod.GET)
    public Response getTables(
            @RequestParam(value = "_DATABASE_", required = true, defaultValue = "") String database
    ) {
        Object res = CacheMgr.getInstance().getFun("jdbcDao.getTables:" + database , new Cache.Function() {
            @Override
            public Object cache() {
                return jdbcDao.getTables(database);
            }
        });

        return Response.makeTrue("", res);
    }

    @ApiOperation(value = "获取数据库/用户列表", notes = "")
    @ResponseBody
    @RequestMapping(value = "/getDatabasesOrUsers.do", method = RequestMethod.GET)
    public Response getDatabases(  ) {
        Object res = CacheMgr.getInstance().getFun("jdbcDao.getDatabasesOrUsers:" , new Cache.Function() {
            @Override
            public Object cache() {
                return jdbcDao.getDatabasesOrUsers();
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
        bean.remove(KEY_TABLE);
        bean.remove(KEY_DB);


        List<String> cols = cacheService.getColsCache(database, tableName);
        String keyId = cols.get(0);
        //insert into student(ID, NAME, TIME) values(?,?,?)
        StringBuilder sb = new StringBuilder("insert into " + (database.length() > 0 ? database+"." : "") + tableName + "(");
        StringBuilder info = new StringBuilder();
        List<Object> args = new ArrayList<>();
        for(Object key : bean.keySet()){
            if(!cols.contains(key)){
                info.append(key + ",");
            }else {
                sb.append(key + ",");
                if (key.equals("S_MTIME")) {
                    bean.set(key, TimeUtil.getTimeYmdHmss());
                }
                if (key.equals(keyId) && bean.get(keyId, "").length() == 0) {
                    bean.set(keyId, LangUtil.getTimeSeqId());
                    log.info("none id save then make id " + bean);
                }
                args.add(bean.get(key));
            }
        }
        if(args.size() <= 0){
            return Response.makeFalse("没有参数");
        }else {
            sb.setLength(sb.length() - 1);
        }
        sb.append(") values(").append(SqlUtil.makePosition("?", args.size())).append(")");
        log.info("make sql " + sb.toString());
        int res = jdbcDao.executeSql(sb.toString(), args.toArray());
        return Response.makeTrue((info.length() > 0 ? "WARING 不存在的键值" + info + " \n" : "") + SqlUtil.makeSql(sb.toString(), args.toArray()), res);
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
        bean.remove(KEY_TABLE);
        bean.remove(KEY_DB);
        List<String> cols = cacheService.getColsCache(database, tableName);
        String keyId = cols.get(0);
        //delete from student where 1=1 and id=? and name=?
        StringBuilder sb = new StringBuilder("delete from " + (database.length() > 0 ? database+"." : "") + tableName + " where 1=1 ");
        StringBuilder info = new StringBuilder();
        List<Object> args = new ArrayList<>();
        sb.append(" and ").append(keyId).append( "=? ");
        args.add(bean.get(keyId));
//        for(Object key : bean.keySet()){
//            if(!colsMap.containsKey(key)){
//                info.append(key + ",");
//            }
//            sb.append("and " + key + "=? ");
//            args.add(bean.get(key));
//        }
//        if(args.size() <= 0){
//            return Response.makeFalse("没有参数");
//        }else {
//            sb.setLength(sb.length() - 1);
//        }
        log.info("make sql " + sb.toString());
        int res = jdbcDao.executeSql(sb.toString(), args.toArray());
        return Response.makeTrue((info.length() > 0 ? "WARING 不存在的键值" + info + " \n" : "") + SqlUtil.makeSql(sb.toString(), args.toArray()), res);
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
        bean.remove(KEY_TABLE);
        bean.remove(KEY_DB);


        List<String> cols = cacheService.getColsCache(database, tableName);
        String keyId = cols.get(0);

        //select * from student where 1=1 and id=? and name=?
        StringBuilder sb = new StringBuilder("select * from " + (database.length() > 0 ? database+"." : "") + tableName + " where 1=1 ");
        StringBuilder info = new StringBuilder();
        List<Object> args = new ArrayList<>();
        for(Object key : bean.keySet()){
            if(!cols.contains(key)){
                info.append(key + ",");
            }
            String value = String.valueOf(bean.get(key));
            if(value != null && value.length() > 0) {
                sb.append("and " + key + " like ? ");
                args.add("%" + value  + "%");
            }
        }
        if(args.size() <= 0){
        }else {
            sb.setLength(sb.length() - 1);
        }
        log.info("make sql " + sb.toString());
        List<?> res = jdbcDao.findPage(page, sb.toString(), args.toArray());
        return Response.makePage((info.length() > 0 ? "WARING 不存在的键值" + info + " \n" + " \n" : "") + SqlUtil.makeSql(sb.toString(), args.toArray()), page, res);
    }



}
