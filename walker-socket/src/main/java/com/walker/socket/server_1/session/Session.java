package com.walker.socket.server_1.session;

import org.apache.log4j.Logger;

import com.walker.common.util.Bean;
import com.walker.common.util.TimeUtil;
import com.walker.core.route.SubPub;
import com.walker.core.route.SubPubMgr;
import com.walker.core.route.SubPub.OnSubscribe;
import com.walker.core.route.SubPub.Res;
import com.walker.socket.server_1.Key;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.MsgBuilder;
import com.walker.socket.server_1.plugin.Plugin;
import com.walker.socket.service.MessageService;
import com.walker.socket.service.redis.MessageServiceImpl;

/**
 * 会话 关联socket user
 * 
 * 订阅模式管理分发
 * 
 * 建立连接 订阅socket
 * 		登录成功 订阅user
 * 			订阅到消息 写入socket
 * 		退出登录	取消订阅user
 * 断开连接 取消订阅socket
 * 
 */
public class Session<T> implements OnSubscribe<Msg,Session<T>> {
	private static Logger log = Logger.getLogger(Session.class); 
	
	MessageService messageService = new MessageServiceImpl();
	
	User user = new User();
	/**
	 * socket ip:port
	 */
	String key = "";
	/**
	 * time
	 */
	String time = "";
	
    /**
     * 路由 发布订阅
     */
    SubPub<Msg, Session<T>> sub = SubPubMgr.getSubPub("msg_route", 0);
	
	/**
	 * socket实体以及 key send实现回调
	 */
	Socket<T> socket;	
	
	public Session(Socket<T> socket) {
		setSession(socket);
	}
	public void setSession(Socket<T> socket) {
		this.socket = socket;
		this.key = socket.key();
	}
	public String getKey() {
		return this.key;
	}
	public String getTime() {
		return this.time;
	}
	public Session<T> setTime(String time) {
		this.time = time;
		return this;
	}
	public User getUser() {
		return this.user;
	}
	/**
	 * 判定session是否相同
	 */
	@Override
	public boolean equals(Object obj) {
		@SuppressWarnings("unchecked")
		Session<T> to = (Session<T>) obj;
		return getKey().equals(to.getKey());
		//return super.equals(obj);
	}

	@Override
	public String toString() {
		return "Session[" + getKey() + "." + getUser() + "]";
	}
	
	 
	public Boolean isLogin() {
		return getUser().getId().length() != 0;
	}
	/**
	 * 长连接成功后 订阅socket消息
	 */
	public void onConnect() {
		sub.subscribe(getKey(), this); 	//订阅当前socket
		sub.subscribe("all_socket", this);		//订阅所有socket
//		this.id = getKey();
	}
	public void onUnConnect() {
		sub.unSubscribe(getKey(), this); 	//订阅当前socket
		sub.unSubscribe("all_socket", this);	//订阅所有socket
	}
	
	/**
	 * 登录成功后 订阅用户消息 单聊群聊特殊规则
	 * 注册Rarp ip -> session
	 */
	public void onLogin(Bean bean) {
		this.onUnLogin(bean);
		User user = getUser();
		user.setId(bean.get(Key.ID, ""));
		user.setName(bean.get(Key.NAME, ""));
		user.setPwd(bean.get(Key.PWD, ""));
		this.setTime(TimeUtil.getTimeYmdHms());
		sub.subscribe(user.getId(), this);	//订阅当前登录用户userid
		sub.subscribe("all_user", this);		//订阅所有登录用户
		log.info("login ok " + this.toString() );
		

		bean.set(Key.USER, getUser());
		String beforeStr = bean.get(Key.BEFORE, TimeUtil.getTimeYmdHmss());
//		long before = TimeUtil.format(beforeStr, "yyyy-MM-dd HH:mm:ss:sss").getTime();
//		bean.set(Key.MSG, messageService.finds(user.getId(), before, Integer.MAX_VALUE));
		
	}
	public void onUnLogin(Bean bean) {
		User user = getUser();
		sub.unSubscribe(user.getId(), this);	//订阅当前登录用户userid
		sub.unSubscribe("all_user", this);		//订阅所有登录用户
		user.setId("");
		user.setName("");
		user.setPwd("");
		this.setTime("");
		log.info("unlogin ok " + this.toString() );
	}

	public void send(Object obj) {
		this.socket.send(obj.toString());
	}
	/**
 	 * session负责自己处理业务
	 */
	@Override
	public Res<Session<T>> onSubscribe(Msg msg) {
//		log.debug("onSubscribe " + msg);
		int status = msg.getStatus();
//		msg.setTo(getKey());	//单聊可以替换 但群聊 不能 

		if(msg.getType().equals(Plugin.KEY_LOGIN)) {
			Bean bean = (Bean) msg.getData();
			if(status == 0) {
				this.onLogin(bean);
			}else {
				this.onUnLogin(bean);
			}
		}
		if(msg.getType().equals("monitor")) {	//data格式化不能json
			send(msg.getData());
		}else {
			send(msg);
		}

		//模拟写入socket耗时
//		ThreadUtil.sleep(20);
		
		return new Res<Session<T>>(Type.DONE, this);
	}
	
	
	
}
