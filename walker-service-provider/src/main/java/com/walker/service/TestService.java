package com.walker.service;

import com.walker.common.util.Page;
import com.walker.mode.Test;
import io.swagger.models.auth.In;

import java.util.List;

public interface TestService {



    public Test add(Test test);
    public Integer update(Test test);
    public Integer delete(Test test);
    public Test get(Test test);

    public List<Test> finds(Test test, Page page);
    public Integer count(Test test);




}
