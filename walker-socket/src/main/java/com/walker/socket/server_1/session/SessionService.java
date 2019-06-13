package com.walker.socket.server_1.session;

import java.util.List;

import com.walker.common.util.Bean;

/**
 * 会话管理
 * 
 * 列表展示
 * 会话事件：
 * 	添加
 * 	移除
 * 	读取
 * 
 */
public interface SessionService<T> {
	/**
	 * 显示会话信息
	 */
	String show();
	/**
	 * 在线列表 ID KEY TIME
	 */
	List<Bean> getSessionList();
	/**
	 * 是否已存在 登录校验
	 */
	Session<T> isExists(Socket<T> socket, String id);
	
	/**
	 * 添加连接
	 * @param socket
	 */
	void sessionAdd(Socket<T> socket);
	/**
	 * 移除连接
	 * @param socket
	 */
	void sessionRemove(Socket<T> socket);
	/**
	 * 读取到消息
	 * @param socket
	 * @param msg
	 */
	void sessionOnRead(Socket<T> socket, Object msg);

}

