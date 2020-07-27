package com.walker.controller;

import com.walker.Response;
import com.walker.common.util.Page;
import com.walker.core.database.ZookeeperModel;
import com.walker.dao.RedisDao;
import com.walker.service.RedisService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping({"/zookeeper"})
public class ZookeeperController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisService redisService;
    @Autowired
    RedisDao redisDao;

    ZookeeperModel zookeeperModel = new ZookeeperModel().setHost("localhost:8096").setMillsecondsTimeout(10000);


    @ApiOperation(value = "节点列表查询", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response locks(
            @RequestParam(value = "URL", required = false, defaultValue = "/") String url,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);

        List<Map<String, String>> list = new ArrayList<>();

        List<?> res = zookeeperModel.findPage(url);
        page.setNum(res.size());
        return Response.makePage("get zk value", page, res);
    }

    @ApiOperation(value = "删除zk", notes = "")
    @ResponseBody
    @RequestMapping(value = "/delet.do", method = RequestMethod.GET)
    public Response delet(
            @RequestParam(value = "ids", required = true, defaultValue = "") String ids
    ) {

        long res = zookeeperModel.delete(new HashSet<>(Arrays.asList(ids.split(","))));

        return Response.makeTrue("rm zks " + ids, res);
    }
    @ApiOperation(value = "添加字节点/更新 zk key value", notes = "")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.GET)
    public Response save(
            @RequestParam(value = "URL", required = true, defaultValue = "/") String url
            , @RequestParam(value = "DATA", required = false, defaultValue = "") String data
            , @RequestParam(value = "CHILD_SIZE", required = false, defaultValue = "") String childSize
            , @RequestParam(value = "FROM_URL", required = false, defaultValue = "/") String fromUrl
            , @RequestParam(value = "SEARCH_URL", required = false, defaultValue = "/") String searchUrl
    ) {
        url = searchUrl + url;
        while(url.indexOf("//") >= 0){
            url = url.replace("//", "/");
        }

        return Response.makeTrue("save zk " + url + " " + data, zookeeperModel.createOrUpdateVersion(url, data));
    }


    @ApiOperation(value = "获取需要前段展示的表列名 备注名", notes = "")
    @ResponseBody
    @RequestMapping(value = "/getColsMap.do", method = RequestMethod.GET)
    public Response getColsMap(
    ) {
        Map<String, String> colMap = zookeeperModel.getCols();
        Map<String, Object> res = new HashMap<>();
        res.put("colMap", colMap);
        res.put("colKey", colMap.keySet().toArray()[0]);
        return Response.makeTrue("zk key value", res);
    }


}
