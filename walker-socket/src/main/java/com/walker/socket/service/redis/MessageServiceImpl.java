package com.walker.socket.service.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.walker.common.util.Bean;
import com.walker.common.util.Tools;
import com.walker.core.database.Redis;
import com.walker.core.database.Redis.Fun;
import com.walker.socket.server_1.Key;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.MsgBuilder;
import com.walker.socket.server_1.plugin.Plugin;
import com.walker.socket.server_1.session.Session;
import com.walker.socket.server_1.session.User;
import com.walker.socket.service.MessageService;

import redis.clients.jedis.Jedis;

/**
 * 离线消息积压队列
 * 上线后 先拉离线 清空 再接收新消息
 * @author walker
 *
 */
public class MessageServiceImpl implements MessageService{
	private static Logger log = Logger.getLogger(MessageServiceImpl.class); 

	@Override
	public Long save(User user, Msg msg) {
		return Redis.doJedis(new Fun<Long>() {
			@Override
			public Long make(Jedis jedis) {
				long score = System.currentTimeMillis();
				String key = Key.getKeyOffline(user.getId());
				jedis.zadd(key, score, msg.toString());
				log.info(key + " save " + score);
				if(jedis.zcard(key) > 500) {
					long res = jedis.zremrangeByRank(key, 0, 0);
					log.info(key + " rem " + res);
				}
				return score;
			}
		});
	}

	@Override
	public Msg find(User user, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Msg> finds(User user, long before, int count) {
		return Redis.doJedis(new Fun<List<Msg>>() {
			@Override
			public List<Msg> make(Jedis jedis) {
				List<Msg> list = new ArrayList<Msg>();
				String key = Key.getKeyOffline(user.getId());
				Set<String> set = jedis.zrangeByScore(key, before+1, Integer.MAX_VALUE, 0, count);
				log.info(key + " finds " + set.size());
				for(String str : set) {
					list.add(new Msg(str));
				}
				return list;
			}
		});
	}
	
	
	

	@Test
	public void test() {
		MessageService service = new MessageServiceImpl();
		User user = new User();
		user.setId("000");
		List<Long> scores = new ArrayList<Long>();
		for(int i = 0; i < 10; i++) {
			Msg msg = new MsgBuilder().makeMsg(Plugin.KEY_MESSAGE, "00" + i, new Bean().set("count", i));
			Long score = service.save(user, msg);
			Tools.out("", score);
			scores.add(score);
		}
		for(int i = 0; i < 3; i++) {
			List<Msg> list = service.finds(user, scores.get(i), 5);
			Tools.formatOut(list);
		}
		
	}


}
