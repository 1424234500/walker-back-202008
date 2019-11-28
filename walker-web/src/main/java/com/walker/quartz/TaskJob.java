package com.walker.quartz;

import com.walker.common.util.LangUtil;
import com.walker.common.util.Pc;
import com.walker.common.util.TimeUtil;
import com.walker.mode.JobHis;
import com.walker.service.LogService;
import com.walker.util.SpringContextUtil;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *  extends org.quartz.InterruptableJob	?
 */
public abstract class TaskJob extends QuartzJobBean implements Runnable {
	private Logger log = LoggerFactory.getLogger(getClass());


	LogService logService = SpringContextUtil.getBean("logService");
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException{
		JobDetail jobDetail = context.getJobDetail();
		Trigger trigger = context.getTrigger();
		CronTrigger cronTrigger = (CronTrigger) trigger;

		String className = jobDetail.getJobClass().getName();
		String status = "1";//状态0失败1执行中2成功
		String about = "";
		long startTime = System.currentTimeMillis();
		log.info("Scheduler quartz " + className + " " + cronTrigger.getCronExpression() + " " + cronTrigger.getDescription());
		log.info("Desc:" + jobDetail.getDescription());
		JobHis jobHis = new JobHis()
				.setID(LangUtil.getTimeSeqId())
				.setIP_PORT(Pc.getIp())
				.setS_TIME_START(TimeUtil.format(startTime))
				.setSTATUS(status)
				.setINFO(className)
				.setTIP(jobDetail.getDescription() + " " + cronTrigger.getCronExpression() + " " + cronTrigger.getDescription())
				;
		logService.saveJobHis(jobHis);
		try {
			this.run();
			status = "2";
		}catch (Exception e){
			status = "0";
			about += "Exception: " + e.getMessage();
			throw e;
		}finally {
			long stopTime = System.currentTimeMillis();
			long cost = stopTime - startTime;
			jobHis
			.setSTATUS(status)
			.setS_TIME_STOP(TimeUtil.format(stopTime))
			.setS_TIME_COST("" + cost)
			.setABOUT(about)
			;
			logService.saveJobHis(jobHis);
		}

	}



	
}
