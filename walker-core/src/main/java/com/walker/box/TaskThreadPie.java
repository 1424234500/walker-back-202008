package com.walker.box;


import com.walker.common.util.ThreadUtil;
import com.walker.common.util.Tools;
import com.walker.system.Pc;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程协作统计工具抽象
 *
 * 我有多少个任务
 *
 * 你按配置开启10个线程协作完成任务
 * 你按配置控制队列最多允许排队200个
 * 你得让我实现每个任务 你得告诉我该实现第n个任务  要能迭代器模式
 * 你得按配置定时5s展示一下进度信息
 * 你得在所有任务完成后 展示结果统计 执行了都少个任务 异常了多少个 耗时了多久
 *
 *
 *
 */
abstract class Pie{
    abstract void onStartThread(int threadNo);
    void onScheduleRun(){
        Tools.out(process());
    };
    void onAllThreadFinished(long costAll, int countAll, int countException){
        Tools.out(process());
    };
    abstract String process();
}
public abstract class TaskThreadPie extends Pie{
    boolean isDetail = false;
    int threadSize = Runtime.getRuntime().availableProcessors();
    long sleepTimeSch = 5000;

    long timeStart = System.currentTimeMillis();
    ThreadPoolExecutor pool;
    ScheduledExecutorService sch;

    AtomicInteger countNow; //当前完成数量
    AtomicInteger countException; //当前异常数量
    int countAll;   //总任务数量

    public boolean isDetail() {
        return isDetail;
    }

    public TaskThreadPie setDetail(boolean detail) {
        isDetail = detail;
        return this;
    }

    public int getThreadSize() {
        return threadSize;
    }

    public TaskThreadPie setThreadSize(int threadSize) {
        this.threadSize = threadSize;
        return this;
    }

    public long getSleepTimeSch() {
        return sleepTimeSch;
    }

    public TaskThreadPie setSleepTimeSch(long sleepTimeSch) {
        this.sleepTimeSch = sleepTimeSch;
        return this;
    }

    public int getCountAll() {
        return countAll;
    }

    public TaskThreadPie setCountAll(int countAll) {
        this.countAll = countAll;
        return this;
    }

    TaskThreadPie(int countAll){
        this.countAll = countAll;
    }
    void start() {
        Tools.out("start pie ", threadSize, countAll, sleepTimeSch, isDetail);
        countException = new AtomicInteger(0);
        countNow = new AtomicInteger(0);
//        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(this.threadSize){
        pool = new ThreadPoolExecutor(this.threadSize, this.threadSize
                , 0L, TimeUnit.MILLISECONDS
                , new LinkedBlockingQueue<Runnable>(this.countAll)
                , new ThreadPoolExecutor.AbortPolicy()
        ){
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
//                System.out.println("准备执行线程：" + r.toString() +"==="  + t.getName());
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
//                System.out.println("执行完成线程：" + r.toString());
            }

            @Override
            protected void terminated() {
                Tools.out("thread pool exit" );
            }

        };
        sch = Executors.newSingleThreadScheduledExecutor();
        sch.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                onScheduleRun();
            }
        }, 0, this.sleepTimeSch, TimeUnit.MILLISECONDS);
        int nowi = 0;
        while (nowi < countAll) {
            int ccc = pool.getCorePoolSize();
            int acc = pool.getActiveCount();
            int qcc = pool.getQueue().size();
            if(qcc < ccc * 100) {
                final int tno = nowi++;
                detail("init start thread", tno, acc, qcc, ccc);
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        long timeStart = System.currentTimeMillis();
                        try {
                            onStartThread(tno);
                        } catch (Exception e) {
                            countException.addAndGet(1);
                            throw e;
                        } finally {
                            countNow.addAndGet(1);
                            long deta = System.currentTimeMillis() - timeStart;
                            detail("##thread finish " + tno + " " + Tools.calcTime(deta));
                        }
                    }
                });
            }else if(sleepTimeSch > 0){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        pool.shutdown();
        try{
            pool.awaitTermination(9999999, TimeUnit.DAYS);
        }catch (Exception e){
            e.printStackTrace();
        }
        sch.shutdownNow();
        onAllThreadFinished(System.currentTimeMillis() - timeStart, countAll, countException.get());
    }

    @Override
    public String process() {
//        deta countNow = all countAll
        long deta = System.currentTimeMillis() - timeStart;
        long all = (long) (1f * deta * countAll / countNow.get());
        long last =  all - deta;
        return "Process[count " + countNow + "/" + countException + "/" + countAll
                + " percent " + (int)(countNow.get() * 1000f / countAll) / 10f + "% " +
                " cost " + Tools.calcTime(deta) + " last " + Tools.calcTime(last) + " all " + Tools.calcTime(all) + "]";
    }


    private void detail(Object...objects){
        if(this.isDetail)
        Tools.out(objects);
    }


}
