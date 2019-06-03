package com.walker.socket.client;
 

/**
 * 回调
 * 
 * 建立连接
 * 发送消息
 * 读取消息
 * 断开连接
 *
 * 其他提示信息
 */
public interface OnSocket{
	public String out(Object...objects);
	public void onRead(String socketId, String str);
	public void onSend(String socketId, String str);
	public void onConnect(String socketId);
	public void onDisconnect(String socketId );
}

