package com.walker.job;

import com.walker.common.util.Bean;
import com.walker.quartz.TaskJob;
import com.walker.service.InitService;
import com.walker.service.LogService;
import com.walker.service.SyncAreaService;
import com.walker.service.SyncService;
import com.walker.util.SpringContextUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 同步 地域信息
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobUpdateArea extends TaskJob {
	private Logger log = LoggerFactory.getLogger(getClass());

//	@Autowired
//	@Qualifier("syncService")
//	private SyncService syncService;
	//此处不能自动注入? 扫描注入包配置问题
	SyncService syncService = SpringContextUtil.getBean("syncService");

	/**
	 * When an object implementing interface <code>Runnable</code> is used
	 * to create a thread, starting the thread causes the object's
	 * <code>run</code> method to be called in that separately executing
	 * thread.
	 * <p>
	 * The general contract of the method <code>run</code> is that it may
	 * take any action whatsoever.
	 *
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		log.info("begin---------");

		syncService.syncArea(new Bean().set("hello", "world"));

		log.info("end---------");

	}
}
