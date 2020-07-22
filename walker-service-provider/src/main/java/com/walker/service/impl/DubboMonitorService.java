/**
 * Copyright 2006-2015 handu.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.walker.service.impl;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.monitor.MonitorService;
import com.google.common.collect.Maps;
import com.walker.common.util.LangUtil;
import com.walker.common.util.TimeUtil;
import com.walker.dao.DubboInvokeRepository;
import com.walker.mode.DubboInvoke;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * MonitorService
 *
 * 参考handu dubbo-monitor 修改实现监控monitor存储 自定义查询 自定义图表
 */
@Service("monitorService")
public class DubboMonitorService implements com.alibaba.dubbo.monitor.MonitorService {

    private Logger log = LoggerFactory.getLogger(getClass());

    public static final String CLASSNAME = DubboMonitorService.class.getName() + ".";

//    private static final String[] types = {SUCCESS, FAILURE, ELAPSED, CONCURRENT, MAX_ELAPSED, MAX_CONCURRENT};

    private static final String POISON_PROTOCOL = "poison";

    private static final String TIMESTAMP = "timestamp";

    private volatile boolean running = true;

    private Thread writeThread;

    private BlockingQueue<URL> queue;

//    @Autowired
//    private RegistryContainer registryContainer;
//
//    @Autowired
//    private Dao dao;
    @Autowired
    private DubboInvokeRepository dubboInvokeRepository;

    @PostConstruct
    private void init() {
        queue = new LinkedBlockingQueue<URL>(Integer.parseInt(ConfigUtils.getProperty("dubbo.monitor.queue", "100000")));
        writeThread = new Thread(new Runnable() {
            public void run() {
                while (running) {
                    try {
                        writeToDataBase(); // 记录统计日志
                    } catch (Throwable t) { // 防御性容错
                        log.error("Unexpected error occur at write stat log, cause: " + t.getMessage(), t);
                        try {
                            Thread.sleep(5000); // 失败延迟
                        } catch (Throwable t2) {
                        }
                    }
                }
            }
        });
        writeThread.setDaemon(true);
        writeThread.setName("DubboMonitorAsyncWriteLogThread");
        writeThread.start();
    }

