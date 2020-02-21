package com.walker.controller;

import com.walker.Response;
import com.walker.common.util.Bean;
import com.walker.common.util.MapListUtil;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.core.database.Redis;
import com.walker.dao.JdbcDao;
import com.walker.dao.mapper.StatisticsMapper;
import com.walker.service.BaseService;
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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.*;

/**
 * Tomcat监控后台
 * @author Walker
 *
 */
@Controller
@RequestMapping("/tomcat")
public class TomcatController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    JdbcDao baseService;
    @Autowired
    StatisticsMapper statisticsMapper;
    
    @ApiOperation(value = "统计", notes = "")
    @ResponseBody
    @RequestMapping(value = "/statics.do", method = RequestMethod.GET)
    public Response statics(
            @RequestParam(value = "url", required = false, defaultValue = "") String url
    ){

        List<List<String>> list = null;
        List<Map<String, Object>> listDb = null;
        if (Tools.notNull(new Object[]{url}) && !url.toLowerCase().equals("undefined") && !url.toLowerCase().equals("null")) {
            if (baseService.getDs().equals("oracle")) {
                list = MapListUtil.toArrayAndTurn(baseService.find(" SELECT LEV, NVL(TIME, '0') TIME FROM  ( SELECT HOUR, CAST(SUM(COSTTIME)/SUM(COUNT)/1000 AS NUMBER(8, 3)) TIME FROM (  SELECT  TO_CHAR(LT.TIME, 'HH24') HOUR, LT.COUNT, LT.COSTTIME FROM W_LOG_TIME LT WHERE 1=1 AND LT.URL=?  )GROUP BY HOUR   ) T1,  ( SELECT LPAD(LEVEL, 2, '0') LEV FROM DUAL CONNECT BY LEVEL <= 24    ) T2 WHERE T1.HOUR(+) = T2.LEV  ORDER BY LEV ", new Object[]{url}));
            } else {
                list = MapListUtil.toArrayAndTurn(baseService.find(" SELECT T2.LEV, IFNULL(T1.TIME, '0') TIME FROM   ( SELECT HOUR, (0 + SUM(COSTTIME)/SUM(COUNT)/1000) TIME FROM (  SELECT  SUBSTR(LT.TIME,12,2) HOUR, LT.COUNT, LT.COSTTIME FROM W_LOG_TIME LT WHERE 1=1 AND LT.URL=?  ) T GROUP BY HOUR   ) T1 RIGHT JOIN  ( select lpad(level, 2, '0') lev from (select  (@i/*'*/:=/*'*/@i+1) level from  information_schema.COLUMNS t ,(select   @i/*'*/:=/*'*/0) it ) t  where level<=24    ) T2 ON T1.HOUR=T2.LEV ORDER BY LEV\t", new Object[]{url}));
            }
        } else if (baseService.getDs().equals("oracle")) {
            list = MapListUtil.toArrayAndTurn(baseService.find("SELECT URL,CAST(SUM(COSTTIME)/SUM(COUNT)/1000 AS NUMBER(8, 3)) TIME FROM W_LOG_TIME WHERE 1=1 GROUP BY URL ORDER BY URL ", new Object[0]));
        } else {
//            list = MapListUtil.toArrayAndTurn(baseService.find("SELECT URL,(0 + SUM(COSTTIME)/SUM(COUNT)/1000) TIME FROM W_LOG_TIME WHERE 1=1 GROUP BY URL ORDER BY URL ", new Object[0]));
            listDb = statisticsMapper.findAction();
            list = MapListUtil.toArrayAndTurn(listDb);
        }
//| X                            | Y1   | Y2   | Y3   | Y4   | Y5   |
//| com.walker.job.JobMakeUser   |   10 |    0 |    0 | NULL |    0 |
//| /common/findPage.do          |    2 |    0 |    0 | NULL |    0 |
//| com.walker.job.JobUpdateArea |    1 |    0 |    0 | NULL |    0 |
//        转置矩阵
//  url1, url2, url3
//  y1-1, y1-2, y1-3
//  y2-2, y2-2, y2-3
//  com.walker.job.JobMakeUser      /common/findPage.do        com.walker.job.JobUpdateArea
//  10                              2                           0
//  0                               0                           0
//  0                               0                           0
//
/**
 * 多指标  按   接口排名
 * X      Y1        Y2              Y3          Y4              Y5
 * 接口名  成功次数  成功平均耗时      失败次数      失败平均耗时     成功率0～100
 */
        String[] names = {"成功次数", "成功耗时", "失败次数", "失败耗时", "成功率"};
        String[] types = {"bar", "bar", "bar", "bar", "line"};
        String[] stacks = {"1", "1", "1", "1", "2"};    //分组 堆叠

        final String format = "yyyy-MM-dd HH:mm";
        final List<String> listXs = list.get(0);
        final List<Bean> series = new ArrayList();
        final Set<String> items = new HashSet();
        //指标 结构 [ {  name: '百度', type: 'bar', stack: '搜索引擎', data: [620, 732, 701, 734, 1090, 1130, 1120]  }, ]
        for(int i = 1; i < list.size(); i++){
            List<String> line = list.get(i);
            series.add(new Bean().set("name", names[i - 1]).set("type", types[i - 1]).set("stack", stacks[ i - 1]).set("data", line)   );
        }

        Map option = MapListUtil.map()
                .put("title", "统计action")
                .put("legend", new Bean().put("data", names))   //线 y轴值中文名列表
                .put("tooltip", new HashMap())
                .put("xAxis", Arrays.asList(new Bean().put("data", listXs)))     //x轴 坐标中文名列表
                .put("yAxis", Arrays.asList(new HashMap())) //若无报错YAxis 0 not found
                .put("series", series)      //指标 结构 [ {  name: '百度', type: 'bar', stack: '搜索引擎', data: [620, 732, 701, 734, 1090, 1130, 1120]  }, ]
                .build();


        Map res = MapListUtil.getMap()
                .put("res", "true")
                .put("option", option)
                .put("items", listXs)
                .put("args", new Bean())
                .build();

        return Response.makeTrue("", res);

    }
