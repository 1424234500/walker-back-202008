package com.walker.quartz;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;


public abstract class TaskJob extends QuartzJobBean implements Runnable {
	private Logger log = LoggerFactory.getLogger(getClass());

	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException{
		JobDetail jobDetail = context.getJobDetail();
		Trigger trigger = context.getTrigger();
		CronTrigger cronTrigger = (CronTrigger) trigger;
		log.info("Scheduler quartz " + this.getClass().toString() + " " + cronTrigger.getCronExpression() + " " + cronTrigger.getDescription());
		log.info("desc:" + jobDetail.getDescription());
		
		this.run();
		
	}

	
}
