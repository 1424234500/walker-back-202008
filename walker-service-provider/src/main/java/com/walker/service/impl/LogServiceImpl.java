package com.walker.service.impl;

import com.walker.common.util.Bean;
import com.walker.common.util.LangUtil;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.core.cache.Cache;
import com.walker.core.cache.CacheMgr;
import com.walker.dao.JdbcDao;
import com.walker.dao.KafkaDao;
import com.walker.service.LogService;
import com.walker.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Set;

@Transactional
@Service("logService")
//@Scope("prototype")	//默认单例 dubbo不能单例
public class LogServiceImpl implements LogService {
    @Autowired
    private JdbcDao jdbcDao;

	private Cache<String> cache = CacheMgr.getInstance();


    //info:
    //id,userid,time,url,ip,mac,port,about
	@Override
	public void saveControl(String userid, String url, String ip, String host, int port, String params) {
		int res = 0;
		params = Tools.cutString(params, 180);
		res = jdbcDao.executeSql("insert into LOG_INFO"
				+ "(ID,TIME,USERID,URL,IP,MAC,PORT,ABOUT) "
				+ "values"
				+ "(?,?,?,?,?,?,?,?) "
				,LangUtil.getGenerateId(),TimeUtil.getTimeYmdHmss(),userid,url,ip,host,port,params
			);
	}


	/**
	 * 切换 线程安全的计数方案
	 */
	@Override
	public void saveStatis(String url, String params, long costtime) {
		url = url.split("\\.")[0]; //url编码
		Bean bean = cache.get(CACHE_KEY);
		if(bean != null){
		}else{
			bean = new Bean();
		}
		Bean beanUrl = bean.get(url, new Bean());
		beanUrl.put("URL", url);
		beanUrl.put("COSTTIME", bean.get("COSTTIME", 0L) + costtime);
		beanUrl.put("COUNT", bean.get("COUNT", 0) + 1);

		bean.put(url, beanUrl);
		cache.put(CACHE_KEY, bean);
//		saveStatis();
	}
	@Override
	public void saveStatis() {
		Bean bean = cache.get(CACHE_KEY, new Bean());
		cache.remove(CACHE_KEY);

//		Redis redis = Redis.getInstance();
		//redis.show();
		Set<Object> keys = bean.keySet();
		if(keys != null)
			for(Object key : keys){
				if(bean.containsKey(key)){
					Bean map = bean.get(key, new Bean());
					int res = jdbcDao.executeSql("insert into W_LOG_TIME"
							+ "(IPPORT, ID, URL, COUNT, TIME, COSTTIME) "
							+ "values"
							+ "(?, ?, ?, ?, ?, ?) "
							, "localhost:8080", LangUtil.getGenerateId(), map.get("URL"), map.get("COUNT"), TimeUtil.getTimeYmdHmss(), map.get("COSTTIME")
						); 
				}
			}
//		redis.clearKeys();
		//redis.show();
	}

}