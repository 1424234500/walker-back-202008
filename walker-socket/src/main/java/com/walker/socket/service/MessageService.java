package com.walker.socket.service;

import java.util.List;

import com.walker.socket.server_1.Msg;

public interface MessageService {
	/**
	 * 存储消息 先存redis 再存mysql 分表
	 * 先存消息实体 再存用户关联
	 * @param toId
	 * @param msg
	 * @return
	 */
	Long save(String[] toIds, Msg msg);
	
	/**
	 * 查消息实体
	 * @param msgId
	 * @return
	 */
	Msg findMsg(String msgId);
	/**
	 * 查询用户a和用户b聊天的 时间节点之前的数据 mysql 分表
	 * @param userId
	 * @param before
	 * @param count
	 * @return
	 */
	List<Msg> findBefore(String userId, String toId, String before, int count);
	/**
	 * 查询时间节点之后的数据 离线后搜到的消息
	 * @param userId
	 * @param after
	 * @param count
	 * @return
	 */
	List<Msg> findAfter(String userId, String after, int count);

}
