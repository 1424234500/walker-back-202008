package com.walker.socket;

import org.apache.log4j.PropertyConfigurator;

import com.walker.common.setting.Setting;
import com.walker.common.util.Context;
import com.walker.core.cache.CacheMgr;
import com.walker.socket.client.ClientNetty;
import com.walker.socket.client.ClientUI;

public class ClientLauncher {

	public static void main(String[] args) throws Exception {
		System.setProperty("path_conf", "conf");
		PropertyConfigurator.configure(Context.getPathConf("log4j.properties"));

//		new ClientUI(new ClientIO("127.0.0.1", 8090), "io-io");
//		new ClientUI(new ClientIO("127.0.0.1", 8091), "io-nio");
//		new ClientUI(new ClientNIO("127.0.0.1", 8090), "nio-io");
//		new ClientUI(new ClientNIO("127.0.0.1", 8091), "nio-nio-server");
//		new ClientUI(new ClientNIO("127.0.0.1", 8091), "nio-nio-client");
//		new ClientUI(new ClientNIO("127.0.0.1", 8092), "nio-netty-client");
		try {
			new ClientUI(new ClientNetty("39.106.111.11", Setting.get("socket_port_netty", 8093)), "netty-netty-client-39");
		}catch (Exception e){
			e.printStackTrace();
		}
		try {
			new ClientUI(new ClientNetty("127.0.0.1",	 Setting.get("socket_port_netty", 8093)), "netty-netty-client-127");
		}catch (Exception e){
			e.printStackTrace();
		}
		while(true) {}
	}

}
