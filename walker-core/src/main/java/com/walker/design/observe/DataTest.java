package com.walker.design.observe;


import java.util.Observable;
import java.util.Observer;


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
public class DataTest{

    public static void main(String[] argv){

        DataCenter<String> dataCenter = new DataCenter<>();
        DataUser dataUser1 = new DataUser().setName("n1");
        DataUser dataUser2 = new DataUser().setName("n2");
        dataCenter.addObserver(dataUser1);
        dataCenter.addObserver(dataUser2);

        dataCenter.sendData("helloall");

        dataCenter.deleteObserver(dataUser2);

        dataCenter.sendData("hello1");



    }



}



