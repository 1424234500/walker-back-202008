package com.walker.core.scheduler;


import java.util.List;

/**
 * 定时器任务调度工具
 *
 * 初始化任务 db cache
 * 
 * 加入任务
 * 移除任务
 * 修改任务
 * 
 * 策略 异常上抛 用者处理
 */
public interface Scheduler{
	
	 /**
     * 启动
     */
    public void start() throws Exception;

	 /**
     * 暂停
     */
    public void pause() throws Exception;

    
    /**
     * 关闭
     */
    public void shutdown() throws Exception;

    /**
     * 添加任务
     * @return
     */
    public Task add(Task task) throws Exception;

    /**
     * 立即执行任务
     * @return
     */
    public Task run(Task task) throws Exception;


    /**
     * 移除任务
     * @return
     */
    public Task remove(Task task) throws Exception;
    
    /**
     * 保存任务  添加删除多个触发器
     * @return
     */
    public Task save(Task task) throws Exception;


    public Task saveTrigger(Task task, List<String> cronOn, List<String> cronOff) throws Exception;



}
