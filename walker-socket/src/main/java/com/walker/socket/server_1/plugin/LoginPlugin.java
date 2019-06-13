package com.walker.socket.server_1.plugin;

import com.walker.common.util.Bean;
import com.walker.socket.server_1.Key;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.MsgBuilder;
import com.walker.socket.server_1.netty.handler.SessionHandler;
import com.walker.socket.server_1.session.Session;

public class LoginPlugin<T> extends Plugin<T>{

	LoginPlugin(Bean params) {
		super(params);
	}

	/**
	 * {type:login,sto:,sfrom:128.2.3.1\:9080,from:,to:,data:{user:123,pwd:123456} }	
	 */
	@Override
	public void onData(Msg msg) {
		
		Bean data = (Bean) msg.getData();
		String user = data.get(Key.USER, "");
		String pwd = data.get(Key.PWD, "");
		log.info(user);
		log.info(pwd);
		
		//以user 为 id去重 校验
		Session<?> session = SessionHandler.sessionService.isExists(null, user);
		if(session == null) {
			publish(msg.getFrom(), MsgBuilder.makeOnLogin().setData(data));
		}else {
			publish(msg.getFrom(), MsgBuilder.makeOnLoginError(session, data).setInfo("id重复"));
		}
		
//		publish(msg.getFrom(), msg);
		
	}

}
