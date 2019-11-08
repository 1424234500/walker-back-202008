package com.walker.service.impl;

import com.walker.service.ScheduleService;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * quartz实现
 *
 */
class ScheduleServiceImpl implements ScheduleService {
	private static Logger log = Logger.getLogger(ScheduleServiceImpl.class);

	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;

	private Scheduler getScheduler() {
		return schedulerFactoryBean.getScheduler();
	} 

	@Override
	public void start() throws Exception {
		if(!getScheduler().isStarted())
			getScheduler().start();
	}
	@Override
	public void pause() throws Exception {
		getScheduler().pauseAll();
	}
	@Override
	public void shutdown() throws Exception {
		if(!getScheduler().isShutdown())
			getScheduler().shutdown();
	}

	@Override
	public void add(com.walker.core.scheduler.Task task) throws Exception {
		Scheduler scheduler = getScheduler();
		JobDetail jobDetail = task.getJobDetail();//makeJobDetail(task);
		Trigger trigger = task.getTrigger();//makeTrigger(task);
		// 将任务及其触发器放入调度器
		scheduler.scheduleJob(jobDetail, trigger);
		start();
	}
	@Override
	public void remove(com.walker.core.scheduler.Task task) throws Exception {
		JobDetail job = task.getJobDetail();
		getScheduler().deleteJob(job.getKey());
	}
	/**
	 * 只修改触发器 
	 * 以className methodName args[] 为键修改
	 */
	@Override
	public void update(com.walker.core.scheduler.Task task) throws Exception {
		remove(task);
		add(task);
	} 

}
