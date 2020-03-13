package com.walker.box;


import com.walker.common.util.ThreadUtil;
import com.walker.common.util.Tools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程协作统计工具抽象
 */
interface Pie{
    void onStartThread(int threadNo);
    void onScheduleRun();
    void onAllThreadFinished();
    String process();
    Integer processAdd(Integer count);

}
public abstract class TaskThreadPie implements Pie{
    long timeStart = System.currentTimeMillis();
    ExecutorService pool;
    ScheduledExecutorService sch;

    AtomicInteger countNow;
    int countAll;

    TaskThreadPie(int countAll){

    }

    TaskThreadPie(int poolSize, int threadNum, long schTimeDeta, int countAll){
        countNow = new AtomicInteger(0);
        this.countAll = countAll;
        pool = Executors.newFixedThreadPool(poolSize);
        sch = Executors.newSingleThreadScheduledExecutor();
        sch.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                onScheduleRun();
            }
        }, 0, schTimeDeta, TimeUnit.MILLISECONDS);
        for(int i = 0; i < threadNum; i++){
            final int tno = i;
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    Tools.out("##thread start " + tno);
                    long timeStart = System.currentTimeMillis();
                    onStartThread(tno);
                    long deta = System.currentTimeMillis() - timeStart;
                    Tools.out("##thread finish " + tno + " " + Tools.calcTime(deta));
                }
            });
        }

        pool.shutdown();
        try{
            pool.awaitTermination(9999999, TimeUnit.DAYS);
        }catch (Exception e){
            e.printStackTrace();
        }
        sch.shutdownNow();
        Tools.out("onAllThreadFinished");

        onAllThreadFinished();

    }

    @Override
    public String process() {
        long deta = System.currentTimeMillis() - timeStart;
        long last = (long) (1f * (countAll - countNow.get())  / countNow.get() * deta);

        return "Process[count " + countNow + "/" + countAll
                + " percent " + (int)(countNow.get() * 1000f / countAll) / 10f + "% " +
                " cost " + Tools.calcTime(deta) + " last " + Tools.calcTime(last) + "]";
    }

    @Override
    public Integer processAdd(Integer count) {
        return countNow.addAndGet(count);
    }

    public static void main(String[] argv){
        new TaskThreadPie(2, 4, 3 * 1000, 40){

            @Override
            public void onStartThread(int threadNo) {
                for(int i = 0; i < 10; i++){
                    Tools.out(threadNo, i, "do");
                    this.processAdd(1);
                    ThreadUtil.sleep(1000);
                }
            }

            @Override
            public void onScheduleRun() {
                Tools.out(this.process());
            }

            @Override
            public void onAllThreadFinished() {
                Tools.out(this.process());
            }
        };


    }


}
