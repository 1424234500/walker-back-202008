package com.walker.service;


import com.walker.common.util.Bean;
import com.walker.mode.Area;

import java.util.List;

/**
 * 初始化数据服务 调度器
 */
public interface SyncService {

    /**
     * 高耗时 初始化地理信息 异步
     * @param args
     */
    Bean syncArea(Bean args);


    /**
     * 高耗时 初始化信息 异步
     * @param args
     */
    Bean syncDept(Bean args);


    /**
     * 高耗时 初始化信息 异步
     * @param args
     */
    Bean syncUser(Bean args);



}
