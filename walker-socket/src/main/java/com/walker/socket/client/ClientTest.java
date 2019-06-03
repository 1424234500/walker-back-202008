package com.walker.socket.client;

import org.apache.log4j.PropertyConfigurator;

import com.walker.common.setting.Setting;
import com.walker.common.util.Context;
import com.walker.common.util.ThreadUtil;
import com.walker.common.util.Tools;

public class ClientTest {
	public static void main(String[] argv) {
		System.setProperty("path_conf", "conf");
		PropertyConfigurator.configure(Context.getPathConf("log4j.properties"));

		
		Client res = new ClientNetty("39.106.111.11", Setting.get("socket_port_netty", 8093));
		res.setOnSocket(new OnSocket() {
			@Override
			public void onSend(String socketId, String str) {
				Tools.out("发送成功", str);
			}
			
			@Override
			public void onRead(String socketId, String str) {
				Tools.out("收到", str);
			}
			
			@Override
			public void onDisconnect(String socketId) {
				Tools.out("断开", socketId);
			}
			
			@Override
			public void onConnect(String socketId) {
				Tools.out("连接", socketId);
			}
			@Override
			public String out(Object... objects) {
				return Tools.out(objects);
			}
		});
		res.start();
		Tools.out("创建新连接", res.toString());
		ThreadUtil.sleep(3000);
		for(int i = 0; i < 5; i++) {
			res.send("" + i);
			ThreadUtil.sleep(1000);
		}
		
	}
}
