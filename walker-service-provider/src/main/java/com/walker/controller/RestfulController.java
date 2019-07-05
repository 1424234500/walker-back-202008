package com.walker.controller;


import com.walker.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
Parameter Types
        OpenAPI 3.0 distinguishes between the following parameter types based on the parameter location. The location is determined by the parameter’s in key, for example, in: query or in: path.

        path parameters, such as /users/{id}
        query parameters, such as /users?role=admin
        header parameters, such as X-MyHeader: Value
        cookie parameters, which are passed in the Cookie header, such as Cookie: debug=0; csrftoken=BUSe35dohU3O1MZvDCU

@ApiOperation(value = "测试借口3", notes = "测试借口3", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_VALUE)

 */
@Api(value = "测试 | swagger | springboot | controller |返回对象")
@Controller
@RequestMapping("/restful")
public class RestfulController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @ApiOperation(value="value 测试返回字符串 get", notes="notes 返回字符串", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="path", name = "id", value = "用户ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType="path", name = "name", value = "名字", required = false, dataType = "String"),
    })
    @RequestMapping(value="make.do/{id}/{name}",method= RequestMethod.GET)
    public String get(@PathVariable("id") String id, @PathVariable("name") String name){
        String res = "get id:" + id + " name:" + name;
        log.info(res);
        return res;
    }

    @ApiOperation(value="value 测试返回对象 get", notes="notes  测试返回对象", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="path", name = "id", value = "用户ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType="path", name = "name", value = "名字", required = false, dataType = "String"),
    })
    @RequestMapping(value="makeobject.do/{id}/{name}",method= RequestMethod.GET)
    public Response getobject(@PathVariable("id") String id, @PathVariable String name){
        String res = "get id:" + id + " name:" + name;
        log.info(res);
        return Response.getTrue(res);
    }

    @ApiOperation(value="value 测试添加 post", notes="notes 测试添加 post", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "id", value = "用户ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "name", value = "名字", required = false, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "time", value = "时间", required = false, dataType = "String"),
    })
    @RequestMapping(value="/make.do",method=RequestMethod.POST) //, produces = "application/json")
    public Response post(@PathVariable String id, @PathVariable String name, @PathVariable String time){
        String res = "post id:" + id + " name:" + name+ " time:" + time;
        log.info(res);
        return Response.getTrue(res);
    }
    @ApiOperation(value="value 测试更新 put", notes="notes 测试更新 put", httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query", name = "id", value = "用户ID", required = true, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "name", value = "名字", required = false, dataType = "String"),
            @ApiImplicitParam(paramType="query", name = "time", value = "时间", required = false, dataType = "String"),
    })
    @RequestMapping(value="/make.do",method=RequestMethod.PUT)
    public Response put(@PathVariable String id, @PathVariable String name, @PathVariable String time){
        String res = "put id:" + id + " name:" + name+ " time:" + time;
        log.info(res);
        return Response.getTrue(res);
    }

    @ApiOperation(value="value 测试删除 delete", notes="notes 测试删除 delete", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="path", name = "id", value = "用户ID", required = true, dataType = "String"),
    })
    @RequestMapping(value="/{id}/make.do",method=RequestMethod.DELETE)
    public Response delete(@PathVariable String id){
        String res = "delete id:" + id;
        log.info(res);
        return Response.getTrue(res);
    }


}
