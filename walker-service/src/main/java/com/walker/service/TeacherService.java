package com.walker.service;

import com.walker.common.util.Page;
import com.walker.mode.Teacher;

import java.util.List;

public interface TeacherService {



    public Teacher add(Teacher test);
    public Integer update(Teacher test);
    public Integer delete(Teacher test);
    public Teacher get(Teacher test);

    public List<Teacher> finds(Teacher test, Page page);
    public Integer count(Teacher test);




}
