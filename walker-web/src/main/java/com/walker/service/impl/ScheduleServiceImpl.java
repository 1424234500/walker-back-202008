package com.walker.service.impl;

import com.walker.service.ScheduleService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

/**
 *
 * quartz实现
 *
 */
@Service("scheduleService")
class ScheduleServiceImpl implements ScheduleService {
	private Logger log = LoggerFactory.getLogger(getClass());


	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;

	private Scheduler getScheduler() {
		return schedulerFactoryBean.getScheduler();
	} 

	@Override
	public void start() throws Exception {
		log.info("start");
		if(!getScheduler().isStarted())
			getScheduler().start();
	}
	@Override
	public void pause() throws Exception {
		log.info("pause");
		getScheduler().pauseAll();
	}
	@Override
	public void shutdown() throws Exception {
		log.info("shutdown");
		if(!getScheduler().isShutdown())
			getScheduler().shutdown();
	}

	@Override
	public void add(com.walker.core.scheduler.Task task) throws Exception {
		log.info("add " + task.toString());
		Scheduler scheduler = getScheduler();
		JobDetail jobDetail = task.getJobDetail();//makeJobDetail(task);
		Trigger trigger = task.getTrigger();//makeTrigger(task);
		// 将任务及其触发器放入调度器
		scheduler.scheduleJob(jobDetail, trigger);
		start();
	}
	@Override
	public void remove(com.walker.core.scheduler.Task task) throws Exception {
		log.info("remove " + task.toString());

		JobDetail job = task.getJobDetail();
		getScheduler().deleteJob(job.getKey());
	}
	/**
	 * 只修改触发器 
	 * 以className methodName args[] 为键修改
	 */
	@Override
	public void update(com.walker.core.scheduler.Task task) throws Exception {
		log.info("update " + task.toString());

		remove(task);
		add(task);
	} 

}
