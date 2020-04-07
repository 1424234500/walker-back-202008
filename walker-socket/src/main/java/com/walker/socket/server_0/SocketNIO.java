package com.walker.socket.server_0;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

import com.walker.common.setting.Setting;
import com.walker.common.util.Tools;

/**
 * 底层 socket 基本实现
 * 负责实现socket通信 的 连接建立 并调用上层server的 事件 send receive
 * 再由上层server决定调用底层的 send receive...
 *
 */

public  class SocketNIO extends SocketFrame<SocketChannel>  {
	static private Charset charset = Charset.forName("UTF-8");

	static ServerSocketChannel serverSocketChannel;
	 
	static String  serverHostName;			//服务器hostname
	static boolean boolIfOn;				//是否开启监听线程
	static String  serverIp;				//服务器ip
	static int	   serverPort;				//服务器port
	static Selector selector;				//选择器
 
	public SocketNIO(){
		serverPort = Setting.get("socket_port_nio", 8091);
	}

	@Override
	protected void startImpl() throws Exception{
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(serverPort));
		
		out("启动服务器成功：服务器信息如下");
		serverIp = InetAddress.getLocalHost().getHostAddress();
		serverHostName = InetAddress.getLocalHost().getHostName();
		out(serverIp, serverPort, serverHostName);
		serverSocketChannel.configureBlocking(false);
		//2.打开选择器
		selector = Selector.open();
		//注册等待事件
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
//		serverSocketChannel.register(selector, SelectionKey.OP_READ);
//		serverSocketChannel.register(selector, SelectionKey.OP_CONNECT);
//		serverSocketChannel.register(selector, SelectionKey.OP_WRITE);

		out("等待客户端.....");
		boolIfOn = true;
		//死循环，这里不会阻塞
		while(boolIfOn) {
			//1.在轮询获取待处理的事件
			int wait = selector.select();
			out("当前等待处理的事件："+wait+"个");
			if(wait == 0){
				//如果没有可处理的事件，则跳过
				continue;
			}
			//获取所有待处理的事件
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = keys.iterator();
			//遍历
			while(iterator.hasNext()) {
				SelectionKey key = (SelectionKey) iterator.next();
				//处理前，关闭选在择器中的事件
				iterator.remove();
				//处理事件
				out("event："+key.toString());
				out("事件Readable："+key.isReadable());
				out("事件Acceptable："+key.isAcceptable());
				process(key);
			}
		}
//		while (boolIfOn) {
//			try{
//				SocketChannel tempSocket = serverSocketChannel.accept();
//				//获取到一个连接客户,放入连接客户集合
//				out("检测到连接:" + tempSocket.toString());
//				//上层负责管理转发调用逻辑
//				onNewConnection(tempSocket);
//				//frame底层负责启动监听任务 并传递给上层处理
//				startRead(tempSocket);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//		}
		out("关闭服务器监听线程");
		
	}
	/**
	 * 根据事件类型，做处理
	 * @param key
	 * @throws IOException
	 */
	public void process(SelectionKey key) throws IOException {
		//连接就绪
		if (key.isAcceptable()) {
			//获取通道
			ServerSocketChannel server = (ServerSocketChannel) key.channel();
			//进入服务端等待
			SocketChannel client = server.accept();
			//非阻塞模式
			client.configureBlocking(false);
			//注册选择器，并设置为读取模式，收到一个连接请求，
			// 然后起一个SocketChannel，并注册到selector上，
			// 之后这个连接的数据，就由这个SocketChannel处理
			client.register(selector, SelectionKey.OP_READ);
			//将此对应的channel设置为准备接受其他客户端请求
			key.interestOps(SelectionKey.OP_ACCEPT);
			client.write(charset.encode("hello"));
			onNewConnection(client);
		}
		//读就绪
		if (key.isReadable()) {
			//返回该SelectionKey对应的 Channel，其中有数据需要读取
			SocketChannel client = (SocketChannel) key.channel();

			try {
				String readLine = readImpl(client);
				if (Tools.notNull(readLine)) {
//					out(readLine);
					onReceive(client, readLine);
				}
				key.interestOps(SelectionKey.OP_READ);
			}catch (Exception e) {
				key.cancel();
				if (key.channel() != null) {
					key.channel().close();
				}
			}

		}
	}
	@Override
	protected void startRead(SocketChannel socket) throws Exception {
		read(socket);
	}

	@Override
	protected String readImpl(SocketChannel socket) throws Exception {
		return SocketUtil.readImpl(socket);
	}

	@Override
	protected void sendImpl(SocketChannel socket, String jsonstr) throws Exception {
		SocketUtil.sendImpl(socket, jsonstr);
	}

	@Override
	protected void stopImpl() {
		// TODO Auto-generated method stub
	}
	
	
}
