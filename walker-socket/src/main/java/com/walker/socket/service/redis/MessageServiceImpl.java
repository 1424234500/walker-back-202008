package com.walker.socket.service.redis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.walker.common.util.Bean;
import com.walker.common.util.LangUtil;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Tools;
import com.walker.core.database.BaseDao;
import com.walker.core.database.Dao;
import com.walker.core.database.Redis;
import com.walker.core.database.Redis.Fun;
import com.walker.core.database.SqlUtil;
import com.walker.core.mode.Watch;
import com.walker.socket.server_1.Key;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.MsgBuilder;
import com.walker.socket.server_1.plugin.Plugin;
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
	BaseDao dao = new Dao();
	final static String TABLE_MSG= "W_MSG";
	final static String TABLE_MSG_USER = "W_MSG_USER";
	
	final static String TABLE_MSG_ = TABLE_MSG + "_";
	final static String TABLE_MSG_USER_ = TABLE_MSG_USER + "_";
	final static int COUNT_MSG = 2;
	final static int COUNT_MSG_USER = 4;
	
	/**
	 * 消息每张分表数量
	 */
	public List<Integer> sizeMsg() {
		List<Integer> res = new ArrayList<>();
		for(int i = 0; i < COUNT_MSG; i++) {
			res.add(dao.count("select * from " + TABLE_MSG_ + i));
		}
		log.info(res);
		return res;
	}
	/**
	 * 消息关联用户每张分表数量
	 */
	public List<Integer> sizeMsgUser(){
		List<Integer> res = new ArrayList<>();
		for(int i = 0; i < COUNT_MSG_USER; i++) {
			res.add(dao.count("select * from " + TABLE_MSG_USER_ + i));
		}
		log.info(res);
		return res;
	}
	
	@Override
	public Long save(String[] toIds, Msg msg) {
		try {
			//insert into W_MSG_0 values()
			//insert into W_MSG_1 values()
			//保存消息实体
			Bean data = msg.getData();
			String msgId = data.get(Key.ID, LangUtil.getGenerateId());
			dao.executeSql("insert into " + TABLE_MSG_ + SqlUtil.makeTableCount(COUNT_MSG,msgId) + " values(?,?)",  msgId, msg.toString() );
			Bean dbLines = new Bean();
			String fromId = msg.getUserFrom().getId();
			for(String toId : toIds) {
				String id = SqlUtil.makeTableKey(fromId, toId);
				String tableName = TABLE_MSG_USER_ + SqlUtil.makeTableCount(COUNT_MSG_USER, id);
				
				List<List<Object>> dbLine = null;
				List<Object> line = new ArrayList<>();
				if(dbLines.containsKey(tableName)) {
					dbLine = (List<List<Object>>) dbLines.get(tableName);
				}else {
					dbLine = new ArrayList<>();
					dbLines.put(tableName, dbLine);
				}
				line.add(id);
				line.add(fromId);
				line.add(toId);
				line.add(msgId);
				line.add(msg.getTimeDo());
				dbLine.add(line);
			}
			for(Entry<?, ?> entry : dbLines.entrySet()) {
				String db = String.valueOf(entry.getKey());
				List<List<Object>> lines = (List<List<Object>>) entry.getValue();
				if(lines.size() > 0) {
					dao.executeSql("INSERT INTO " + db + " VALUES(?,?,?,?,?) ", lines);
				}
			}
//-消息 分表ID 消息id - 消息json串
//CREATE TABLE  IF NOT EXISTS  W_MSG_0 (ID VARCHAR(40), TEXT TEXT);
//--消息映射用户 分表ID 用户会话a:b - 消息id - 时间
//CREATE TABLE  IF NOT EXISTS  W_MSG_USER_0 (ID VARCHAR(200), USER_FROM VARCHAR(40), USER_TO VARCHAR(40), MSG_ID VARCHAR(40), TIME VARCHAR(20) );

//{"TD":1562058992216,"ST":"000","SF":"223.104.213.72:61511",
//	"DATA":
//			{"STA":"","TEXT":"11111111","ID":"34268913073696_76VkEy","FILE":"","TYPE":"TEXT"}
//,"TO":"91","FROM":{"ID":"000","PWD":"","NAME":"傻子就是你"}
//,"WS":0,"TYPE":"message","TR":1562058992197,"TC":1562058991442}]

		}catch(Exception e) {
			log.error("save msg error " + msg.toString(), e);;
		}
		return Redis.doJedis(new Fun<Long>() {
			@Override
			public Long make(Jedis jedis) {
//				long score = System.currentTimeMillis();
				long score = msg.getTimeDo();
				for(String toId : toIds) {
					String key = Key.getKeyOffline(toId);
					jedis.zadd(key, score, msg.toString());
					log.info(key + " save " + score);

					if(jedis.zcard(key) > 500) {
						long res = jedis.zremrangeByRank(key, 0, 0);
						log.info(key + " rem " + res);
					}
				}
				return score;
			}
		});
	}

	
	
	/**
	 * 查询用户a和用户b聊天的 时间节点之前的数据 mysql 分表
	 * @param userId
	 * @param before
	 * @param count
	 * @return
	 */
	@Override
	public List<Msg> findBefore(String userId, String toId, String before, int count) {
		log.info(Tools.objects2string("findBefore", userId, toId, before, count  ));
		List<Msg> list = new ArrayList<Msg>();
		String id = SqlUtil.makeTableKey(userId, toId);
		String tableName = TABLE_MSG_USER_ + SqlUtil.makeTableCount(COUNT_MSG_USER, id);
		Long time = TimeUtil.format(before, "yyyy-MM-dd HH:mm:ss:SSS").getTime();

		List<Map<String, Object>> ids = dao.findPage("SELECT * FROM " + tableName + " WHERE ID=? AND TIME < ? order by TIME desc ", 1, count, id, time);
		for(Map<String, Object> map : ids) {
			String userFrom = String.valueOf(map.get("USER_FROM"));
			String userTo = String.valueOf(map.get("USER_TO"));
			String msgId = String.valueOf(map.get("MSG_ID"));
			Msg msg = this.findMsg(msgId);
			if(msg != null) {
				list.add(msg);
			}else {
				log.warn("msg null ? " + userFrom + " " + userTo + " " + msgId);
			}
		}
//		mysql> select * from W_MSG_1;
//		+-----------------------+----------------------------------------------------------------------------+
//		| ID                    | TEXT                                                                       |
//		+-----------------------+----------------------------------------------------------------------------+
//		| 35689868912060_Prd_iu | {"TD":1562069257362,"DATA":{"count":0},"TO":"001,002,003","TYPE":"message" |
//
//		mysql> select * from W_MSG_USER_3;
//		+----------+-----------+---------+-----------------------+---------------+
//		| ID       | USER_FROM | USER_TO | MSG_ID                | TIME          |
//		+----------+-----------+---------+-----------------------+---------------+
//		| 000:003: | 000       | 003     | 35689868912060_Prd_iu | 1562069257362 |
		return list;
	}
	/**
	 * 查询用户a和用户b聊天的 时间节点之前的数据 mysql 分表
	 * @param userId
	 * @param before
	 * @param count
	 * @return
	 */
	@Override
	public List<Msg> findBeforeByMerge(String userId, String toId, String before, int count) {
		log.info(Tools.objects2string("findBefore", userId, toId, before, count  ));
		List<Msg> list = new ArrayList<Msg>();
		String id = SqlUtil.makeTableKey(userId, toId);
//		String tableName = TABLE_MSG_USER_ + SqlUtil.makeTableCount(COUNT_MSG_USER, id);
		Long time = TimeUtil.format(before, "yyyy-MM-dd HH:mm:ss:SSS").getTime();
//select u.ID,u.USER_FROM,u.USER_TO,u.MSG_ID,u.TIME,m.TEXT from W_MSG m,W_MSG_USER u where u.MSG_ID=m.ID;
		List<Map<String, Object>> ids = dao.findPage("SELECT u.ID,u.USER_FROM,u.USER_TO,u.MSG_ID,u.TIME,m.TEXT FROM " 
		+ TABLE_MSG + " m," + TABLE_MSG_USER + " u  WHERE u.ID=? AND u.TIME < ? and u.MSG_ID=m.ID order by u.TIME desc ", 1, count, id, time);
		for(Map<String, Object> map : ids) {
			String userFrom = String.valueOf(map.get("USER_FROM"));
			String userTo = String.valueOf(map.get("USER_TO"));
			String msgId = String.valueOf(map.get("MSG_ID"));
			String text = String.valueOf(map.get("TEXT"));
			Msg msg = new Msg(text);
			if(msg != null) {
				list.add(msg);
			}else {
				log.warn("msg null ? " + userFrom + " " + userTo + " " + msgId);
			}
		}
//		mysql> select * from W_MSG_1;
//		+-----------------------+----------------------------------------------------------------------------+
//		| ID                    | TEXT                                                                       |
//		+-----------------------+----------------------------------------------------------------------------+
//		| 35689868912060_Prd_iu | {"TD":1562069257362,"DATA":{"count":0},"TO":"001,002,003","TYPE":"message" |
//
//		mysql> select * from W_MSG_USER_3;
//		+----------+-----------+---------+-----------------------+---------------+
//		| ID       | USER_FROM | USER_TO | MSG_ID                | TIME          |
//		+----------+-----------+---------+-----------------------+---------------+
//		| 000:003: | 000       | 003     | 35689868912060_Prd_iu | 1562069257362 |
		return list;
	}
	@Override
	public List<Msg> findAfter(String userId, String after, int count) {
		return Redis.doJedis(new Fun<List<Msg>>() {
			@Override
			public List<Msg> make(Jedis jedis) {
				List<Msg> list = new ArrayList<Msg>();
				String key = Key.getKeyOffline(userId);
				Long b = TimeUtil.format(after, "yyyy-MM-dd HH:mm:ss:SSS").getTime();
//				Set<String> set = jedis.zrevrangeByScore(key, b - 1, 0, 0, count);//获取上面的 旧的
				Set<String> set = jedis.zrangeByScore(key, b+1, Double.MAX_VALUE, 0, count);	//获取下面的 新的
				log.info("findAfter " + key + " " + after + " " + b + " " + Double.MAX_VALUE + " " + set.size());
				for(String str : set) {
					list.add(new Msg(str));
				}
				return list;
			}
		});

	}

	@Override
	public Msg findMsg(String msgId) {
		Map<String,Object> map = dao.findOne("SELECT * FROM " + TABLE_MSG_ + SqlUtil.makeTableCount(COUNT_MSG,msgId) + " WHERE ID=? ", msgId);
		if(map == null) {
			return null;
		}
		String text = String.valueOf(map.get("TEXT"));
		return new Msg(text);
	}
	@Override
	public Msg findMsgByMerge(String msgId) {
		Map<String,Object> map = dao.findOne("SELECT * FROM " + TABLE_MSG + " WHERE ID=? ", msgId);
		if(map == null) {
			return null;
		}
		String text = String.valueOf(map.get("TEXT"));
		return new Msg(text);
	}

	


	@Test
	public void test() {
		
		MessageServiceImpl service = new MessageServiceImpl();
		
		int saveCount = 100;
		int size = 100;
		Watch w = new Watch("test merge and no merge", size);

		w.costln("sizeMsg", service.sizeMsg());
		w.costln("sizeMsgUser", service.sizeMsgUser());
		
		
		String id = "000";
		String id1 = "001";
		String id2 = "002";
		String id3 = "003";
		String id4 = "004";
		List<String> scores = new ArrayList<String>();
		for(int i = 0; i < saveCount; i++) {
			Msg msg = new MsgBuilder().makeMsg(Plugin.KEY_MESSAGE, "", new Bean().set("count", i))
					.setUserFrom(new User().setId(id).setName("name"))
					.setTimeDo(System.currentTimeMillis());
			msg.addUserTo(id1);
			msg.addUserTo(id2);
			msg.addUserTo(id3);
			msg.addUserTo(id4);
			
			Long score = service.save(msg.getUserTo(), msg);
			String t = TimeUtil.format(score, "yyyy-MM-dd HH:mm:ss:SSS");
			Tools.out(score, t);
			scores.add(t);
		}
		w.costln("save",saveCount);
		w.costln("sizeMsg", service.sizeMsg());
		w.costln("sizeMsgUser", service.sizeMsgUser());
		
		Tools.formatOut(scores);
		//查接收者的离线消息
		List<Msg> list = service.findAfter(id1, scores.get(0), 20);
		Tools.formatOut(list);
		
		//查发送者和接受者会话的历史消息
		List<Msg> list1 = service.findBefore(id, id1, scores.get(scores.size() - 1), 20);
		Tools.formatOut(list1);
		
		for(int i = 0;i < size; i++) {
			Tools.out(i);
			service.findBefore(id, id1, scores.get(scores.size() - 1), i % 20);
		}
		w.costln("findBefore",size);
		for(int i = 0;i < size; i++) {
			Tools.out(i);
			service.findBeforeByMerge(id, id1, scores.get(scores.size() - 1), i % 20);
		}
		w.costln("findBeforeByMerge",size);
		w.res();
		Tools.out(w);
	}
}
