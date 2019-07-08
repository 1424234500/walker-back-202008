package com.walker.service.impl;

import com.walker.dao.TestRepository;
import com.walker.mode.Test;
import com.walker.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestRepository testRepository;


    /**
     *  Sort sort = new Sort(Sort.Direction.DESC, "id");
     *
     * @return
     */
    @Override
    public Page<Test> finds(Test test, Sort sort, int pageNum, int pageSize) {
        //将参数传给这个方法就可以实现物理分页了，非常简单。
        Pageable pageable = new PageRequest(pageNum, pageSize, sort);
        Page<Test> modes = testRepository.findAll(pageable);
        return modes;
    }

    @Override
    public Integer update(Test test) {
        return testRepository.updateTest(test.getName(), test.getId());
    }

    @Override
    public void delete(Long id) {
        testRepository.deleteById(id);
    }

    @Override
    public Test add(Test test) {
        return testRepository.save(test);
    }

    @Override
    public Test save(Test test) {
        return testRepository.save(test);
    }

    @Override
    public Test getOne(Long id) {
        return testRepository.getOne(id);
    }
}
