package com.walker.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.walker.common.util.Bean;
import com.walker.common.util.MapListUtil;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.core.database.Redis;
import com.walker.core.database.Redis.Fun;
import com.walker.web.RequestUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

@Controller
@RequestMapping("/redis")
public class RedisControl extends BaseControll {
	
	public RedisControl() {
		super(RedisControl.class, "");
	} 
	
	
	@RequestMapping("/statics.do") 
	public void statis(HttpServletRequest request, HttpServletResponse response){ 
		  
//		        legend: { data: ['线条1', '线条2']   },
//		        xAxis: 	{ data: [x1, x2, x3, x4]   }, 
//		        series: [   
//		                 { name: '线条1',    type: 'line',   data: [y1, y2, y3, y4],  },
//		                 { name: '线条2',    type: 'line',   data: [y1, y2, y3, y4],  }, 
//				]

		String from = getValue(request, "FROM");
		String to = getValue(request, "TO");
		String format = "yyyy-MM-dd mm";
		List<String> listXs = new ArrayList<>();
		List<Bean> series = new ArrayList<>();
		Bean newArg = Redis.doJedis(new Fun<Bean>() {
			@Override
			public Bean make(Jedis jedis) {
				String fromNew = from;
				String toNew = to;
				
				String start = "stat:statis:";
				
				Set<String> keys = jedis.keys(start + "*");
				if(keys.size() <= 0) return null;
				double min = 0, max = 0;
				double deta = 10 * 60 * 1000;
				if(from.length() == 0 && to.length() == 0) {//取redis存储的最近deta
//					jedis.zrange(String.valueOf(keys.toArray()[0]), -1, -1);
					Set<Tuple> set = jedis.zrevrangeWithScores(String.valueOf(keys.toArray()[0]), 0, 0);
					max = set.toArray(new Tuple[0])[0].getScore();
					min = max - deta;
					fromNew = TimeUtil.format((long)min, format);
					toNew = TimeUtil.format((long)max, format);
				}else if(from.length() == 0) {//取to之前的deta
					max = TimeUtil.format(to, format).getTime();
					min = max - deta;
					fromNew = TimeUtil.format((long)min, format);
				}else if(to.length() == 0) {//取from到from + deta
					min = TimeUtil.format(from, format).getTime();
					max = min + deta;
					toNew = TimeUtil.format((long)max, format);		
				}
				deta = max - min;
				
				for(String key : keys) {
					String type = "bar";
					if(listXs.size() == 0) {
						Set<Tuple> rowWithScore = jedis.zrangeByScoreWithScores(key, min, max);
						for(Tuple colTuple : rowWithScore) {
							double score = colTuple.getScore();
							String col = colTuple.getElement();
//							score 	299392393  2019-01-02 12:12:12
//							deta	10 * 60 * 1000
//							采样点	10 * 1000 ->60个 10min 只需要到 分钟级别 10:10 -》 20:10
							listXs.add(TimeUtil.formatAuto((long) score, (long)deta));
						}
					}
					List<List<String>> line = new ArrayList<>();
					List<String> lineQpsNet = new ArrayList<>();
					List<String> lineQpsWait = new ArrayList<>();
					List<String> lineQpsDone = new ArrayList<>();
					line.add(lineQpsNet);
					line.add(lineQpsWait);
					line.add(lineQpsDone);
					
					List<List<String>> lineAve = new ArrayList<>();
					List<String> lineAveNet = new ArrayList<>();
					List<String> lineAveWait = new ArrayList<>();
					List<String> lineAveDone = new ArrayList<>();
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
					
					String name = key.substring(start.length());
					series.add(new Bean()
							.put("name", name +":net")	//该线条的名字
							.put("type", type)	//该线条的显示方式line bar pie
							.put("stack", name)	//堆积列名
							.put("data", line.get(0))	//该线条的y值集合
					);
					series.add(new Bean()
							.put("name", name +":wait")	//该线条的名字
							.put("type", type)	//该线条的显示方式line bar pie
							.put("stack", name)	//堆积列名
							.put("data", line.get(1))	//该线条的y值集合
					);
					series.add(new Bean()
							.put("name", name +":done")	//该线条的名字
							.put("type", type)	//该线条的显示方式line bar pie
							.put("stack", name)	//堆积列名
							.put("data", line.get(2))	//该线条的y值集合
					);
				}
				return new Bean().put("FROM", fromNew).put("TO", toNew);
			}
		});
		
		
		//共x轴 多线数据 
		//x1, x2, x3, x4
		//y1, y2, y3, y4 
		//y5, y6, y7, y8   
		Bean title = new Bean().put("text", "socket统计pqs");		//标题
		List<String> listLineNames = new ArrayList<>();
		for(Bean bean : series) {
			listLineNames.add(bean.get("name", ""));
		}
		Bean legend = new Bean().put("data", listLineNames);   //线条名字集合
		Bean xAxis = new Bean().put("data", listXs);  	//x坐标集合 多线条共x轴
		Bean option = new Bean()
				.put("title", title)  
				.put("legend", legend) 
				.put("tooltip", new Bean()) //若无则不能预览
				.put("xAxis", xAxis) 
				.put("yAxis", new Bean()) 	//若无则报错YAxis 0 not found
				.put("series", series) 		//lines
				;
		 
		Bean res = new Bean()
				.put("format", format)
				.put("arg", newArg)
				.put("res", newArg == null)
				.put("option", option) 
				.put("info", RequestUtil.getRequestBean(request)); 
		log(res);
		echo(res);
	}	
	
	
}