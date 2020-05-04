package com.walker.controller;

import com.walker.Response;
import com.walker.common.util.Bean;
import com.walker.common.util.Page;
import com.walker.common.util.TimeUtil;
import com.walker.core.database.Redis;
import com.walker.core.database.Redis.Fun;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.walker.mode.PushBindModel;
import com.walker.service.Config;
import com.walker.service.LockService;
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
    LockService lockService;

    @ApiOperation(value = "统计socket", notes = "")
    @ResponseBody
    @RequestMapping(value = "/statics.do", method = RequestMethod.GET)
    public Response statics(
            @RequestParam(value = "from", required = false, defaultValue = "") String from,
            @RequestParam(value = "to", required = false, defaultValue = "") String to,
            @RequestParam(value = "url", required = false, defaultValue = "") String url
    ) {
        final String format = "yyyy-MM-dd HH:mm";
        final List<String> listXs = new ArrayList();
        final List<Bean> series = new ArrayList();
        final List<Bean> series2 = new ArrayList();
        final Set<String> items = new HashSet();
        Bean newArg = (Bean)Redis.doJedis(new Fun<Bean>() {
            public Bean make(Jedis jedis) {
                String fromNew = from;
                String toNew = to;
                String start = "stat:statis:";
                Set<String> keys = jedis.keys(start + "*");
                double min = 0.0D;
                double max = 0.0D;
                double deta = 60 * 60 * 1000.0D;
                if (from.length() == 0 && to.length() == 0) {
//                    for(String key : keys) {
//                        Set<Tuple> set = jedis.zrevrangeWithScores(String.valueOf(keys.toArray()[0]), 0L, 0L);
//                        max = ((Tuple[]) set.toArray(new Tuple[0]))[0].getScore();
//                    }

                    max = System.currentTimeMillis();
                    min = max - deta;
                    fromNew = TimeUtil.getTime((long)min, format);
                    toNew = TimeUtil.getTime((long)max, format);
                } else if (from.length() == 0) {
                    max = (double)TimeUtil.format(to, format);
                    min = max - deta;
                    fromNew = TimeUtil.getTime((long)min, format);
                } else if (to.length() == 0) {
                    min = (double)TimeUtil.format(from, format);
                    max = min + deta;
                    toNew = TimeUtil.getTime((long)max, format);
                } else {
                    min = (double)TimeUtil.format(from, format);
                    max = (double)TimeUtil.format(to, format);
                }

                deta = max - min;

                String type;
                ArrayList lineAve;
                ArrayList line;
                String name;
                for(String key : keys){

                    type = "bar";
                    if (listXs.size() == 0) {
                        Set<Tuple> rowWithScore = jedis.zrangeByScoreWithScores(key, min, max);
                        for(Tuple colTuple : rowWithScore) {
                            double score = colTuple.getScore();
//                            String colx = colTuple.getElement();
                            String x = TimeUtil.formatAuto((long) score, 0);
                            listXs.add(x);
                        }
                    }

                    line = new ArrayList();
                    List<String> lineQpsNet = new ArrayList();
                    List<String> lineQpsWait = new ArrayList();
                    List<String> lineQpsDone = new ArrayList();
                    line.add(lineQpsNet);
                    line.add(lineQpsWait);
                    line.add(lineQpsDone);
                    lineAve = new ArrayList();
                    List<String> lineAveNet = new ArrayList();
                    List<String> lineAveWait = new ArrayList();
                    List<String> lineAveDone = new ArrayList();
                    lineAve.add(lineAveNet);
                    lineAve.add(lineAveWait);
                    lineAve.add(lineAveDone);
                    Set<String> row = jedis.zrangeByScore(key, min, max);
                    for(String col : row) {
                        String[] cc = col.split(" +");
                        lineQpsNet.add(cc[4]);
                        lineQpsWait.add(cc[9]);
                        lineQpsDone.add(cc[14]);
                        lineAveNet.add(cc[6]);
                        lineAveWait.add(cc[11]);
                        lineAveDone.add(cc[16]);
                    }

                    name = key.substring(start.length());

                    if (url.length() == 0 || url.contains(name)) {
                        items.add(name);

                        series.add((new Bean()).put("name", name + ":net").put("type", type).put("stack", name).put("data", line.get(0)));
                        series.add((new Bean()).put("name", name + ":wait").put("type", type).put("stack", name).put("data", line.get(1)));
                        series.add((new Bean()).put("name", name + ":done").put("type", type).put("stack", name).put("data", line.get(2)));
                        series2.add((new Bean()).put("name", name + ":net").put("type", type).put("stack", name).put("data", lineAve.get(0)));
                        series2.add((new Bean()).put("name", name + ":wait").put("type", type).put("stack", name).put("data", lineAve.get(1)));
                        series2.add((new Bean()).put("name", name + ":done").put("type", type).put("stack", name).put("data", lineAve.get(2)));
                    }
                }

                return (new Bean()).put("from", fromNew).put("to", toNew).put("url", url);
            }
        });
        List<String> listLineNames = new ArrayList();
        Iterator var13 = series.iterator();

        Bean xAxis;
        while(var13.hasNext()) {
            xAxis = (Bean)var13.next();
            listLineNames.add(xAxis.get("name", ""));
        }

        Bean legend = (new Bean()).put("data", listLineNames);
        xAxis = (new Bean()).put("data", listXs);
        Bean option = (new Bean()).put("title", (new Bean()).put("text", "qps").put("subtext", "socket统计")).put("legend", legend).put("tooltip", new Bean()).put("xAxis", xAxis).put("yAxis", new Bean()).put("series", series);
        Bean option2 = (new Bean()).put("title", (new Bean()).put("text", "cost").put("subtext", "socket统计")).put("legend", legend).put("tooltip", new Bean()).put("xAxis", xAxis).put("yAxis", new Bean()).put("series", series2);
        Bean res = (new Bean()).put("format", format).put("res", newArg == null).put("arg", newArg).put("option", option).put("option2", option2).put("items", items);
        log.info(res.toString());
        return Response.makeTrue("", res);
    }




    @ApiOperation(value = "查询redis锁集合", notes = "")
    @ResponseBody
    @RequestMapping(value = "/findPage.do", method = RequestMethod.GET)
    public Response locks(
            @RequestParam(value = "KEY", required = true, defaultValue = "") String keys,

            @RequestParam(value = "nowPage", required = false, defaultValue = "1") Integer nowPage,
            @RequestParam(value = "showNum", required = false, defaultValue = "20") Integer showNum,
            @RequestParam(value = "order", required = false, defaultValue = "") String order
    ) {
        Page page = new Page().setNowpage(nowPage).setShownum(showNum).setOrder(order);

        List<?> res = lockService.getLocks(keys);
        page.setNum(res.size());
        return Response.makePage("get redis key value", page, res);
    }

    @ApiOperation(value = "删除redis锁集合", notes = "")
    @ResponseBody
    @RequestMapping(value = "/delet.do", method = RequestMethod.GET)
    public Response delet(
            @RequestParam(value = "ids", required = true, defaultValue = "") String ids
    ) {

        long res = lockService.delLocks(Arrays.asList(ids.split(",")));

        return Response.makeTrue("rm locks " + ids, res);
    }
    @ApiOperation(value = "添加redis锁集合", notes = "")
    @ResponseBody
    @RequestMapping(value = "/save.do", method = RequestMethod.GET)
    public Response addLocks(
            @RequestParam(value = "KEY", required = true, defaultValue = "") String KEY,
            @RequestParam(value = "VALUE", required = true, defaultValue = "") String VALUE
    ) {

        long res = lockService.addLocks(KEY, VALUE);

        return Response.makeTrue("add locks " + KEY + " " + VALUE, res);
    }


    @ApiOperation(value = "获取需要前段展示的表列名 备注名", notes = "")
    @ResponseBody
    @RequestMapping(value = "/getColsMap.do", method = RequestMethod.GET)
    public Response getColsMap(
    ) {
        Map<String, String> colMap = lockService.getColsMapCache();
        Map<String, Object> res = new HashMap<>();
        res.put("colMap", colMap);
        res.put("colKey", colMap.keySet().toArray()[0]);
        return Response.makeTrue("redis key value", res);
    }


}
