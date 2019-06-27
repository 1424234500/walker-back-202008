package com.walker.socket.service;

import java.util.List;

import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.session.User;

public interface MessageService {
	
	Long save(User user, Msg msg);
	
	Msg find(User user, String id);
	
	List<Msg> finds(User user, long before, int count);

}
