package com.walker.controller;


import com.walker.Response;
import com.walker.common.util.Page;
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

import java.util.Arrays;
import java.util.List;

/*
测试 jap teacherService

 */
@Api(value = "测试 service层 TEACHER表 实体类对象 ")
@Controller
@RequestMapping("/teacher")
public class TeacherController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("teacherJpaService")
    private TeacherService teacherService;

    @ApiOperation(value = "post 添加", notes = "post参数 RequestParam ")
    @ResponseBody
    @RequestMapping(value = "/action.do", method = RequestMethod.POST)
    public Response add(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "name", required = true, defaultValue = "default") String name,
            @RequestParam(value = "time", required = false, defaultValue = "default") String time
    ) {
        String info = "post id:" + id + " name:" + name + " time:" + time;
        List<Teacher> res = teacherService.saveAll(Arrays.asList(new Teacher(id, name, time, "")));
        return Response.makeTrue(info, res);
    }

    @ApiOperation(value = "put 更新", notes = "put参数 RequestParam ")
    @ResponseBody
    @RequestMapping(value = "/action.do", method = RequestMethod.PUT)
    public Response update(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "name", required = false, defaultValue = "default") String name,
            @RequestParam(value = "time", required = false, defaultValue = "default") String time,
            @RequestParam(value = "pwd", required = false, defaultValue = "default") String pwd
    ) {
        String info = "update id:" + id + " name:" + name + " time:" + time + " pwd:" + pwd;
        List<Teacher> res = teacherService.saveAll(Arrays.asList(new Teacher(id, name, time, "")));
        return Response.makeTrue(info, res);
    }

    @ApiOperation(value = "delete 删除", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/{ids}/action.do", method = RequestMethod.DELETE)
    public Response delete(
            @PathVariable(value = "ids", required = true) String ids
    ) {
        String info = "delete id:" + ids;
        Object res = teacherService.deleteAll(Arrays.asList(ids.split(",")));
        return Response.makeTrue(info, res);
    }

    @ApiOperation(value = "get 获取", notes = "")
    @ResponseBody
    @RequestMapping(value = "/action.do", method = RequestMethod.GET)
    public Response get(
            @RequestParam(value = "id", required = true) String id
    ) {
        String info = "get id:" + id;
        Teacher model = teacherService.get(new Teacher(id, "", "", ""));
        return Response.makeTrue(info, model);
    }




    @ApiOperation(value = "get findPage 分页查询", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response findPage(
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
            ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);

        String info = "get   name:" + name;

        Teacher teacher = new Teacher().setName(name);
        List<Teacher> list = teacherService.finds(teacher, page);
        page.setNum(teacherService.count(teacher));
        return Response.makePage(info, page, list);
    }


}
