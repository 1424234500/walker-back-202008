package com.walker.controller;

import com.walker.Response;
import com.walker.common.util.*;
import com.walker.core.database.Redis;
import com.walker.core.database.Redis.Fun;

import java.util.*;

import com.walker.dao.RedisDao;
import com.walker.mapper.StatisticsMapper;
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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

@Controller
@RequestMapping({"/redis"})
public class RedisController  {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisService redisService;
    @Autowired
    RedisDao redisDao;


    @ApiOperation(value = "查询redis锁集合", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response locks(
            @RequestParam(value = "KEY", required = false, defaultValue = "") String keys,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);

        List<?> res = redisService.getKeyValues(keys);
        page.setNum(res.size());
        return Response.makePage("get redis key value", page, res);
    }

    @ApiOperation(value = "删除redis", notes = "")
    @ResponseBody
    @RequestMapping(value = "/delet.do", method = RequestMethod.GET)
    public Response delet(
            @RequestParam(value = "ids", required = true, defaultValue = "") String ids
    ) {

        long res = redisService.delKeys(Arrays.asList(ids.split(",")));

        return Response.makeTrue("rm locks " + ids, res);
    }
    @ApiOperation(value = "添加redis数据", notes = "")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.GET)
    public Response save(
            @RequestParam(value = "KEY", required = true, defaultValue = "") String KEY,
            @RequestParam(value = "TYPE", required = false, defaultValue = "string") String TYPE,
            @RequestParam(value = "TTL", required = false, defaultValue = "0") String TTL,
            @RequestParam(value = "VALUE", required = false, defaultValue = "") String VALUE
    ) {

        Bean args = new Bean().put("KEY", KEY).put("TYPE", TYPE).put("TTL", TTL).put("VALUE", VALUE);
//        long res = redisService.addLocks(KEY, VALUE);
        Bean res = redisDao.setKeyInfo(args);
        return Response.makeTrue("add locks " + KEY + " " + VALUE, res);
    }


    @ApiOperation(value = "获取需要前段展示的表列名 备注名", notes = "")
    @ResponseBody
    @RequestMapping(value = "/getColsMap.do", method = RequestMethod.GET)
    public Response getColsMap(
    ) {
        Map<String, String> colMap = redisService.getColsMapCache();
        Map<String, Object> res = new HashMap<>();
        res.put("colMap", colMap);
        res.put("colKey", colMap.keySet().toArray()[0]);
        return Response.makeTrue("redis key value", res);
    }


}
