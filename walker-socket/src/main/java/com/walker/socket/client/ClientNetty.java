package com.walker.socket.client;
 
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import com.walker.common.util.ThreadUtil;
import com.walker.common.util.Tools;
import com.walker.core.exception.ErrorException;
import com.walker.socket.server_0.SocketUtil;
import com.walker.socket.server_1.netty.handler.NettyDecoder;
import com.walker.socket.server_1.netty.handler.NettyEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class ClientNetty extends ClientAdapter {
	EventLoopGroup group;
	Bootstrap b;
	ChannelHandlerContext socket;
	String serverIp = "127.0.0.1";
	int serverPort = 8091;
	private OnSocket onSocket; 
	public ClientNetty(String ip, int port){
		this.serverIp = ip;
		this.serverPort = port;
	}
	
	/**
	 * netty的handler
	 * 负责处理新旧连接
	 * 监控新消息读取
	 * @author ThinkPad
	 *
	 */
	public class HandlerNetty extends ChannelInboundHandlerAdapter {
		 
		
		@Override
		public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
			super.handlerAdded(ctx);
			socket = ctx;
//			out("handlerAdded", ctx);
			if(onSocket != null) {
				onSocket.onConnect(ctx.toString());
			}
		}
	
		@Override
		public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
			super.handlerRemoved(ctx);
//			out("handlerRemoved", ctx);
			if(onSocket != null) {
				onSocket.onDisconnect(ctx.toString());
			}
		}
	
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			if(onSocket != null) {
				onSocket.onRead(ctx.toString(), String.valueOf(msg));
			}
//			out("channelRead", msg);
		}
	
		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) {
			ctx.flush();
//			out("channelReadComplete", ctx);
		}
	
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			// Close the connection when an exception is raised.
			cause.printStackTrace();
//			group.shutdownGracefully();
//			ctx.close();
//			out("exceptionCaught", ctx, cause);
			out("异常", cause);
		}
	
	}






	@Override
	public boolean isStart() {
		return b != null && !group.isShutdown();
	}
	@Override
	public void setOnSocket(OnSocket socket) {
		this.onSocket = socket;
	}
	@Override
	public void start() {
		if(! this.isStart()) {
			out("start Netty", serverIp, serverPort);
			try {
				 // Configure the client.  
		        group = new NioEventLoopGroup();
		        b = new Bootstrap();  
		        b.group(group)
		        .channel(NioSocketChannel.class) // (3)
		        .option(ChannelOption.SO_KEEPALIVE, true) // (4)
		        .option(ChannelOption.TCP_NODELAY, true)  
		        .handler(new ChannelInitializer<SocketChannel>() {  
		            @Override  
		            public void initChannel(SocketChannel ch) throws Exception {  
						ChannelPipeline p = ch.pipeline();
						p.addLast("ping", new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS)); 	//5s心跳包 
	//							p.addLast(new LoggingHandler(LogLevel.INFO));
	//							p.addLast( new ObjectEncoder(),  new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
					    p.addLast(new NettyEncoder(), new NettyDecoder());  
						p.addLast(new HandlerNetty());
		            }  
		        });  
		        // Start the client.  
		        ChannelFuture f = b.connect(serverIp, serverPort).sync();  
		        f.addListener(new GenericFutureListener<Future<Object>>() {
					@Override
					public void operationComplete(Future<Object> arg0) throws Exception {
						out("Netty operationComplete", serverIp, serverPort);
					}
		        });
		        f.get();
		        // Wait until the connection is closed.  
	//	        f.channel().closeFuture().sync();  
		        
		        if(this.onSocket != null) {
					this.onSocket.onConnect(socket.toString());
				}
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
				if(this.socket != null) {
					this.socket.close();
				}
				if(this.group != null) {
					this.group.shutdownGracefully();
				}

				if(this.onSocket != null) {
					this.onSocket.onDisconnect(socket.toString());
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
			socket.writeAndFlush(str);
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
