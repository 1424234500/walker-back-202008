package com.walker.socket.server_1.plugin;

import com.walker.common.util.Bean;
import com.walker.common.util.TimeUtil;
import com.walker.mode.Key;
import com.walker.mode.Msg;
import com.walker.service.MessageService;
import com.walker.socket.service.redis.MessageServiceImpl;

import java.util.List;

/**
 * 查询服务
 * @author walker
 *
 * @param <T>
 */
public  class OfflinemsgPlugin<T> extends Plugin<T>{
	
	MessageService service = new MessageServiceImpl();
	
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
		List<Msg> msgs = service.findAfter(getNowUser(msg).getId(), before, Integer.valueOf(count));
		msg.setData(msgs);
		publish(msg);

	}
     

}
