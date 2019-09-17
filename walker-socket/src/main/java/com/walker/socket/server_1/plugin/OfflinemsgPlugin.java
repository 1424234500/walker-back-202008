package com.walker.socket.server_1.plugin;

import com.walker.common.util.Bean;
import com.walker.common.util.TimeUtil;
import com.walker.dubbo.DubboMgr;
import com.walker.mode.Key;
import com.walker.mode.Msg;
import com.walker.service.MessageService;

import java.util.List;

/**
 * 查询服务
 * @author walker
 *
 * @param <T>
 */
public  class OfflinemsgPlugin<T> extends Plugin<T>{
	
//	MessageService messageService = new MessageServiceImpl();
	MessageService messageService = DubboMgr.getService("messageServiceSharding");


	OfflinemsgPlugin(Bean params) {
		super(params);
	}
	/**
	 * {type:session,sto:,sfrom:128.2.3.1\:9080,to:123,from:222,data:{before,count} }	
	 */
	public void onData(Msg msg) {
		Bean data = msg.getData();
		String before = data.get(Key.BEFORE, TimeUtil.getTimeYmdHmss());
		String count = data.get(Key.COUNT, "200");
		List<Msg> msgs = messageService.findAfter(getNowUser(msg).getId(), before, Integer.valueOf(count));
		msg.setData(msgs);
		publish(msg);

	}
     

}
