package com.walker.event.listener;

import com.walker.common.util.*;
import com.walker.core.scheduler.Task;
import com.walker.service.ScheduleService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * 启动listener类，用于系统环境总体初始化
 *
 * 迟于springmvc onload执行
 *
 */
public class ContextListener implements ServletContextListener {
    private static Logger log = Logger.getLogger("ContextListener");

    @Autowired
    ScheduleService scheduleService;

    /**
     * 初始化系统
     * @param sce 存放于WEB.XML中的配置信息
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 加载配置参数
        log.info(".........................................................");
        log.info(".........................................................");
        log.info("..start quartz.......................................................");
        try {
            scheduleService.start();
            scheduleService.add(new Task("util.scheduler.job.JobTest","quartz scheduler tools out"));
        }catch (Exception e){
            log.error("quartz start error", e);
        }
        log.info(".........................................................");
        log.info("正在启动系统 ... ...");
        ServletContext sc = sce.getServletContext();
        // 获取系统真实路径
        String systemPath = sc.getRealPath("/");
        if (!systemPath.endsWith(File.separator)) {
            systemPath += ",";
        }

        log.info("系统工作目录: " + systemPath);

        startComp();
//        startTestSelf();

        addShutdownHook();	//添加java程序关闭监听
        log.info("系统初始化完毕，开始接收请求！");
        log.info("........................................................");
        log.info("........................................................");
        log.info("........................................................");


    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {

                log.info("........................................................");
                log.info("........................................................");
                log.warn("----------销毁 执行ShutdownHook----------");
                String nowTime = Tools.getNowTimeL();
                String str = Pc.getRuntime();
                log.warn(nowTime);
                log.warn(str);
                log.warn("----------销毁 执行ShutdownHook 完毕----------");

                log.info("........................................................");
                log.info("........................................................");
            }
        });

    }
    /**
     * 挂载组件 初始化模块
     */
    private void startComp(){
        log.info("&&&&&&&&&&&&&&&&&&&&& 开始初始化call调用 &&&&&&&&&&&&&&&&&&&&");

        //初始化文件目录
//
//		//初始化缓存配置
//		Cache<String> cache = CacheMgr.getInstance();
//		String str = cache.get("on_list_start", ""); //来源于*.properties
////		onstart=util.cache.CacheMgr,util.annotation.TrackerMgr
//		String[] arr = str.split(",");
//		int i = 1;
//		for(final String clz : arr){
//			final int ii = i++;
//			//使用缓冲队列任务的形式来 隔离 避免runtimeException 和相干
//			ThreadUtil.execute(Type.SingleThread, new Runnable(){
//				public void run(){
//					log.info("*******************************************");
//					log.info("********** step." + ii + "\t " + clz + ".init()");
//					log.info("*******************************************");
//					ClassUtil.doClassMethod(clz, "init");
//				}
//			});
//		}

        log.info("&&&&&&&&&&&&&&&&&&&&&&& &&! 开始初始化call调用 &&&&&&&&&&&&&&&&&&&&&&");
    }


    /**
     * 用以触发springmvc的代理 初始化
     */
    private void startTestSelf() {
        ThreadUtil.schedule(new Runnable() {
            @Override
            public void run() {
                log.info("######################开启延时测试初始化springMvc#######################");
                try {
                    log.info(HttpUtil.doPost("http://localhost:8080/walker-web/tomcat/listCacheMap.do", new Page().toBean(), null, null));
                    log.info("######################测试地址完毕#######################");
                } catch (Exception e) {
                    log.info("地址测试异常 等会儿重新测试");
                    startTestSelf();
                }
            }
        }, 10, TimeUnit.SECONDS);


    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        log.info("...... ...................................................");
        log.info("...... ...................................................");
        log.info("...... ...................................................");
        log.info("系统关闭！");
        log.info("...... ...................................................");
        log.info("...... ...................................................");
        log.info("...... ...................................................");


    }
}
