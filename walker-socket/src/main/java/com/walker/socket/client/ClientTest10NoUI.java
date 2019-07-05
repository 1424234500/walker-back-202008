package com.walker.socket.client;

import com.walker.common.setting.Setting;
import com.walker.common.util.ThreadUtil;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.mode.Msg;
import com.walker.socket.server_1.plugin.MsgBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
/**
 * 保持每个连接发送频率
 * 逐步扩大连接数,当连接数最大
 * 逐步扩大每个连接发送频率
 * 观察发送qps
 * @author walker
 *
 */
public class ClientTest10NoUI{
	int dtime = 100;	//每次连接 登录间隔延时
	int dtimesend = 100;	//每批次发送间隔
	int dtimesendMax = 1000;
	int flag = -1;
	int dtimesendDeta = 50;	//减少量
	int dtimenewconnection = 20000;	//每批次连接添加间隔 或者发送频率变更间隔
	int num = 10;	//每批次添加连接
	int countFalse = 0; 	//添加连接批次异常次数 5次之后停止 新连接 而开始加大发送消息评频率
	int countFalseMax = 2;
	int maxNum = 101;	//最大连接数161
	List<Client> cc = new ArrayList<Client>();
	AtomicLong count = new AtomicLong(0L);
	ClientTest10NoUI() throws Exception{
		startMakeClient();
		startSend();
		startMakeSend();
		
	}
	public void startSend() {
		//开启循环发送 每个连接 发送  一批次  每个都发送 每秒每个连接发送10次
		new Thread() {
			public void run() {
				while(!Thread.interrupted()) {
					new Thread() {
						public void run() {
//							Tools.out("开始发送批次", cc.size(), dtimesend);
							for(int i = cc.size() - 1; i >= 0; i--) {
								Client client = cc.get(i);
								if(client.isStart()) {
									try {
										sendAllUser(client);
									} catch (Exception e) {
			//							e.printStackTrace();
										Tools.out( "发送失败 移除断开连接", i, client.toString());
										cc.remove(i);
									}
								}else {
									Tools.out( "移除断开连接", i, client.toString());
									cc.remove(i);
								}
							}
			//				Tools.out("开始发送批次" + cc.size(), "完成");
						}
					}.start();
					ThreadUtil.sleep(dtimesend);
				}
			}
		}.start();
	}
	int before = 0;	//上次添加完连接后的数量 若有减少
	public void startMakeClient() {
		//开启增长一个批次的连接
		ThreadUtil.scheduleWithFixedDelay(new Thread() {
			public void run() {
				Tools.out("当前连接数" + cc.size(), countFalse);
//				if(countFalse > countFalseMax)return;

				if(cc.size() < maxNum - num ) {
					Tools.out("创建连接数" + num);
					for(int i = 0; i < num; i++) {
						Client client;
						try {
							client = newConnect();
							cc.add(client);
							ThreadUtil.sleep(dtime);
							login(client);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				int after = cc.size();
				Tools.out("连接数", before, after, "完成", countFalse, "发送间隔", dtimesend);
				if(after < before + num || after >= maxNum) {	
					countFalse ++;
					Tools.out("有部分失败新连接或者到了最大连接数", before, after, countFalse);
				}else if(countFalse > 0){	//连续计数5次标示失败
					countFalse --;
				}
				before = cc.size();

			}
		}, 3000, dtimenewconnection, TimeUnit.MILLISECONDS);
	}
	public void startMakeSend() {
		//开启增长一个批次的连接
		ThreadUtil.scheduleWithFixedDelay(new Thread() {
			public void run() {
				int to = dtimesend + flag * dtimesendDeta;

				if(to <= 10 || to > dtimesendMax) {
					flag = -1 * flag;
				}
				to = dtimesend + flag * dtimesendDeta;
				Tools.out("当前发送间隔",dtimesend,"调整至", to);
				dtimesend = to;
				cc.remove(0);
			}
		}, 3000, dtimenewconnection, TimeUnit.MILLISECONDS);
	}

	private Client newConnect() throws Exception {
//		Client res = new ClientNetty("127.0.0.1", Setting.get("socket_port_netty", 8093));
		Client res = new ClientNetty("39.106.111.11", Setting.get("socket_port_netty", 8093));
		res.setOnSocket(new OnSocket() {
			@Override
			public void onSend(String socketId, String str) {
			}
			
			@Override
			public void onRead(String socketId, String str) {
				Tools.out("收到", str);
			}
			
			@Override
			public void onDisconnect(String socketId) {
				Tools.out("断开", socketId);
			}
			
			@Override
			public void onConnect(String socketId) {
				Tools.out("连接", socketId);
			}
			@Override
			public String out(Object... objects) {
				return Tools.out(objects);
			}
		});
		res.start();
		Tools.out("创建新连接", res.toString());
		return res;
	}
	public void show(Client client) {

	}
	public void login(Client client) throws Exception {
		client.send(MsgBuilder.makeLogin(Tools.getRandomNum(10, 99, 2), TimeUtil.getTimeYmdHmss()).toString());
	}
	public void sendAllUser(Client client) throws Exception {
		count.addAndGet(1L);
		
		Msg msg = MsgBuilder.testMessageTo("all_user", "b"+count);
		client.send(msg.toString());
	}
	
	
	public static void main(String[] args) throws Exception {
		new ClientTest10NoUI();
		while(true) {}
	}

	

}
