package com.walker.design.observe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;

/**
 * 发布订阅 观察者模式
 *
 *
 *
 * 数据中心
 * 利用java.util.Observable替我们实现 数据结构管理（vector 安全list） 添加 删除 通知
 *
 *
 * 用户/设备 订阅专项数据
 * 利用Observer接口实现订阅接口
 *
 *
 *
 * main
 * 初始化数据中心 初始化用户 绑定订阅关系
 *
 * 等待
 * 用户/设备 被（推送）（是否 线程隔离） 被通知数据变化
 *
 *
 */
public class DataCenter<DATA> extends Observable {
    private static Logger log = LoggerFactory.getLogger(DataCenter.class);

    /**
     * 具体消息
     */
    private DATA data;

    /**
     * 数据时间节点
     */
    private long time;

    /**
     * 消息频道 需要索引决定 是否通知某观察者
     */
    private String channel;



    public DataCenter(){

    }

    public void sendData(DATA data){
        this.data = data;
        this.time = System.currentTimeMillis();
        log.info("Observable send " + data + " at " + time);

        setChanged();   //通知前 必须标示已改变 看源码
        notifyObservers(this.data);
    }


    public DATA getData() {
        return data;
    }

    public DataCenter setData(DATA data) {
        this.data = data;
        return this;
    }

    public long getTime() {
        return time;
    }

    public DataCenter<DATA> setTime(long time) {
        this.time = time;
        return this;
    }
}
