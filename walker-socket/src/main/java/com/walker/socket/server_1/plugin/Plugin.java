package com.walker.socket.server_1.plugin;

import com.walker.common.util.Bean;
import com.walker.common.util.LangUtil;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.core.route.SubPub;
import com.walker.core.route.SubPubMgr;
import com.walker.dubbo.DubboMgr;
import com.walker.mode.Key;
import com.walker.mode.Msg;
import com.walker.mode.PushModel;
import com.walker.mode.UserSocket;
import com.walker.service.MessageService;
import com.walker.service.PushAgentService;
import com.walker.socket.server_1.netty.handler.SessionHandler;
import com.walker.socket.server_1.session.Session;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务分类处理插件
 *
 */
public abstract class Plugin<T> {
	protected Logger log = Logger.getLogger(Plugin.class); 

	final public static String KEY_LOGIN = "login";
	final public static String KEY_SERVICE= "messageService";
	final public static String KEY_MESSAGE = "message";
	final public static String KEY_OFFLINEMSG= "offlinemsg";

	final public static String KEY_ECHO = "echo";
	final public static String KEY_MONITOR = "monitor";
	final public static String KEY_SESSION = "session";
	final public static String KEY_EXCEPTION = "exception";

	
	
	
	/**
     * 路由 发布订阅
     * pub发布 key socket定向消息 或者 发布user频道消息
     * 不需要session
     */
    private SubPub<Msg, Session<T>> pub = SubPubMgr.getSubPub("msg_route", 0);

    private PushAgentService pushAgentService = DubboMgr.getService("pushAgentService");
	;

	Bean params;
	Plugin(Bean params){
		this.params = params;
	}


	/**
	 * 目标 多用户 逻辑传递  发一个少一个
	 * 如何实现多用户连接同一台socket 
	 * 如何实现同ip连接同一台socket
	 * 对于单聊
	 * 		目标用户只有单端 ! 
	 * 
	 * 对于群聊
	 * 		成员列表取出 替换为单聊
	 * 
	 * 向上传递 || 定向传递
	 *  ip路由为什么是向上传递 因为没有一个注册中心 所有局域网向中心注册? 模拟路由ip向上传递
	 * 	Redis作为路由器
	 * 
树形路由节点广播模式 
以ip段位分区 0,256  0,4
路由表 本地广播 向上传递

                                                           lv0
       
                            lv1.0                                                       lv1.1
        
    lv1.0.0[0,3]   lv1.0.1[4,7]     lv1.0.3[8,11]   lv1.0.4[12,15]      lv1.1.0[16,19]       lv1.1.1[20,23]
	 * 
	 * 
	 */
	public void publish(Msg msg) {
		Msg msgc = LangUtil.cloneObject(msg);

		String[] tos = msgc.getUserTo();
		String tostr = msgc.getUserToStr();

		if(msg.getType().equals(Plugin.KEY_MESSAGE)) {
			Bean bean = (Bean) msg.getData();
			String text = bean.get(Key.TEXT, "");
//			{type:message,data:{to:123,from:222,type:txt,body:hello} }
//		推送
			PushModel pushModel = new PushModel()
					.setUSER_ID(tostr)
					.setTITLE(msg.getUserFrom().getName() + "")
					.setCONTENT(text)
					.setEXT(bean.toString())
					.setS_MTIME(TimeUtil.getTimeYmdHms())
					;

			try {
				pushAgentService.push(pushModel);
			}catch (Exception e){
//				pushAgentService = null;
				log.error(e.toString(), e);
			}
		}


		//单端在线 记录未命中目标 向上传递
		List<String> offUsers = new ArrayList<String>();
		for(String to : tos) {
			Msg msgNew = LangUtil.cloneObject(msgc);
			msgNew.setUserTo(to);
			
			List<Session<T>> onUsers = publish(to, msgNew);
			log.info("publish " + to + " on " + onUsers.size() );
			log.info("订阅列表");
			Tools.formatOut(onUsers);
			
			if(onUsers.size() <= 0) {
				offUsers.add(to);
			}
		}
		//存在未命中的目标 则向上传递
		if(offUsers.size() > 0) {
			Tools.out("未命中目标 向上传递", offUsers, msgc);
		}
	
	}
	
	public List<Session<T>> publish(String channel, Msg msg) {
		return pub.publish(channel, msg);
	}
	public Session<?> getNowSession(Msg msg){
		return SessionHandler.sessionService.getSession(msg.getFrom(), "");
	}
	public UserSocket getNowUser(Msg msg){
		return getNowSession(msg).getUserSocket();
	}
	abstract void onData(Msg bean);
	
}
