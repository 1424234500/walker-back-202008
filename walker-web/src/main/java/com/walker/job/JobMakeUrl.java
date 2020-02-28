package com.walker.job;

import com.walker.common.util.Bean;
import com.walker.quartz.TaskJob;
import com.walker.service.MakeTestService;
import com.walker.service.SyncService;
import com.walker.util.SpringContextUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 造数基础数据
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobMakeUrl extends TaskJob {
	private Logger log = LoggerFactory.getLogger(getClass());

//	@Autowired
//	@Qualifier("syncService")
//	private SyncService syncService;
	//此处不能自动注入? 扫描注入包配置问题
	MakeTestService makeTestService = SpringContextUtil.getBean("makeTestService");


	@Override
	public String make() {
		log.info("begin---------");

		Object bean = makeTestService.makeUrlTestRandomThread(10 * 60 * 1000, 100, 1);
		String res = String.valueOf(bean);
		log.info(res);

		log.info("end---------");
		return res;
	}

}
