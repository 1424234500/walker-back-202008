package com.walker.socket.server_1.plugin.aop;

import com.walker.common.util.Bean;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.SocketException;

public class AuthAop<T> extends Aop<T>{

	AuthAop(Bean params) {
		super(params);
	}

	@Override
	public void doAop(Msg msg) throws SocketException {
		if(msg.getUserFrom().length() == 0) {
			String tip = this.params.get("tip", "") + msg.getData();
			log.warn(tip);
			error(tip);
		}else {
			log.warn("已经登录");
		}
	}

}
