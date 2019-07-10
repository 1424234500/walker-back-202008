package com.walker.service;

import com.walker.mode.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface TestJpaService {



    public Test add( Test test);
    public Integer update(Test test);
    public void delete(Test test);
    public Test get(Test test);

    public List<Test> finds(Test test, Pageable page);




}
