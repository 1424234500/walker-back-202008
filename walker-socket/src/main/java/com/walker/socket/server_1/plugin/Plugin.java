package com.walker.socket.server_1.plugin;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.walker.common.util.Bean;
import com.walker.common.util.LangUtil;
import com.walker.common.util.Tools;
import com.walker.core.route.SubPub;
import com.walker.core.route.SubPubMgr;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.session.Session;

/**
 * 业务分类处理插件
 *
 */
public abstract class Plugin<T> {
	protected Logger log = Logger.getLogger(Plugin.class); 

	/**
     * 路由 发布订阅
     * pub发布 key socket定向消息 或者 发布user频道消息
     * 不需要session
     */
    SubPub<Msg, Session<T>> pub = SubPubMgr.getSubPub("msg_route", 0);
    
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
		String[] tos = msg.getUserTo();
		//单端在线 记录未命中目标 向上传递
		List<String> offUsers = new ArrayList<String>();
		for(String to : tos) {
			msg.setUserTo(to);

			Msg msgNew = LangUtil.cloneObject(msg);
			msgNew.setUserTo(to);
			List<Session<T>> onUsers = publish(to, msg);
			log.info("publish " + to + " on " + onUsers.size() );
			Tools.formatOut(onUsers);
			
			if(onUsers.size() <= 0) {
				offUsers.add(to);
			}
		}
		//存在未命中的目标 则向上传递
		if(offUsers.size() > 0) {
			Tools.out("未命中目标 向上传递", offUsers, msg);
		}
	
	}
	
	public List<Session<T>> publish(String channel, Msg msg) {
		return pub.publish(channel, msg);
	}


	abstract void onData(Msg bean);
	
}
