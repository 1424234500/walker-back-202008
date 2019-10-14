package com.walker.service;

import com.walker.common.util.Page;
import com.walker.mode.Student;

import java.util.List;
import java.util.Map;


/**
 * 案例：student表的hibernate和mybatis两种方式实现操作
 *
 */
public interface StudentService  {
	

    List<Student> saveAll(List<Student> objs);
    Integer[] deleteAll(List<String> ids);

    Student get(Student obj);
    Integer delete(Student obj);

    List<Student> finds(Student obj, Page page);
    Integer count(Student obj);



}