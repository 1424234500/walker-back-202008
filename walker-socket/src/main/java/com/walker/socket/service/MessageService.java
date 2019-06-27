package com.walker.socket.service;

import java.util.List;

import com.walker.socket.server_1.Msg;

public interface MessageService {
	
	Long save(String toId, Msg msg);
	
	
	List<Msg> finds(String userId, String before, int count);

}
