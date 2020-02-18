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


	@Override
	public String make() {
		log.info("begin---------");

		Bean bean = syncService.syncArea(new Bean().set("hello", "world"));
		String res = String.valueOf(bean);
		log.info(res);

		log.info("end---------");
		return res;
	}

}
