package com.walker.job;

import com.alibaba.fastjson.JSON;
import com.walker.common.util.Bean;
import com.walker.common.util.HttpBuilder;
import com.walker.config.MakeConfig;
import com.walker.quartz.TaskJob;
import com.walker.mode.Dept;
import com.walker.service.DeptService;
import com.walker.service.InitService;
import com.walker.util.SpringContextUtil;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * 同步 地域信息
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobUpdateArea extends TaskJob {
	private Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	InitService initService;

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

		initService.updateArea();

		log.info("end---------");

	}
}
