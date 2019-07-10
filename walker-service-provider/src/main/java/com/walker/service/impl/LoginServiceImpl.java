package com.walker.service.impl;

import com.walker.common.util.Bean;
import com.walker.common.util.MD5;
import com.walker.core.cache.Cache;
import com.walker.core.cache.CacheMgr;
import com.walker.event.Context;
import com.walker.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Service("loginService")
public class LoginServiceImpl implements LoginService,Serializable {
	private static final long serialVersionUID = 8304941820771045214L;

	private Cache<String> cache = CacheMgr.getInstance();

	@Override
	public Boolean login() {
		return saveLogin("TEST", "");
	}

	@Override
	public Boolean saveLogin(String id, String pwd) {
		String token = MD5.makeKey(id, System.currentTimeMillis());
		Map map = cache.get(CACHE_KEY, new LinkedHashMap<String, Object>());
		Bean bean = new Bean().put("TOKEN", token).put("ID", id).put("TIME", System.currentTimeMillis()).put("EXPIRE", 60L * 1000);
		map.put(token, bean);
		cache.put(CACHE_KEY, map);	
		Context.getRequest().getSession().setAttribute("TOKEN", token);
		return true;
	}
    



}