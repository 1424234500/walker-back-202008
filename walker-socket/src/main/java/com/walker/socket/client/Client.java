package com.walker.socket.client;


/**
 * 一个socket客户端抽象
 * 
 * 主动操作 控制
 * 
 * 设置回调
 * 
 */

public interface Client{
	/**
	 * 输出
	 */
	public String out(Object...objects);

	/**
	 * socket读取消息回调
	 * 
	 */
	public void setOnSocket(OnSocket socket);

	
	/**
	 * 是否启动
	 * @return
	 */
	public boolean isStart();
	/**
	 * 启动 
	 * 仅在未启动时启动
	 * 
	 * @throws 异常
	 */
	public void start();
	/**
	 * 关闭 
	 * 仅在启动时关闭
	 * 
	 * @throws 异常
	 */
	public void stop();


	/**
	 * socket 写入字符串 
	 * 
	 * @throws 异常
	 */
	public void send(String str);
	
} 

