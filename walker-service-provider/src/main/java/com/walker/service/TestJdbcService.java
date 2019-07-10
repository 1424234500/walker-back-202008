package com.walker.service;

import com.walker.mode.Test;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TestJdbcService {



    public Test add(Test test);
    public Integer update(Test test);
    public Integer delete(Test test);
    public Test get(Test test);

    public List<Test> finds(Test test, Pageable page);




}
