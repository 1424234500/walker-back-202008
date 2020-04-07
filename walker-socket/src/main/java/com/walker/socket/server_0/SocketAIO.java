package com.walker.socket.server_0;
import com.walker.common.setting.Setting;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.*;

public class SocketAIO  extends SocketFrame<Socket>  {
	AsynchronousServerSocketChannel server;

	static String  serverHostName;			//服务器hostname
	static boolean boolIfOn;				//是否开启监听线程
	static String  serverIp;				//服务器ip
	static int	   serverPort;				//服务器port

	public SocketAIO(){
		serverPort = Setting.get("socket_port_aio", 8083);
	}

	@Override
	protected void startRead(Socket socket) throws Exception {

	}

	@Override
	protected String readImpl(Socket socket) throws Exception {
		return null;
	}

	@Override
	protected void sendImpl(Socket socket, String jsonstr) throws Exception {

	}


	protected void startImpl() throws Exception {
		try {
			//线程缓冲池，为了体现异步
			ExecutorService executorService = newCachedThreadPool();
			//给线程池初始化一个线程
			AsynchronousChannelGroup threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService, 1);

			//Asynchronous异步
			server = AsynchronousServerSocketChannel.open(threadGroup);

			//启动监听
			server.bind(new InetSocketAddress(serverPort));
			out("服务已启动，监听端口" + serverPort);

			final Map<String,Integer> count = new ConcurrentHashMap<String, Integer>();
			count.put("count", 0);
			//开始等待客户端连接
			//实现一个CompletionHandler 的接口，匿名的实现类
			server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
				//缓存区
				final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
				//实现IO操作完成的方法
				@Override
				public void completed(AsynchronousSocketChannel result, Object attachment) {
					count.put("count", count.get("count") + 1);
					out(count.get("count"));
					try {
						//清空缓存标记
						buffer.clear();
						//读取缓存内容
						out(result.toString(), "get", result.read(buffer).get());
						//写模式转换成读模式
						buffer.flip();
						result.write(buffer.put( (result.toString()+"-world").getBytes()));
						buffer.flip();
					} catch (Exception e) {
						out(e.toString());
					} finally {
						try {
							result.close();
							server.accept(null, this);
						} catch (Exception e) {
							out(e.toString());
						}
					}
				}

				//实现IO操作失败的方法
				@Override
				public void failed(Throwable exc, Object attachment) {
					out("IO操作是失败: " + exc);
				}
			});
		} catch (IOException e) {
			out(e);
		}
	}

	@Override
	protected void stopImpl() {
		if(server != null && server.isOpen()){
			try {
				server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}