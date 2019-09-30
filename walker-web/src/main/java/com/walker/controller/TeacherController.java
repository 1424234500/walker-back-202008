package com.walker.controller;


import com.walker.Response;
import com.walker.common.util.Page;
import com.walker.dao.JdbcDao;
import com.walker.mode.Teacher;
import com.walker.service.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
测试 jap teacherService

 */
@Api(value = "测试jpa操作 service层 ")
@Controller
@RequestMapping("/teacher")
public class TeacherController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    JdbcDao jdbcDao;
    @Autowired
    @Qualifier("teacherJpaService")
    private TeacherService teacherService;

    //    public Teacher add( Teacher test);
    @ApiOperation(value = "post 添加", notes = "post参数 RequestParam ")
    @ResponseBody
    @RequestMapping(value = "/action.do", method = RequestMethod.POST, produces = "application/json")
    public Response add(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "name", required = true, defaultValue = "default") String name,
            @RequestParam(value = "time", required = false, defaultValue = "default") String time
    ) {
        String res = "post id:" + id + " name:" + name + " time:" + time;
        log.info(res);
        Teacher model = teacherService.add(new Teacher(id, name, time, ""));
        return Response.makeTrue(res, model);
    }

    //    public Integer update(Teacher test);
    @ApiOperation(value = "put 更新", notes = "put参数 RequestParam ")
    @ResponseBody
    @RequestMapping(value = "/action.do", method = RequestMethod.PUT, produces = "application/json")
    public Response update(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "name", required = false, defaultValue = "default") String name,
            @RequestParam(value = "time", required = false, defaultValue = "default") String time,
            @RequestParam(value = "pwd", required = false, defaultValue = "default") String pwd
    ) {
        String res = "update id:" + id + " name:" + name + " time:" + time + " pwd:" + pwd;
        log.info(res);
        Teacher model = teacherService.save(new Teacher(id, name, time, pwd));
        return Response.makeTrue(res, model);
    }

    //    public void delete(Teacher test);
    @ApiOperation(value = "delete 删除", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/{id}/action.do", method = RequestMethod.DELETE)
    public Response delete(
            @PathVariable(value = "id", required = true) String id
    ) {
        String res = "delete id:" + id;
        log.info(res);
        teacherService.delete(new Teacher(id, "", "", ""));
        return Response.makeTrue(res);
    }

    //    public Teacher get(Teacher test);
    @ResponseBody
    @RequestMapping(value = "/action.do", method = RequestMethod.GET, produces = "application/json")
    public Response get(
            @RequestParam(value = "id", required = true) String id
    ) {
        String res = "get id:" + id;
        log.info(res);
        Teacher model = teacherService.get(new Teacher(id, "", "", ""));
        return Response.makeTrue(res, model);
    }
//
//    public Page<Teacher> finds(Teacher test, Pageable page) ;


    @ApiOperation(value = "get 分页查询", notes = "url restful参数 PathVariable")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.POST)
    public Response findPage(
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "3") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
            ) {
        String res = "get   name:" + name;
        log.info(res);

        Page page1 = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);

        Teacher teacher = new Teacher().setName(name);
        List<Teacher> list = teacherService.finds(teacher, page1);
        page1.setNum(teacherService.count(teacher));
        log.info(page1.toString());
        return Response.makePage(res, page1, list);
    }


    @ApiOperation(value = "获取需要前段展示的表列名 备注名", notes = "url restful参数 PathVariable")
    @ResponseBody
    @RequestMapping(value = "/getColsMap.do", method = RequestMethod.POST)
    public Response getColsMap(
            @RequestParam(value = "tableName", required = true, defaultValue = "TEACHER") String tableName
    ) {
        return Response.makeTrue(tableName, jdbcDao.getColumnsMapByTableName(tableName));
    }


}
