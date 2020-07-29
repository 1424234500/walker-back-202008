package com.walker.controller.user;


import com.walker.Response;
import com.walker.common.util.Page;
import com.walker.common.util.TimeUtil;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

/*
测试 jap teacherService

 */
@Api(value = "service层 W_STUDENT 实体类对象 ")
@Controller
@RequestMapping("/teacher")
public class TeacherController {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    @Qualifier("baseService")
    private BaseService baseService;

    @Autowired
    @Qualifier("teacherJpaService")
    private TeacherService teacherService;

    @ApiOperation(value = "post 保存 更新/添加 ", notes = "")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.POST)
    public Response save(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_ATIME", required = false, defaultValue = "") String sAtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "0") String sFlag,
            @RequestParam(value = "NAME", required = false, defaultValue = "") String name,
            @RequestParam(value = "SEX", required = false, defaultValue = "") String sex,
            @RequestParam(value = "LEVEL", required = false, defaultValue = "") String level
    ) {
        Teacher teacher = new Teacher();
        teacher.setID(id);
        teacher.setS_MTIME(TimeUtil.getTimeYmdHms());
        teacher.setS_ATIME(sAtime.length() > 0 ? sAtime : TimeUtil.getTimeYmdHmss());
        teacher.setS_FLAG(sFlag.equalsIgnoreCase("1") ? "1" : "0");
        teacher.setNAME(name);
        teacher.setSEX(sex.equalsIgnoreCase("1") ? "1" : "0");
        teacher.setLEVEL(level);

        String info = "post teacher:" +teacher.toString();
        List<Teacher> res = teacherService.saveAll(Arrays.asList(teacher));
        return Response.makeTrue(info, res);
    }

    @ApiOperation(value = "delete 删除", notes = "delete参数 restful 路径 PathVariable ")
    @ResponseBody
    @RequestMapping(value = "/delet.do", method = RequestMethod.GET)
    public Response delet(
            @RequestParam(value = "ids", required = false, defaultValue = "") String ids
    ) {
        String info = "delete ids:" + ids;
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
        Teacher model = teacherService.get(new Teacher().setID(id));
        return Response.makeTrue(info, model);
    }




    @ApiOperation(value = "get findPage 分页查询", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response findPage(
            @RequestParam(value = "ID", required = false, defaultValue = "") String id,
            @RequestParam(value = "S_MTIME", required = false, defaultValue = "") String sMtime,
            @RequestParam(value = "S_ATIME", required = false, defaultValue = "") String sAtime,
            @RequestParam(value = "S_FLAG", required = false, defaultValue = "") String sFlag,
            @RequestParam(value = "NAME", required = false, defaultValue = "") String name,
            @RequestParam(value = "SEX", required = false, defaultValue = "") String sex,
            @RequestParam(value = "LEVEL", required = false, defaultValue = "") String level,
            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);
        Teacher teacher = new Teacher();
        teacher.setID(id);
        teacher.setS_MTIME(sMtime);
        teacher.setS_ATIME(sAtime);
        teacher.setS_FLAG(sFlag);
        teacher.setNAME(name);
        teacher.setSEX(sex);
        teacher.setLEVEL(level);

        String info = "get   teacher:" + teacher;

        List<Teacher> list = teacherService.finds(teacher, page);
        page.setNum(teacherService.count(teacher));
        return Response.makePage(info, page, list);
    }


}
