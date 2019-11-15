package com.walker.service;


import com.alibaba.fastjson.JSON;
import com.walker.common.util.Bean;
import com.walker.common.util.HttpBuilder;
import com.walker.config.MakeConfig;
import com.walker.core.scheduler.Task;
import com.walker.job.JobUpdateArea;
import com.walker.mode.Dept;
import com.walker.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 初始化数据服务
 */
@Service("initService")
public class InitService {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    ScheduleService scheduleService;

    @Autowired
    MakeConfig makeConfig;
    @Autowired
    DeptService deptService;

    public void initOnStart(){

    }
    public void initQuartz(){
        log.info("..start quartz.......................................................");
        try {
            scheduleService.start();
            scheduleService.add(new Task("com.walker.job.JobTest","sb quartz scheduler tools out", "0 0/2 * * * ?", "0 0/3 * * * ?"));
            scheduleService.add(new Task("com.walker.job.JobTest2","sb quartz scheduler tools out2", "0 0/5 * * * ?", "0 0/30 * * * ?"));
            scheduleService.add(new Task("com.walker.job.JobUpdateArea","update area from meituan", "0 0 1 * * ?"));
            new JobUpdateArea().run();
        }catch (Exception e){
            log.error("quartz start error", e);
        }
    }

    public void updateArea(){

//		https://www.meituan.com/ptapi/getprovincecityinfo/
//[{"provinceCode":"370000","provinceName":"山东","cityInfoList":[{"id":60,"name":"青岛","pinyin":"qingdao","acronym":"qd","rank":"B","firstChar":"Q"}


        try {
            String str = new HttpBuilder(makeConfig.urlAreaMeituan, HttpBuilder.Type.GET)
                    .setConnectTimeout(3).setRequestTimeout(3).setSocketTimeout(5)
                    .setEncode("utf-8").setDecode("utf-8").buildString();

            List<Bean> list = JSON.parseObject(str, List.class);
/*
[{
				"provinceCode":"370000",
				"provinceName":"山东",
				"cityInfoList": [{
					"id":60,
					"name":"青岛",
					"pinyin":"qingdao",
					"acronym":"qd",
					"rank":"B",
					"firstChar":"Q"
				}]
			},
]
*/
            List<Dept> depts = new ArrayList<>();
            for(Bean bean : list){
                Dept dept = new Dept().setID("D" + bean.get("provinceCode", ""))
                        .setNAME(bean.get("provinceName", ""))
                        ;
                depts.add(dept);
                List<Bean> listC = bean.get("cityInfoList", new ArrayList<>());
                for(Bean b : listC){
                    Dept d = new Dept().setID("D" + bean.get("id", ""))
                            .setNAME(bean.get("name", ""))
                            ;
                    depts.add(dept);
                }
            }

            log.info("save " + depts.size());
            if(depts.size() > 0){
                deptService.saveAll(depts);
            }

        } catch (Exception e) {
            log.error("update area error ????????????");

        }


    }

}
