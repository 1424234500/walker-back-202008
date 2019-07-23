package com.walker.controller;


import com.walker.Response;
import com.walker.common.util.Page;
import com.walker.mode.Test;
import com.walker.service.TestService;
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
测试 jap jpaService

 */
@Api(value = "测试jpa操作 service层 ")
@Controller
@RequestMapping("/test")
public class TestController {
    private Logger log = LoggerFactory.getLogger(getClass());


    @Autowired
    @Qualifier("testJpaService")
    private TestService jpaService;

    //    public Test add( Test test);
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
        Test model = jpaService.add(new Test(id, name, time, ""));
        return Response.makeTrue(res, model);
    }

    //    public Integer update(Test test);
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
        Integer model = jpaService.update(new Test(id, name, time, pwd));
        return Response.makeTrue(res, model);
    }

    //    public void delete(Test test);
    @ApiOperation(value = "delete 删除", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/{id}/action.do", method = RequestMethod.DELETE)
    public Response delete(
            @PathVariable(value = "id", required = true) String id
    ) {
        String res = "delete id:" + id;
        log.info(res);
        jpaService.delete(new Test(id, "", "", ""));
        return Response.makeTrue(res);
    }

    //    public Test get(Test test);
    @ResponseBody
    @RequestMapping(value = "/action.do", method = RequestMethod.GET, produces = "application/json")
    public Response get(
            @RequestParam(value = "id", required = true) String id
    ) {
        String res = "get id:" + id;
        log.info(res);
        Test model = jpaService.get(new Test(id, "", "", ""));
        return Response.makeTrue(res, model);
    }
//
//    public Page<Test> finds(Test test, Pageable page) ;


    @ApiOperation(value = "get 分页查询", notes = "url restful参数 PathVariable")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response findPage(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "3") Integer showNum
    ) {
        String res = "get   name:" + name;
        log.info(res);

        Page page1 = new Page().setNOWPAGE(nowPage).setSHOWNUM(showNum);

        List<Test> list = jpaService.finds(new Test("", name, "", ""), page1);
        log.info(page1.toString());
        return Response.makePage(res, page1, list);
    }


}