//option = {
//        legend: {
//            data: ['百度', '谷歌', '必应', '其他']
//        },
//        xAxis: [
//            {
//                data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
//            }
//        ],
//        yAxis: [
//            {  }
//        ],
//        series: [
//            {
//                name: '百度', type: 'bar', stack: '搜索引擎',
//                data: [620, 732, 701, 734, 1090, 1130, 1120]
//            },
//            {
//                name: '谷歌', type: 'bar', stack: '搜索引擎',
//                data: [120, 132, 101, 134, 290, 230, 220]
//            },
//        ]
//    };

    @ApiOperation(value = "统计行为耗时", notes = "")
    @ResponseBody
    @RequestMapping(value = "/staticscount.do", method = RequestMethod.GET)
    public Response staticscount(
            @RequestParam(value = "url", required = false, defaultValue = "") String url
    ){
        List list = null;
        if (Tools.notNull(new Object[]{url}) && !url.toLowerCase().equals("undefined") && !url.toLowerCase().equals("null")) {
            if (baseService.getDs().equals("oracle")) {
                list = MapListUtil.toArrayAndTurn(baseService.find("  SELECT LEV, NVL(COUNT, '0') SUMCOUNT FROM  ( SELECT HOUR, SUM(COUNT) COUNT FROM (  SELECT  TO_CHAR(LT.TIME, 'HH24') HOUR, LT.URL, LT.COUNT FROM W_LOG_TIME LT WHERE 1=1 AND LT.URL=?  )GROUP BY HOUR ) T1,  ( SELECT LPAD(LEVEL, 2, '0') LEV FROM DUAL CONNECT BY LEVEL <= 24    ) T2 WHERE T1.HOUR(+) = T2.LEV  ORDER BY LEV ", new Object[]{url}));
            } else {
                list = MapListUtil.toArrayAndTurn(baseService.find("   SELECT T2.LEV, IFNULL(T1.COUNT, '0') SUMCOUNT FROM   ( SELECT HOUR, SUM(COUNT) COUNT FROM (  SELECT  SUBSTR(LT.TIME,6,2) HOUR, LT.URL, LT.COUNT FROM W_LOG_TIME LT WHERE 1=1 AND LT.URL=?  ) T GROUP BY HOUR ) T1 RIGHT JOIN ( select lpad(level, 2, '0') lev from (select  (@i/*'*/:=/*'*/@i+1) level from  information_schema.COLUMNS t ,(select   @i/*'*/:=/*'*/0) it ) t  where level<=12  ) T2 ON T1.HOUR = T2.LEV  ORDER BY T2.LEV", new Object[]{url}));
            }
        } else if (baseService.getDs().equals("oracle")) {
            list = MapListUtil.toArrayAndTurn(baseService.find(" SELECT URL,SUM(COUNT) SUMCOUNT FROM W_LOG_TIME WHERE 1=1 GROUP BY URL ORDER BY URL ", new Object[0]));
        } else {
            list = MapListUtil.toArrayAndTurn(baseService.find(" SELECT URL,SUM(COUNT) SUMCOUNT FROM W_LOG_TIME WHERE 1=1 GROUP BY URL ORDER BY URL ", new Object[0]));
        }

        List listLineNames = MapListUtil.array().add("action").build();
        List listSeries = MapListUtil.array().add(list.size() > 0 ? (List)list.get(1) : new Bean()).build();
        String type = "bar";
        Map title = MapListUtil.map().put("text", "统计").build();
        Map legend = MapListUtil.map().put("data", listLineNames).build();
        Map xAxis = MapListUtil.map().put("data", list.size() > 0 ? (List)list.get(0) : new Bean()).build();
        List series = MapListUtil.array().build();

        for(int i = 0; i < listSeries.size(); ++i) {
            series.add(MapListUtil.map().put("name", listLineNames.get(i)).put("type", type).put("data", listSeries.get(i)).build());
        }

        Map option = MapListUtil.map()
                .put("title", title)
                .put("legend", legend)
                .put("tooltip", new HashMap())
                .put("xAxis", xAxis)
                .put("yAxis", new HashMap()) //若无报错YAxis 0 not found
                .put("series", series)
                .build();


        Map res = MapListUtil.getMap()
                .put("res", "true")
                .put("option", option)
                .build();

        log.info(res.toString());

        return Response.makeTrue(url, res);
    }

}