package com.walker.socket.client;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

import com.walker.common.util.ThreadUtil;
import com.walker.common.util.Tools;
import com.walker.core.exception.ErrorException;
import com.walker.socket.server_0.SocketUtil;

public class ClientNIO extends ClientAdapter {
 
	SocketChannel socket = null;
	String serverIp = "127.0.0.1";
	int serverPort = 8090;
	//回调
	private OnSocket onSocket; 
	public ClientNIO(String ip, int port){
		this.serverIp = ip;
		this.serverPort = port;
	}
	  
	@Override
	public boolean isStart() {
		return socket != null && socket.isConnected() && !socket.isConnected();
	}
	@Override
	public void setOnSocket(OnSocket socket) {
		this.onSocket = socket;
	}
	@Override
	public void start() {
		if(! this.isStart()) {
			out("start IO", serverIp, serverPort);
			try {
				
				socket = SocketChannel.open();
				socket.connect(new InetSocketAddress(serverIp, serverPort));
				if(this.onSocket != null) {
					this.onSocket.onConnect(this.socket.toString());
				}
				
				ThreadUtil.schedule(new Runnable() {
					@Override
					public void run() {
						String line = "";
						try {
							line = SocketUtil.readImpl(socket);
						} catch (Exception e) {
							throw new ErrorException(e);
						}
						if(line.length() > 0 && onSocket != null) {
							onSocket.onRead(socket.toString(), line);
						}
					}
				}, 50, TimeUnit.MILLISECONDS);
				

				
			} catch (Exception e) {
				throw new ErrorException("socket连接失败");
			}
		}else {
			out("have started", serverIp, serverPort);
		}
	}
	@Override
	public void stop() {
		if(this.isStart()) {
			out("stop IO", serverIp, serverPort);
			try {
				if(this.socket != null){
					this.socket.close();
					this.socket = null;
					if(this.onSocket != null) {
						this.onSocket.onDisconnect(socket.toString());
					}
				}
			} catch (Exception e) {
				throw new ErrorException("socket关闭失败");
			}
		}else {
			out("have stoped", serverIp, serverPort);
		}		
	}
	@Override
	public void send(String str) {
		try {
			SocketUtil.sendImpl(socket, str);
			if(this.onSocket != null) {
				this.onSocket.onSend(socket.toString(), str);
			}
		} catch (Exception e) {
			throw new ErrorException(e);
		}
	} 
	@Override
	public String out(Object... objects) {
		if(this.onSocket != null) {
			return this.onSocket.out(objects);
		}else {
			return super.out(objects);
		}
	} 
}
