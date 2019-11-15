package com.walker.quartz;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;


public abstract class TaskJob extends QuartzJobBean implements Runnable {
	private Logger log = LoggerFactory.getLogger(getClass());

	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException{
		JobDetail jobDetail = context.getJobDetail();
		log.info("Scheduler quartz execute " + this.getClass().toString());
		log.info(" " + jobDetail.getDescription());
		
		this.run();
		
	}

	
}
