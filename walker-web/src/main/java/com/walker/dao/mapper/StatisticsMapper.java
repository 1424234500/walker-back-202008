package com.walker.dao.mapper;

import com.walker.common.util.Page;
import com.walker.mode.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 统计sql
 *
 */
public interface StatisticsMapper {

    /**
     * 多指标  按   接口排名
     * X      Y1        Y2              Y3          Y4              Y5
     * 接口名  成功次数  成功平均耗时      失败次数      失败平均耗时     成功率0～100
     */
    List<Map<String, Object>> findAction();

    /**
     * 多指标  按   时间分布（粒度 每分钟 每小时）
     * X      Y1        Y2              Y3          Y4              Y5
     * 时间   成功次数  成功平均耗时      失败次数      失败平均耗时     成功率0～100
     */
//    List<Map> findActionTime(Map<String, Object> object);





}
