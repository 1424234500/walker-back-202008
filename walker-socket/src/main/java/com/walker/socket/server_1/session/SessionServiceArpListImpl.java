package com.walker.socket.server_1.session;

import com.walker.common.setting.Setting;
import com.walker.common.util.Bean;
import com.walker.common.util.Tools;
import com.walker.core.aop.Fun;
import com.walker.core.pipe.Pipe;
import com.walker.core.pipe.PipeMgr;
import com.walker.core.pipe.PipeMgr.Type;
import com.walker.mode.Key;
import com.walker.mode.Msg;
import com.walker.socket.server_1.SocketException;
import com.walker.socket.server_1.plugin.MsgBuilder;
import com.walker.socket.server_1.plugin.PluginMgr;
import com.walker.socket.server_1.plugin.aop.ModelCount;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用戶连接管理
 * 策略：
 * key相同的session 是否需要覆盖socket对象 ？
 * 是否存在key相同 而 socket对不上 已销毁？
 * 
 * 启动pipe队列消费者线程
 * 		消费（plugin aop处理）
 * 			拦截
 * 			存储
 * 			发送
 * 			计数
 * 
 * 读取到socket消息 放入pipe队列
 * 
 * 
 */
public class SessionServiceArpListImpl<T> implements SessionService<T> {
	private static Logger log = Logger.getLogger(SessionServiceArpListImpl.class); 
    

	/**
	 * 会话列表 Arp 	mac:<mac,ip>
	 * 可通过key session.send定向发送消息
	 * socket : session
	 */
    public Map<String, Session<T>> index = new ConcurrentHashMap<>();
    
    /**
     * 业务处理队列 
     * 线程内部使用对象 避免再编码解码json
     * 多进程可切换使用redis 编码解码string
     */
//    private Pipe<Msg0> pipe = PipeMgr.getPipe(Type.PIPE, "queue-msg");
    private Pipe<String> pipe = PipeMgr.getPipe(Type.REDIS, "stat:queue-msg");
    
    public String show() {
    	String res = "\n------------show session - -------\n";
    	int i = 0;
    	for(Session<T> item : index.values()) {
    		res += i++ + "\t " + item.toString() + "\n";
    	}
    	res += "------------show session - -------\n";
    	return res;
    }
    
    @SuppressWarnings("unchecked")
	public SessionServiceArpListImpl(){
    	PluginMgr.getInstance();//初始化任务
    	pipe.startConsumer(Setting.get("netty_thread_consumer", 1), new Fun<String>() {
			public Object make(String msg1) {
				Msg msg = new Msg(msg1);
				ModelCount.getInstance().onWait(msg);
//				if(1==1)
//				return 0;
				Session<T> session = index.get(msg.getFrom()); //根据socket key找到session
				if(session != null) {
					NDC.push(session.toString());
					try {
						PluginMgr.getInstance().doMsg(msg);
					}catch(Exception e) {
						log.error("plugin exception", e);
						//插件处理异常 反馈异常
						session.send(MsgBuilder.makeException(session, msg, e));
					}
					NDC.pop();
				}else {
					log.error("该用户已不存在 " + msg.toString());
				}
//				ThreadUtil.sleep(50);
				ModelCount.getInstance().onDone(msg);

				return true;
			}
    	});
    	
    }
	@Override
	public void sessionAdd(Socket<T> socket) {
		String key = socket.key();
		Session<T> session = index.get(key);
		if(session != null) {
			log.error("add userSocket have exist?" + key + " " + session);
		}else {
			session = new Session<T>(socket);
			session.onConnect();
			index.put(key, session);
			log.debug("add userSocket " + session);
		}
	}
	@Override
	public void sessionRemove(Socket<T> socket) {
		String key = socket.key();
		Session<T> session = index.get(key);
		if(session != null) {
			session.onUnConnect();
			index.remove(key);
			log.debug("add userSocket " + session);
		}else {
			log.error("remove no userSocket " + key + " " + index.get(key));
		}		
	}
	@Override
	public void sessionOnRead(Socket<T> socket, Object obj) {
		String key = socket.key();
		Session<T> session = index.get(key);
		if(session != null) {
			Msg msg = new Msg(obj.toString());	//集成发送者的信息  禁止冒名顶替
			//设置来源socket key session<T>
			msg.setFrom(session.getKey());
			//设置userFrom当前用户 若消息包含了from 则不设置 允许顶替发消息
			msg.setUserFrom(session.getUserSocket());
			//默认发给自己
			if(msg.getTo().length() == 0) {
				msg.setTo(session.getUserSocket().getId());
			}

			msg.setWaitSize(pipe.size());
//			pipe.set(msg);
			ModelCount.getInstance().onNet(msg);
			pipe.put(msg.toString());
			
		}else {//异常请求
			log.error("receive msg from no userSocket ? " + socket);
			socket.send("receive msg from no userSocket ? " + socket);
		}
	}

	@Override
	public List<Bean> getSessionList() {
		List<Bean> res = new ArrayList<Bean>();
		for(String key : index.keySet()) {
			Session<T> session = index.get(key);
			res.add(new Bean().set(Key.USER, session.getUserSocket()).set(Key.KEY, session.getKey()).set(Key.TIME, session.getTime()));
		}
		return res;
	}
	@Override
	public Session<T> getSession(String socketKey, String userId) {
		Session<T> res = null;
		if(socketKey.length() > 0) {
			res = index.get(socketKey);
		}else if(userId.length() > 0) {
			for(String key : index.keySet()) {
				Session<T> session = index.get(key);
				if(session.getUserSocket().getId().equals(userId)) {
					res = session;
					break;
				}
			}
		}
		
		return res;
	}
    
    public static void main(String[] argv) throws SocketException {
    	String msg1 = "{\"time_client\":1560161804160,\"data\":{},\"sfrom\":\"223.104.212.51:36052\",\"wait_size\":0,\"from\":\"\",\"to\":\"\",\"time_reveive\":1560161804162,\"type\":\"session\"}";
    	Msg msg = new Msg(msg1);
		Tools.out(msg);

		ModelCount.getInstance().onWait(msg);
		Tools.out(msg);

		PluginMgr.getInstance().doMsg(msg);
		Tools.out(msg);

		ModelCount.getInstance().onDone(msg);

		msg.setData(msg);
		
		Tools.out(msg);
		
		
    }
} 
