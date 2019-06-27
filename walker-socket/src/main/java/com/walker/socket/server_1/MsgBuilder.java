package com.walker.socket.server_1;

import org.junit.Test;

import com.walker.common.util.Bean;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.socket.server_1.plugin.Plugin;
import com.walker.socket.server_1.session.Session;

/**
 * 构造各种消息格式
 * @author walker
 *
 */
public class MsgBuilder {
//	16:38:35.main-1.{"DATA":{"pwd":"123456","user":"test"},"TYPE":Plugin.KEY_LOGIN,"TC":1560415115280}
//	16:38:35.main-1.{"DATA":"{TYPE:text,TEXT:test}","TO":"to","TYPE":"message","TC":1560415115610}
//	16:38:35.main-1.{"TYPE":"monitor","TC":1560415115611}
	/**
	 * 	"{type:login,data:{user:username,pwd:123456} }"
	 */
	public static Msg makeLogin(String userId, String before) {
		return new Msg().setType(Plugin.KEY_LOGIN)
				.setData(new Bean().set(Key.ID, userId).set(Key.NAME, userId).set(Key.PWD, "123456").set(Key.BEFORE, before))
				.setTimeClient(System.currentTimeMillis());
	}
	/**
	 * 登录拉去时间前
	 */
	public static Msg makeLogin(String userId, String pwd, String before) {
		return new Msg().setType(Plugin.KEY_LOGIN)
				.setData(new Bean().set(Key.ID, userId).set(Key.NAME, userId).set(Key.PWD, pwd).set(Key.BEFORE, before))
				.setTimeClient(System.currentTimeMillis());
	}
	/**
	 * 	"{type:monitor,data:{} }"
	 */
	public static Msg testMonitor() {
		return new Msg().setType(Plugin.KEY_MONITOR)
				.setTimeClient(System.currentTimeMillis());
	}
	/**
	 * 	"{type:message,to:all_socket/all_user/000,data:{type:txt,body:body} }"
	 */
	public static Msg testMessageTo(String to, String body) {
		return new Msg().setType(Plugin.KEY_MESSAGE)
				.setData(new Bean().set(Key.TYPE, Key.TEXT).set(Key.TEXT, body))
				.setUserTo(to)
				.setTimeClient(System.currentTimeMillis());
	}
	

	public static Msg makeSession( Object data) {
		return new Msg().setType(Plugin.KEY_SESSION)
				.setData(data)
				.setTimeClient(System.currentTimeMillis());
	}
	
	public static Msg makeMessageTo(String to, Object data) {
		return new Msg().setType(Plugin.KEY_MESSAGE)
				.setData(data)
				.setUserTo(to)
				.setTimeClient(System.currentTimeMillis());
	}
	
	public static Msg makeMsg(String plugin, String to, Object data) {
		return new Msg().setType(plugin)
				.setUserTo(to)
				.setData(data)
				.setTimeClient(System.currentTimeMillis());
	}
	public static Msg makeMsgAllSocket(String plugin, Object data) {
		return new Msg().setType(plugin)
				.setUserTo(Key.ALL_SOCKET)
				.setData(data)
				.setTimeClient(System.currentTimeMillis());
	}
	public static Msg makeMsgAllUser(String plugin, Object data) {
		return new Msg().setType(plugin)
				.setUserTo(Key.ALL_USER)
				.setData(data)
				.setTimeClient(System.currentTimeMillis());
	}
	
	
	
	
	public static <T> Object makeException(Session<T> session, Msg msg, Exception e) {
		return new Msg().setType(Plugin.KEY_EXCEPTION).setStatus(1)
				.setInfo(e.toString()).setData(new Bean().set(Key.MSG, msg).set(Key.INFO, Tools.toString(e)));
	}

	
	
	public static <T> Msg makeOnLogin() {
		return new Msg().setType(Plugin.KEY_LOGIN).setStatus(0);
	}
	public static <T> Msg makeOnLoginError(Session<T> session, Bean bean) {
		return new Msg().setType(Plugin.KEY_LOGIN).setStatus(1).setData(bean);
	}
	public static <T> Msg makeOnUnLogin(Session<T> session, Bean bean) {
		return new Msg().setType(Plugin.KEY_LOGIN).setStatus(2).setData(bean.set(Key.SESSION, session));
	}

	@Test
	public void test() {
		Tools.out(makeLogin("test", TimeUtil.getTimeYmdHmss()));
		Tools.out(testMessageTo("to", "test"));
		Tools.out(testMonitor());
		
	}
	
	
}