    /**
     * Dubbo调用信息数据写入DB
     *
     * @throws Exception
     */
    private void writeToDataBase() throws Exception {
        URL statistics = queue.take();
        if (POISON_PROTOCOL.equals(statistics.getProtocol())) {
            return;
        }
        String timestamp = statistics.getParameter(Constants.TIMESTAMP_KEY);
        Date now;
        if (timestamp == null || timestamp.length() == 0) {
            now = new Date();
        } else if (timestamp.length() == "yyyyMMddHHmmss".length()) {
            now = new SimpleDateFormat("yyyyMMddHHmmss").parse(timestamp);
        } else {
            now = new Date(Long.parseLong(timestamp));
        }
        DubboInvoke dubboInvoke = new DubboInvoke();

        dubboInvoke.setID(LangUtil.getTimeSeqId());
        try {
            if (statistics.hasParameter(PROVIDER)) {
                dubboInvoke.setTYPE(CONSUMER);
                dubboInvoke.setCONSUMER(statistics.getHost());
                dubboInvoke.setPROVIDER(statistics.getParameter(PROVIDER));
                int i = dubboInvoke.getPROVIDER().indexOf(':');
                if (i > 0) {
                    dubboInvoke.setPROVIDER(dubboInvoke.getPROVIDER().substring(0, i));
                }
            } else {
                dubboInvoke.setTYPE(PROVIDER);
                dubboInvoke.setCONSUMER(statistics.getParameter(CONSUMER));
                int i = dubboInvoke.getCONSUMER().indexOf(':');
                if (i > 0) {
                    dubboInvoke.setCONSUMER(dubboInvoke.getCONSUMER().substring(0, i));
                }
                dubboInvoke.setPROVIDER(statistics.getHost());
            }
            dubboInvoke.setINVOKE_DATE(TimeUtil.getTimeYmdHms(now.getTime()));
            dubboInvoke.setSERVICE(statistics.getServiceInterface());
            dubboInvoke.setMETHOD(statistics.getParameter(METHOD));
            dubboInvoke.setINVOKE_TIME(TimeUtil.getTimeYmdHms(statistics.getParameter(TIMESTAMP, System.currentTimeMillis())));
            dubboInvoke.setSUCCESS("" + statistics.getParameter(SUCCESS, 0));
            dubboInvoke.setFAILURE("" + statistics.getParameter(FAILURE, 0));
            dubboInvoke.setELAPSED("" + statistics.getParameter(ELAPSED, 0));
            dubboInvoke.setCONCURRENT("" + statistics.getParameter(CONCURRENT, 0));
            dubboInvoke.setMAX_ELAPSED("" + statistics.getParameter(MAX_ELAPSED, 0));
            dubboInvoke.setMAX_CONCURRENT("" + statistics.getParameter(MAX_CONCURRENT, 0));
            if (dubboInvoke.getSUCCESS().equalsIgnoreCase("0")
                    && dubboInvoke.getFAILURE().equalsIgnoreCase("0")
                    && dubboInvoke.getELAPSED().equalsIgnoreCase("0")
                    && dubboInvoke.getCONCURRENT().equalsIgnoreCase("0")
                    && dubboInvoke.getMAX_ELAPSED().equalsIgnoreCase("0")
                    && dubboInvoke.getMAX_CONCURRENT().equalsIgnoreCase("0")
            ){
                return;
            }
            dubboInvokeRepository.save(dubboInvoke);

        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

//
//    /**
//     * 统计调用数据用于图表展示 使用其他自定义查询
//     *
//     * @param dubboInvoke
//     */
//    public List<DubboInvoke> countDubboInvoke(DubboInvoke dubboInvoke) {
//        if (StringUtils.isEmpty(dubboInvoke.getSERVICE())) {
//            log.error("统计查询缺少必要参数！");
//            throw new RuntimeException("统计查询缺少必要参数！");
//        }
//        return dao.getList(CLASSNAME, "countDubboInvoke", dubboInvoke);
//    }
//




    /**
     * 监控数据采集.
     * 1. 支持调用次数统计：count://host/interface?application=foo&method=foo&provider=10.20.153.11:20880&success=12&failure=2&elapsed=135423423
     * 1.1 host,application,interface,group,version,method 记录监控来源主机，应用，接口，方法信息。
     * 1.2 如果是消费者发送的数据，加上provider地址参数，反之，加上来源consumer地址参数。
     * 1.3 success,faulure,elapsed 记录距上次采集，调用的成功次数，失败次数，成功调用总耗时，平均时间将用总耗时除以成功次数。
     *
[ INFO][20200722 17:10:37][main][ [DUBBO] Notify urls for subscribe url
consumer://192.168.43.245/com.walker.service.AreaService
     ?application=service-provider
     &category=providers,configurators,routers
     &dubbo=2.5.3
     &interface=com.walker.service.AreaService
     &logger=log4j
     &methods=findsRoot,saveAll,finds,get,deleteAll,count,delete
     &owner=walker
     &pid=15901&revision=1.0
     &side=consumer&timeout=30000
     &timestamp=1595409037320
     &version=1.0
, urls: [
     dubbo://192.168.43.245:8095/com.walker.service.AreaService
     ?anyhost=true
     &application=service-provider
     &delay=-1&dubbo=2.5.3
     &interface=com.walker.service.AreaService
     &logger=log4j
     &methods=findsRoot,saveAll,finds,get,deleteAll,count,delete
     &owner=walker
     &pid=15595
     &revision=1.0
     &side=provider
     &timeout=30000
     &timestamp=1595408938648
     &version=1.0
, empty://192.168.43.245/com.walker.service.AreaService
     ?application=service-provider
     &category=configurators&dubbo=2.5.3
     &interface=com.walker.service.AreaService
     &logger=log4j
     &methods=findsRoot,saveAll,finds,get,deleteAll,count,delete
     &owner=walker&pid=15901
     &revision=1.0&side=consumer
     &timeout=30000
     &timestamp=1595409037320
     &version=1.0
, empty://192.168.43.245/com.walker.service.AreaService
     ?application=service-provider
     &category=routers&dubbo=2.5.3
     &interface=com.walker.service.AreaService
     &logger=log4j&methods=findsRoot,saveAll,finds,get,deleteAll,count,delete&owner=walker
     &pid=15901&revision=1.0
     &side=consumer&timeout=30000
     &timestamp=1595409037320&version=1.0]
, dubbo version: 2.5.3, current host: 192.168.43.245][][com.alibaba.dubbo.registry.support.AbstractRegistry.notify(AbstractRegistry.java:422)]

     * @param statistics
     */
    @Override
    public void collect(URL statistics) {
        queue.offer(statistics);
        if (log.isInfoEnabled()) {
            log.info("collect statistics: " + statistics);
        }

    }

    /**
     * 监控数据查询. 
     * 1. 支持按天查询：count://host/interface?application=foo&method=foo&side=provider&view=chart&date=2012-07-03
     * 1.1 host,application,interface,group,version,method 查询主机，应用，接口，方法的匹配条件，缺失的条件的表示全部，host用0.0.0.0表示全部。
     * 1.2 side=consumer,provider 查询由调用的哪一端采集的数据，缺省为都查询。
     * 1.3 缺省为view=summary，返回全天汇总信息，支持view=chart表示返回全天趋势图表图片的URL地址，可以进接嵌入其它系统的页面上展示。
     * 1.4 date=2012-07-03 指定查询数据的日期，缺省为当天。
     *
     * @param query
     * @return statistics
     */
    @Override
    public List<URL> lookup(URL query) {
        // TODO Auto-generated method stub
        return null;
    }
}