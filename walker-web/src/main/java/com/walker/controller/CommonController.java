package com.walker.controller;


import com.walker.Response;
import com.walker.common.util.Page;
import com.walker.dao.JdbcDao;
import com.walker.mode.Teacher;
import com.walker.service.BaseService;
import com.walker.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/*
测试 jap teacherService

 */
@Api(value = "测试 service层 TEACHER表 实体类对象 ")
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
            @RequestParam(value = "tableName", required = true, defaultValue = "TEACHER") String tableName
    ) {
        return Response.makeTrue(tableName, jdbcDao.getColumnsMapByTableName(tableName));
    }


}
