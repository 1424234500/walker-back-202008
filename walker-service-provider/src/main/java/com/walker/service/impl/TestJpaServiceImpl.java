package com.walker.service.impl;

import com.walker.common.util.Page;
import com.walker.dao.TestRepository;
import com.walker.mode.Test;
import com.walker.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("testJpaService")
public class TestJpaServiceImpl implements TestService {

    @Autowired
    private TestRepository testRepository;


    @Override
    public Test add(Test test) {
        return testRepository.save(test);
    }

    @Override
    public Integer update(Test test) {
        return testRepository.selfUpdateJPQL(test.getName(), test.getId());
    }

    @Override
    public Integer delete(Test test) {
        testRepository.deleteById(test.getId());
        return 1;
    }

    @Override
    public Test get(Test test) {
        return testRepository.selfFindById(test.getId());
    }

    @Override
    public List<Test> finds(Test test, Page page) {
        Sort sort = new Sort(Sort.Direction.ASC, "name");
        Pageable pageable = new PageRequest(page.getNOWPAGE()-1, page.getSHOWNUM(), sort);
        page.setNUM(testRepository.selfCount(test.getName()));
        return testRepository.selfFindPage(test.getName(), pageable);
    }

    @Override
    public Integer count(Test test) {
        return Integer.valueOf(Long.valueOf(testRepository.count()).intValue());
    }
}
