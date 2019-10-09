package com.walker.controller;

import com.walker.Response;
import com.walker.common.util.Bean;
import com.walker.common.util.TimeUtil;
import com.walker.core.database.Redis;
import com.walker.core.database.Redis.Fun;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    @ApiOperation(value = "统计socket", notes = "")
    @ResponseBody
    @RequestMapping(value = "/statics.do", method = RequestMethod.GET)
    public Response findPage(
            @RequestParam(value = "from", required = false, defaultValue = "") String from,
            @RequestParam(value = "to", required = false, defaultValue = "1") String to,
            @RequestParam(value = "url", required = false, defaultValue = "3") String url
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
                if (keys.size() <= 0) {
                    return null;
                } else {
                    double min = 0.0D;
                    double max = 0.0D;
                    double deta = 600000.0D;
                    if (from.length() == 0 && to.length() == 0) {
                        Set<Tuple> set = jedis.zrevrangeWithScores(String.valueOf(keys.toArray()[0]), 0L, 0L);
                        max = ((Tuple[])set.toArray(new Tuple[0]))[0].getScore();
                        min = max - deta;
                        fromNew = TimeUtil.format((long)min, format);
                        toNew = TimeUtil.format((long)max, format);
                    } else if (from.length() == 0) {
                        max = (double)TimeUtil.format(to, format).getTime();
                        min = max - deta;
                        fromNew = TimeUtil.format((long)min, format);
                    } else if (to.length() == 0) {
                        min = (double)TimeUtil.format(from, format).getTime();
                        max = min + deta;
                        toNew = TimeUtil.format((long)max, format);
                    } else {
                        min = (double)TimeUtil.format(from, format).getTime();
                        max = (double)TimeUtil.format(to, format).getTime();
                    }

                    deta = max - min;
                    Iterator var27 = keys.iterator();

                    while(true) {
                        String type;
                        ArrayList lineAve;
                        ArrayList line;
                        String name;
                        do {
                            if (!var27.hasNext()) {
                                return (new Bean()).put("FROM", fromNew).put("TO", toNew).put("URL", url);
                            }

                            String key = (String)var27.next();
                            type = "bar";
                            if (listXs.size() == 0) {
                                Set<Tuple> rowWithScore = jedis.zrangeByScoreWithScores(key, min, max);
                                Iterator var16 = rowWithScore.iterator();

                                while(var16.hasNext()) {
                                    Tuple colTuple = (Tuple)var16.next();
                                    double score = colTuple.getScore();
                                    String colx = colTuple.getElement();
                                    listXs.add(TimeUtil.formatAuto((long)score, (long)deta));
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
                            Iterator var24 = row.iterator();

                            while(var24.hasNext()) {
                                String col = (String)var24.next();
                                String[] cc = col.split(" +");
                                lineQpsNet.add(cc[4]);
                                lineQpsWait.add(cc[9]);
                                lineQpsDone.add(cc[14]);
                                lineAveNet.add(cc[6]);
                                lineAveWait.add(cc[11]);
                                lineAveDone.add(cc[16]);
                            }

                            name = key.substring(start.length());
                            items.add(name);
                        } while(url.length() > 0 && !url.equals(name));

                        series.add((new Bean()).put("name", name + ":net").put("type", type).put("stack", name).put("data", line.get(0)));
                        series.add((new Bean()).put("name", name + ":wait").put("type", type).put("stack", name).put("data", line.get(1)));
                        series.add((new Bean()).put("name", name + ":done").put("type", type).put("stack", name).put("data", line.get(2)));
                        series2.add((new Bean()).put("name", name + ":net").put("type", type).put("stack", name).put("data", lineAve.get(0)));
                        series2.add((new Bean()).put("name", name + ":wait").put("type", type).put("stack", name).put("data", lineAve.get(1)));
                        series2.add((new Bean()).put("name", name + ":done").put("type", type).put("stack", name).put("data", lineAve.get(2)));
                    }
                }
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
}
