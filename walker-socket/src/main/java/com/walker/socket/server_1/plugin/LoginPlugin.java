package com.walker.socket.server_1.plugin;

import com.walker.common.util.Bean;
import com.walker.core.exception.ErrorException;
import com.walker.mode.Key;
import com.walker.mode.Msg;
import com.walker.mode.User;
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

		String id = data.get(Key.ID, "");
		String name = data.get(Key.NAME, "");
		String pwd = data.get(Key.PWD, "");
		log.info("login " + msg.toString());
		
		//以user 为 id去重 校验
		Session<?> session = getNowSession(msg);
		Session<?> sessionId = SessionHandler.sessionService.getSession("", id);
		if(session == null) {
			throw new ErrorException("该用户已掉线", data);
		}else {
			User user = session.getUser();
			//查表? token 验证机制 公钥 私钥
			if(user.getId().length()==0) { 	//初次登录
				publish(msg.getFrom(), MsgBuilder.makeOnLogin().setData(data).setInfo("初次登录"));
			}  else {
				if(sessionId != null) {
					if(session.equals(sessionId)) {	//重复登录
						publish(msg.getFrom(), MsgBuilder.makeOnLogin().setData(data).setInfo("重复登录"));
					}else {
						publish(msg.getFrom(), MsgBuilder.makeOnLoginError(session, data).setInfo("该用户id已被登录"+id));
					}
				}else {//换号登录
					if((id.length() > 0 && pwd.length() >= 0)){	
						publish(msg.getFrom(), MsgBuilder.makeOnLogin().setData(data).setInfo("换号登录"));
					}else {
						publish(msg.getFrom(), MsgBuilder.makeOnLoginError(session, data).setInfo("参数不正确"));
					}
				}
			}
		}
		
//		publish(msg.getFrom(), msg);
		
	}

}
