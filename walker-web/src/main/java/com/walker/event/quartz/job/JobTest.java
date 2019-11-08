package com.walker.event.quartz.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobTest extends QuartzJobBean {


	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		String taskName = context.getJobDetail().getJobDataMap().getString("name");



	}

	
}
