package com.walker.socket.client;
import com.walker.common.util.ThreadUtil;
import com.walker.core.exception.ErrorException;
import com.walker.socket.server_0.SocketUtil;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class ClientAIO extends ClientAdapter {
	AsynchronousSocketChannel socket ;

	String serverIp = "127.0.0.1";
	int serverPort = 8090;
	//回调
	private OnSocket onSocket;
	public ClientAIO(String ip, int port){
		this.serverIp = ip;
		this.serverPort = port;
	}

	@Override
	public boolean isStart() {
		return socket != null && socket.isOpen();
	}
	@Override
	public void setOnSocket(OnSocket socket) {
		this.onSocket = socket;
	}
	@Override
	public void start() {
		if(! this.isStart()) {
			out("start AIO", serverIp, serverPort);
			try {
				this.onSocket.onConnect(this.socket.toString());
				socket = AsynchronousSocketChannel.open();
				//连接服务端
				socket.connect(new InetSocketAddress(serverIp, serverPort),null,new CompletionHandler<Void,Void>() {
					@Override
					public void completed(Void result, Void attachment) {
						try {
							send("hello");
//							socket.write(ByteBuffer.wrap(("客户端线程：" + Thread.currentThread().getName()+"请求服务端").getBytes())).get();
						} catch (Exception ex) {
							ex.printStackTrace();
							onSocket.onRead(socket.toString(), ex.getMessage());
						}
					}
					@Override
					public void failed(Throwable exc, Void attachment) {
						exc.printStackTrace();
						onSocket.onRead(socket.toString(), exc.getMessage());
					}
				});
				//读取数据
				final ByteBuffer bb = ByteBuffer.allocate(1024);
				socket.read(bb, null, new CompletionHandler<Integer,Object>(){
					@Override
					public void completed(Integer result, Object attachment) {
			//								out("获取反馈结果：" + new String(bb.array()));
						onSocket.onRead(socket.toString(), new String(bb.array()));
					}
					@Override
					public void failed(Throwable exc, Object attachment) {
						exc.printStackTrace();
					}
				} );

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
			out("stop AIO", serverIp, serverPort);
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
//			socket.write(ByteBuffer.wrap(("客户端线程：" + Thread.currentThread().getName()+"请求服务端").getBytes())).get();

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