package com.walker.controller;


import com.walker.Response;
import com.walker.common.util.Page;
import com.walker.dao.TeacherRepository;
import com.walker.mode.Teacher;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 测试仓库repository
 */
@Api(value = "测试jpa操作 dao层 自定义sql ")
@Controller
@RequestMapping("/repository")
public class RepositoryController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private TeacherRepository repository;


    @ApiOperation(value = "自定义更新 selfUpdateSql")
    @ResponseBody
    @RequestMapping(value = "/selfUpdateSql.do", method = RequestMethod.POST, produces = "application/json")
    public Response selfUpdateSql(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "name", required = false, defaultValue = "default") String name
    ) {
        return Response.makeTrue("", repository.selfUpdateSql(name, id));
    }

    @ApiOperation(value = "自定义更新 selfUpdateCacheJPQL")
    @ResponseBody
    @RequestMapping(value = "/selfUpdateJPQL.do", method = RequestMethod.POST, produces = "application/json")
    public Response selfUpdateJPQL(
            @RequestParam(value = "id", required = true) String id,
            @RequestParam(value = "name", required = false, defaultValue = "default") String name
    ) {
        return Response.makeTrue("", repository.selfUpdateCacheJPQL(name, id));
    }

    @ApiOperation(value = "自定义删除 selfDeleteJPQL")
    @ResponseBody
    @RequestMapping(value = "/selfDeleteJPQL.do", method = RequestMethod.POST, produces = "application/json")
    public Response selfDeleteJPQL(
            @RequestParam(value = "id", required = true) String id
    ) {
        return Response.makeTrue("", repository.selfDeleteJPQL(id));
    }

    @ApiOperation(value = "自定义查询 selfFindByName")
    @ResponseBody
    @RequestMapping(value = "/selfFindByName.do", method = RequestMethod.POST, produces = "application/json")
    public Response selfFindByName(
            @RequestParam(value = "name", required = false) String name
    ) {
        return Response.makeTrue("", repository.selfFindByName(name));
    }
    @ApiOperation(value = "分页查询", notes = "url restful参数 PathVariable")
    @ResponseBody
    @RequestMapping(value = "/selfFindPage.do", method = RequestMethod.GET)
    public Response selfFindPage(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "3") Integer showNum
    ) {
        Sort sort = new Sort(Sort.Direction.ASC, "name");
        Pageable pageable = new PageRequest(nowPage-1, showNum, sort);
        log.info(pageable.toString());
        Page page1 = new Page().setNOWPAGE(nowPage).setSHOWNUM(showNum);
        List<Teacher> list = repository.selfFindPage(name, pageable);
        int count = repository.selfCount(name);
        page1.setNUM(count);
        log.info(page1.toString());
        return Response.makePage("", page1, list);
    }

    @ApiOperation(value = "自定义查询 selfCount")
    @ResponseBody
    @RequestMapping(value = "/selfCount.do", method = RequestMethod.GET, produces = "application/json")
    public Response selfCount(
            @RequestParam(value = "name", required = false, defaultValue = "") String name
    ) {
        return Response.makeTrue("", repository.selfCount(name));
    }
    @ApiOperation(value = "getOne 查询")
    @ResponseBody
    @RequestMapping(value = "/getOne.do", method = RequestMethod.GET, produces = "application/json")
    public Response getOne(
            @RequestParam(value = "id", required = true, defaultValue = "1") String id
            ) {
        Teacher res = repository.getOne(id);
        if (res == null) {
            return Response.makeFalse("not exists");
        } else {
            return Response.makeTrue("", res);
        }
    }
    @ApiOperation(value = "save 存储")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST, produces = "application/json")
    public Response save(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "name", required = true, defaultValue = "default") String name,
            @RequestParam(value = "time", required = false, defaultValue = "default") String time
    ) {
        Teacher model = repository.save(new Teacher(id, name, time, ""));
        return Response.makeTrue("", model);
    }
    @ApiOperation(value = "existsById 查询")
    @ResponseBody
    @RequestMapping(value = "/existsById.do", method = RequestMethod.GET, produces = "application/json")
    public Response existsById(
            @RequestParam(value = "id", required = true) String id
    ) {
        return Response.makeTrue("", repository.existsById(id));
    }

    @ApiOperation(value = "count 计数")
    @ResponseBody
    @RequestMapping(value = "/count.do", method = RequestMethod.GET, produces = "application/json")
    public Response count(
            ) {
        return Response.makeTrue("", repository.count());
    }

    @ApiOperation(value = "deleteById 删除")
    @ResponseBody
    @RequestMapping(value = "/deleteById.do", method = RequestMethod.PUT, produces = "application/json")
    public Response deleteById(
            @RequestParam(value = "id", required = true) String id
    ) {
        repository.deleteById(id);
        return Response.makeTrue("", id);
    }
    @ApiOperation(value = "delete 删除")
    @ResponseBody
    @RequestMapping(value = "/delete.do", method = RequestMethod.PUT, produces = "application/json")
    public Response delete(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "name", required = false, defaultValue = "") String name,
            @RequestParam(value = "time", required = false, defaultValue = "") String time
    ) {
        Teacher model = new Teacher(id, name, time, "");
        repository.delete(model);
        return Response.makeTrue("", model);
    }
}
