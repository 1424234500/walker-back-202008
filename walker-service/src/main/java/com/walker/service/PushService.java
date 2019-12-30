package com.walker.service;

import com.walker.mode.PushBindModel;
import com.walker.mode.PushModel;

import java.util.List;
import java.util.Set;

/**
 * 推送服务抽象
 *
 * 我 要推送 给
 *
 * 某客户端id
 * title
 * content
 * type 提醒 or 透传
 * ext  其他参数 携带
 *
 * 返回
 *
 */
public interface PushService {

    /**
     * 推送给目标用户
     * USER_ID
     * TITLE
     * CONTENT
     * @param pushModel
     * @return
     */
    Integer push(PushModel pushModel);


    /**
     * 若在队列中 则删除推送
     *
     * ID
     *
     * @param pushModel
     */
    String delPush(PushModel pushModel);


    /**
     * 绑定用户id和推送id和推送类别
     * @param pushBindModel
     * @return
     */
    Set<PushBindModel> bind(PushBindModel pushBindModel);

    /**
     * 绑定用户id和推送id和推送类别
     * @param userId
     * @return
     */
    Set<PushBindModel> findBind(String userId);

    /**
     * 取消绑定用户id和推送id和推送类别
     * 0则取消所有
     * @param pushBindModels
     * @return
     */
    void unbind(List<PushBindModel> pushBindModels);

    /**
     * 取消绑定用户id和推送id和推送类别
     * @param userId
     * @return
     */
    Set<PushBindModel> unbind(String userId);





}