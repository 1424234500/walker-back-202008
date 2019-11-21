package com.walker.controller;


import com.walker.Response;
import com.walker.common.util.Bean;
import com.walker.common.util.Page;
import com.walker.core.cache.Cache;
import com.walker.core.cache.CacheMgr;
import com.walker.core.database.SqlUtil;
import com.walker.dao.JdbcDao;
import com.walker.service.BaseService;
import com.walker.service.CacheService;
import com.walker.service.ScheduleService;
import com.walker.util.RequestUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 任务调度配置
 */
@Api(value = "quartz工具")
@Controller
@RequestMapping("/quartz")
public class QuartzController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("baseService")
    private BaseService baseService;
    @Autowired
    @Qualifier("cacheService")
    private CacheService cacheService;
    @Autowired
    @Qualifier("jdbcDao")
    private JdbcDao jdbcDao;
    @Autowired
    @Qualifier("scheduleService")
    private ScheduleService scheduleService;

    @ApiOperation(value = "保存任务", notes = "通用存储")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public Response save(HttpServletRequest request) {


        return Response.makeFalse("no implemnets");

    }

    @ApiOperation(value = "删除任务", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/delet.do", method = RequestMethod.GET)
    public Response delet(HttpServletRequest request) {

        return Response.makeFalse("no implemnets");
    }
    @ApiOperation(value = "分页查询任务列表", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response findPage(
            @RequestParam(value = "JOB_NAME", required = false, defaultValue = "") String jobName,
            @RequestParam(value = "DESCRIPTION", required = false, defaultValue = "") String description,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);

        StringBuilder sb = new StringBuilder("select * from  W_QRTZ_JOB_DETAILS where 1=1 ");
        List<Object> args = new ArrayList<>();
        if(jobName != null && jobName.length() > 0) {
            sb.append("and JOB_NAME like ? ");
            args.add("%" + jobName  + "%");
        }
        if(description != null && description.length() > 0) {
            sb.append("and DESCRIPTION like ? ");
            args.add("%" + description  + "%");
        }

        log.info("make sql " + sb.toString());
        List<?> res = jdbcDao.findPage(page, sb.toString(), args.toArray());
        return Response.makePage(SqlUtil.makeSql(sb.toString(), args.toArray()), page, res);
    }


    @ApiOperation(value = "分页查询 任务触发器", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPageTrigger.do", method = RequestMethod.GET)
    public Response findPageTrigger(
            @RequestParam(value = "JOB_NAME", required = true, defaultValue = "") String jobName,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);

        StringBuilder sb = new StringBuilder(
                "select j.JOB_NAME,c.CRON_EXPRESSION,t.DESCRIPTION from  W_QRTZ_JOB_DETAILS j, W_QRTZ_TRIGGERS t, W_QRTZ_CRON_TRIGGERS c  where 1=1 and j.JOB_NAME=t.JOB_NAME and t.TRIGGER_NAME=c.TRIGGER_NAME and j.JOB_NAME=? " );

        log.info("make sql " + sb.toString());
        List<?> res = jdbcDao.findPage(page, sb.toString(), jobName);
        return Response.makePage(SqlUtil.makeSql(sb.toString(), jobName), page, res);
    }



}
