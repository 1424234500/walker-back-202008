package com.walker.socket.server_1.plugin;

import com.walker.common.util.Bean;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.netty.handler.SessionHandler;

/**
 * 数据监控工具
 * @author walker
 *
 * @param <T>
 */
public class MonitorPlugin<T> extends Plugin<T>{

	MonitorPlugin(Bean params) {
		super(params);
	}

	@Override
	public void onData(Msg msg) {
		Object res = SessionHandler.sessionService.show();
		msg.setData(res);
		
		publish(msg.getFrom(), msg);
	}

}
