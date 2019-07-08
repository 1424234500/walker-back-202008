package com.walker.service;

import com.walker.mode.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface TestService {

    public Test getOne(Long id);
    /**
     * new Sort(Sort.Direction.DESC, "id");
     */
    public Page<Test> finds(Test test, Sort sort, int pageSize, int showNum) ;

    public Test add( Test test);
    public Integer update(Test test);

    public Test save( Test test);
    public void delete(Long id);



}
