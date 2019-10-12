package com.walker.controller;


import com.walker.Response;
import com.walker.common.util.Page;
import com.walker.mode.User;
import com.walker.mode.User;
import com.walker.service.BaseService;
import com.walker.service.UserService;
import com.walker.service.UserService;
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
测试 jap userService

 */
@Api(value = "service层 USER 实体类对象 ")
@Controller
@RequestMapping("/user")
public class UserController {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    @Qualifier("baseService")
    private BaseService baseService;

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @ApiOperation(value = "post 保存 更新/添加 ", notes = "")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public Response save(
            @RequestParam(value = "id", required = false, defaultValue = "") String id,
            @RequestParam(value = "name", required = false, defaultValue = "") String name
    ) {
        User user = new User();
        user.setId(id);
        user.setName(name);

        String info = "post user:" +user.toString();
        List<User> res = userService.saveAll(Arrays.asList(user));
        return Response.makeTrue(info, res);
    }

    @ApiOperation(value = "delete 删除", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/{ids}/action.do", method = RequestMethod.DELETE)
    public Response delete(
            @PathVariable(value = "ids", required = true) String ids
    ) {
        String info = "delete ids:" + ids;
        Object res = userService.deleteAll(Arrays.asList(ids.split(",")));
        return Response.makeTrue(info, res);
    }

    @ApiOperation(value = "get 获取", notes = "")
    @ResponseBody
    @RequestMapping(value = "/action.do", method = RequestMethod.GET)
    public Response get(
            @RequestParam(value = "id", required = true) String id
    ) {
        String info = "get id:" + id;
        User model = userService.get(new User().setId(id));
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

        User user = new User().setName(name);
        List<User> list = userService.finds(user, page);
        page.setNum(userService.count(user));
        return Response.makePage(info, page, list);
    }


}
