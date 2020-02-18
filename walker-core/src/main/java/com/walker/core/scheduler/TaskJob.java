package com.walker.core.scheduler;

import com.walker.common.util.Bean;
import com.walker.core.aop.FunReturn;
import org.apache.log4j.Logger;
import org.quartz.*;


public abstract class TaskJob implements Job {
	private static Logger log = Logger.getLogger(TaskJob.class);


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetail jobDetail = context.getJobDetail();
		Trigger trigger = context.getTrigger();
		String triggerInfo = "";
		if(trigger instanceof CronTrigger) {
			CronTrigger cronTrigger = (CronTrigger) trigger;
			triggerInfo = cronTrigger.getCronExpression() + " " + cronTrigger.getDescription();
		}else{
			triggerInfo = trigger.toString();
		}
		String jobName = jobDetail.getKey().getName();
		String className = jobDetail.getJobClass().getName();
		String status = "1";//状态0失败1执行中2成功
		long startTime = System.currentTimeMillis();
		log.info("Scheduler quartz " + className + " " + triggerInfo);
		log.info("Desc:" + jobDetail.getDescription());

		String res = this.make();

		log.info(res);
	}
	public abstract String make();
}
