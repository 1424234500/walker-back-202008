package com.walker.service.impl;

import com.walker.common.util.Bean;
import com.walker.common.util.LangUtil;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.core.cache.Cache;
import com.walker.core.cache.CacheMgr;
import com.walker.dao.JdbcDao;
import com.walker.dao.JobHisRepository;
import com.walker.dao.LogInfoRepository;
import com.walker.mode.JobHis;
import com.walker.mode.LogInfo;
import com.walker.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Transactional
@Service("logService")
//@Scope("prototype")	//默认单例 dubbo不能单例
public class LogServiceImpl implements LogService {
    @Autowired
    private JdbcDao jdbcDao;

	private Cache<String> cache = CacheMgr.getInstance();

	@Autowired
	LogInfoRepository logInfoRepository;


	@Autowired
	JobHisRepository jobHisRepository;
	/**
	 * 保存jobhis quartz类执行日志
	 * @param jobHis
	 */
	public void saveJobHis(JobHis jobHis){
		jobHisRepository.save(jobHis);
	}

    //info:
    //id,userid,time,url,ip,mac,port,about
	@Override
	public void saveControl(String userid, String url, String ip, String host, int port, String params) {
		List<LogInfo> list = cache.get(CACHE_KEY_CONTROL, new ArrayList<>());
		params = Tools.cutString(params, 180);
		LogInfo line = new LogInfo();
		line.setId(LangUtil.getGenerateId());
		line.setTime(TimeUtil.getTimeYmdHmss());
		line.setUSERID(userid);
		line.setURL(url);
		line.setIP(ip + ":" + port);
		line.setHOST(host);
		line.setABOUT(params);
		list.add(line);
		cache.put(CACHE_KEY_CONTROL, list);
	}

	/**
	 * 切换 线程安全的计数方案
	 */
	@Override
	public void saveStatis(String url, String params, long costtime) {
		url = url.split("\\.")[0]; //url编码
		Bean bean = cache.get(CACHE_KEY, new Bean());
		Bean beanUrl = bean.get(url, new Bean());
		beanUrl.put("URL", url);
		beanUrl.put("COSTTIME", bean.get("COSTTIME", 0L) + costtime);
		beanUrl.put("COUNT", bean.get("COUNT", 0) + 1);

		bean.put(url, beanUrl);
		cache.put(CACHE_KEY, bean);
	}
	@Override
	public void saveStatis() {
		log.info("begin batch save");

		Bean bean = cache.get(CACHE_KEY, new Bean());
		Set<Object> keys = bean.keySet();
		if(keys != null && keys.size() > 0) {
			List<List<Object>> list = new ArrayList<>();
			for (Object key : keys) {
				Bean map = bean.get(key, new Bean());
				List<Object> line = Arrays.asList(map.get("IPPORT", "localhost:8080"), LangUtil.getGenerateId(), map.get("URL"), map.get("COUNT"), TimeUtil.getTimeYmdHmss(), map.get("COSTTIME"));
				list.add(line);
			}
			Integer[] res = jdbcDao.executeSql("insert into W_LOG_TIME"
							+ "(IPPORT, ID, URL, COUNT, TIME, COSTTIME) "
							+ "values"
							+ "(?, ?, ?, ?, ?, ?) "
					, list
			);
			log.info("batch save static " + res);
		}
		cache.remove(CACHE_KEY);


		List<LogInfo> list = cache.get(CACHE_KEY_CONTROL, new ArrayList<>());
		if(list.size() > 0) {
			list = logInfoRepository.saveAll(list);
			cache.remove(CACHE_KEY_CONTROL);
			log.info("batch save Control " + list.size());
		}

	}

}