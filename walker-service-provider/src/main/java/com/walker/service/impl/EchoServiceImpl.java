package com.walker.service.impl;

import com.walker.service.EchoService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;


//@Component("echoService")
@org.springframework.stereotype.Service
@com.alibaba.dubbo.config.annotation.Service
//@Transactional
//@Scope("prototype")
public class EchoServiceImpl implements EchoService,Serializable {
	private static final long serialVersionUID = 8304941820771045214L;

	/**
	 * 回响
	 *
	 * @param hello
	 * @return
	 */
	@Override
	public String echo(String hello) {
		return "echo:" + String.valueOf(hello);
	}
}