package com.walker.socket;

import org.apache.log4j.PropertyConfigurator;

import com.walker.common.util.Context;
import com.walker.socket.server_1.SocketNetty;

public class Launcher {

	public static void main(String[] args) throws Exception {
//		web项目需要配置于WEB-INF/classes 	spring.xml	 web.xml寻址classpath:   ? 
		System.setProperty("path_conf", "conf");
		PropertyConfigurator.configure(Context.getPathConf("log4j.properties"));

		new SocketNetty().start();
	}

}
