package com.walker.service;


import com.walker.common.util.*;
import com.walker.config.ShiroConfig;
import com.walker.core.tool.Pc;
import com.walker.dao.JdbcDao;
import com.walker.mode.LogModel;
import com.walker.mode.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 模拟用户操作
 */
@Service("makeTestService")
public class MakeTestService {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    LogService logService;
    @Autowired
    JdbcDao jdbcDao;
    @Autowired
    ShiroConfig shiroConfig;

    ExecutorService executorService = ThreadUtil.getExecutorServiceInstance(4);
    @Autowired
    private RedisTemplate redisTemplate;


    public long makeUrlTestRandomThread(long timeA, long timeD, int threadSize) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                makeUrlTestRandom(timeA, timeD, threadSize);
            }
        });
        return 0;
    }

    /**
     * 开启threadSize个线程，持续timeAll时间，每个线程访问间隔timeDeta
     */
    public long makeUrlTestRandom(long timeA, long timeD, int threadSize){
        final long start = System.currentTimeMillis();
        if(threadSize <= 0){
            threadSize = 1;
        }
        if(timeA <= 1000){
            timeA = 1000;
        }
        if(timeD <= 10){
            timeD = 10;
        }
        final long timeDeta = timeD;
        final long timeAll = timeA;
        LogModel logModel = new LogModel()
                .setCATE(Config.getCateTest())
                .setUSER("test")
                .setIP_PORT_FROM(Pc.getIp())
                .setIP_PORT_TO(Pc.getIp())
                .setWAY("")
                .setURL("makeUrlTestRandom")
                .setARGS(new Bean().put("timeAll", timeAll).put("timeDeta", timeDeta).put("threadSize", threadSize).toString())
                .setCOST(System.currentTimeMillis())
                ;
        log.info("start makeUrlTestRandom " + logModel);
        logModel = logService.saveLogModelNoTime(logModel);
        AtomicLong count = new AtomicLong(0);
        try{
            ExecutorService service = Executors.newFixedThreadPool(threadSize + 1);
            for(int i = 0; i < threadSize; i++) {
                final int tno = i;
                service.execute(new Runnable() {
                    @Override
                    public void run() {
//                        登录
                        String token = shiroConfig.onlineUser(new User().setID("test" + tno).setPWD("test"));

                        List<Map<String, Object>> list = jdbcDao.findPageRand(100,
                                "select * from W_LOG_MODEL t where 1=1 " +
                                        "and t.WAY='GET' " +
                                        "and ( t.URL like '%find%' or t.URL like '%get%' ) " +
                                        "and t.CATE=? ", Config.getCateController() );
                        if(list.size() == 0){
                            log.error("no date to do");
                            return ;
                        }

                        while( ! Thread.interrupted()){
                            int i = (int) (Math.random() * list.size());
                            Map<String, Object> map = list.get(i);
                            String url = String.valueOf(map.get("URL"));    ///common/findPage.do
                            String argsStr = String.valueOf(map.get("ARGS"));//{"showNum":"8","_DATABASE_":"walker","_TABLE_NAME_":"W_LOG_MODEL","nowPage":"1","order":""}
                            try {
                                url = "http://127.0.0.1:8090" + url;
                                Bean args = JsonUtil.get(argsStr);
                                String res = new HttpBuilder(url, HttpBuilder.Type.GET)
                                        .setHeaders(new Bean().put("TOKEN", token))
                                        .setConnectTimeout(3000)
                                        .setRequestTimeout(3000)
                                        .setSocketTimeout(4000)
                                        .setData(args)
                                        .buildString()
                                ;
                                log.info("makeUrl: " + url + " res:" + res.substring(0, Math.min(500, res.length())));
                                count.addAndGet(1);
                            }catch (Exception e){
                                log.error(e.getMessage(), e);
                            }
                            if(System.currentTimeMillis() - start > timeAll){
                                break;
                            }
                            try {
                                Thread.sleep(timeDeta);
                            } catch (InterruptedException e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                        log.info("makeTest thread over");
                    }
                });
            }
            service.awaitTermination(timeAll + 3000, TimeUnit.MILLISECONDS);
            logModel.setRES(count.toString());
        }catch (Exception e){
            logModel.setIS_EXCEPTION(Config.TRUE).setEXCEPTION(Tools.toString(e)).setIS_OK(Config.FALSE);
        }finally {
            logModel.setCOST(System.currentTimeMillis() - logModel.getCOST());
            logService.saveLogModel(logModel);
        }

        return count.get();


    }


}
