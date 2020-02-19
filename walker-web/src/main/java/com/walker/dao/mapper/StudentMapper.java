package com.walker.dao.mapper;

import com.walker.common.util.Page;
import com.walker.mode.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 对象操作
 *
 */
public interface StudentMapper {
    /**
     * 分页查询 排序
     * @param object
     * @param page
     * @return
     */
    List<Student> findPage(@Param("object") Student object, @Param("page") Page page);

    /**
     * 计数
     * @param object
     * @return
     */
    Integer count(@Param("object") Student object);

    /**
     * 批量插入
     * @param list
     */
    Integer insert(List<Student> list);

    /**
     * 批量更新
     * @param list
     */
    Integer update(List<Student> list);

    /**
     * 批量删除
     * @param list
     */
    Integer delete(List<String> list);

}
