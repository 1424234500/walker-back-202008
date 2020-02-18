package com.walker.quartz;

import com.walker.common.util.Pc;
import com.walker.common.util.Tools;
import com.walker.core.aop.FunReturn;
import com.walker.mode.LogModel;
import com.walker.service.Config;
import com.walker.service.LogService;
import com.walker.util.SpringContextUtil;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *  extends org.quartz.InterruptableJob	?
 */
public abstract class TaskJob extends QuartzJobBean {
	private Logger log = LoggerFactory.getLogger(getClass());


	LogService logService = SpringContextUtil.getBean("logService");
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException{
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
		//任务名 类名 触发器名
		LogModel logModel = new LogModel()
				.setCATE(Config.getCateJob())
				.setUSER(Config.getSystemUser())
				.setIP_PORT_FROM(Pc.getIp())
				.setIP_PORT_TO(Pc.getIp())
				.setWAY(jobName)
				.setURL(className)
				.setARGS(triggerInfo)
				.setCOST(0l)
				.setIS_EXCEPTION(Config.FALSE)
				.setIS_OK(status)
				.setABOUT("任务调度日志")
				.setRES("");

		logService.saveLogModel(logModel);
		try {
			String res = this.make();

			status = "2";
			logModel.setIS_EXCEPTION(Config.FALSE)
					.setRES(res)
					;
		}catch (Exception e){
			status = "0";
			logModel.setIS_EXCEPTION(Config.TRUE).setEXCEPTION(Tools.toString(e));
			throw e;
		}finally {
			long stopTime = System.currentTimeMillis();
			long cost = stopTime - startTime;
			logModel.setIS_OK(status).setCOST(cost);
			if(logModel.getIS_EXCEPTION().equalsIgnoreCase(Config.TRUE)){
				log.error(logModel.toString());
			}else{
				log.info(logModel.toString());
			}
			logService.saveLogModel(logModel);
		}

	}

	public abstract String make();

}
