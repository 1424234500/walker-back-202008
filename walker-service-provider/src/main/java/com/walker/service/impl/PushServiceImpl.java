package com.walker.service.impl;

import com.walker.config.Context;
import com.walker.core.cache.Cache;
import com.walker.core.cache.CacheMgr;
import com.walker.core.pipe.Pipe;
import com.walker.dao.JdbcDao;
import com.walker.dao.JobHisRepository;
import com.walker.dao.LogInfoRepository;
import com.walker.mode.JobHis;
import com.walker.mode.LogInfo;
import com.walker.mode.PushBindModel;
import com.walker.mode.PushModel;
import com.walker.service.LogService;
import com.walker.service.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service("logService")
//@Scope("prototype")	//默认单例 dubbo不能单例
public class PushServiceImpl implements PushService
{
    @Autowired
    private JdbcDao jdbcDao;

	@Autowired
	private RedisTemplate redisTemplate;

	private Cache<String> cache = CacheMgr.getInstance();


	/**
	 * * 某客户端id
	 * * title
	 * * content
	 * * type 提醒 or 透传
	 * * ext  其他参数 携带
	 * *
	 *
	 * @param pushModel
	 */
	@Override
	public Integer push(PushModel pushModel) {
		Set<PushBindModel> set = findBind(pushModel.getUSER_ID());
		for(PushBindModel pushBindModel : set){
			


		}

		return set.size();
	}

	/**
	 * 删除推送 若在队列中
	 *
	 * @param pushModel
	 */
	@Override
	public String delPush(PushModel pushModel) {
		return null;
	}

	/**
	 * 绑定用户id和推送id和推送类别
	 * redis	set
	 * key_${user_id1}
	 * 		${push_id1}, ${type1}
	 * 		${push_id2}, ${type2}
	 *
	 * key_${user_id2}
	 * 		${push_id1}, ${type1}
	 * 		${push_id2}, ${type2}
	 *
	 * @param pushBindModel
	 * @return
	 */
	@Override
	public Set<PushBindModel> bind(PushBindModel pushBindModel) {
		pushBindModel.setID("");
		pushBindModel.setS_ATIME("");
		pushBindModel.setS_FLAG("");
		pushBindModel.setS_MTIME("");

		String userKey = Context.getRedisKeyPush() + pushBindModel.getUSER_ID();
		SetOperations<String, PushBindModel> setOperations = redisTemplate.opsForSet();
		setOperations.add(userKey, pushBindModel);
		Set<PushBindModel> res = setOperations.members(userKey);
		return res;
	}

	/**
	 * 绑定用户id和推送id和推送类别
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public Set<PushBindModel> findBind(String userId) {

		String userKey = Context.getRedisKeyPush() + userId;

		SetOperations<String, PushBindModel> setOperations = redisTemplate.opsForSet();

		Set<PushBindModel> res = setOperations.members(userKey);
		return res;
	}

	/**
	 * 取消绑定用户id和推送id和推送类别
	 *
	 * @param pushBindModels
	 * @return
	 */
	@Override
	public void unbind(List<PushBindModel> pushBindModels) {
		SetOperations<String, PushBindModel> setOperations = redisTemplate.opsForSet();
		for(PushBindModel pushBindModel : pushBindModels){
			String userKey = Context.getRedisKeyPush() + pushBindModel.getUSER_ID();

			pushBindModel.setID("");
			pushBindModel.setS_ATIME("");
			pushBindModel.setS_FLAG("");
			pushBindModel.setS_MTIME("");

			setOperations.remove(userKey, pushBindModel);
		}
	}

	/**
	 * 取消绑定用户id和推送id和推送类别
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public Set<PushBindModel> unbind(String userId) {
		String userKey = Context.getRedisKeyPush() + userId;

		SetOperations<String, PushBindModel> setOperations = redisTemplate.opsForSet();

		Set<PushBindModel> res = setOperations.members(userKey);
		redisTemplate.delete(userKey);

		return res;
	}


}