package com.walker.core.service.webservice.jdk7;

import java.util.Map;

import com.walker.common.util.BindingProvider;

public class WebServiceTool {

	/**
	 * webservice超时设置
	 * @param port
	 * @return
	 */
	public static <T> T webserviceConfig(T soap) {
		try {
			Map<String, Object> map = ((BindingProvider)soap).getRequestContext();
			map.put("com.sun.xml.internal.ws.connect.timeout",  2000);
			map.put("com.sun.xml.internal.ws.request.timeout", 5000);
		}catch(Exception e) {
			log.error(e.getMessage(), e);
		}
		return soap;
	}
}
