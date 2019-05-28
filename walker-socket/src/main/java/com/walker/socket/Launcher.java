package com.walker.socket;

import com.walker.socket.server_1.SocketNetty;

public class Launcher {

	public static void main(String[] args) throws Exception {

		new SocketNetty().start();
		
	}

}
