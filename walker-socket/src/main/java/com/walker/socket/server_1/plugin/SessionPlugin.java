package com.walker.socket.server_1.plugin;

import com.walker.common.util.Bean;
import com.walker.mode.Msg;
import com.walker.socket.server_1.netty.handler.SessionHandler;

/**
 * 会话列表
 * @author walker
 *
 * @param <T>
 */
public class SessionPlugin<T> extends Plugin<T>{

	SessionPlugin(Bean params) {
		super(params);
	}

	@Override
	public void onData(Msg msg) {
		//参数
		Bean bean = (Bean)msg.getData();
		
		Object res = SessionHandler.sessionService.getSessionList();
		msg.setData(res);
		msg.setStatus(1);
		
		publish(msg.getFrom(), msg);
	}

}
