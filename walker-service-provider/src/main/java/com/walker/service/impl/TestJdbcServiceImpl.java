package com.walker.service.impl;

import com.walker.common.util.Page;
import com.walker.dao.JdbcDao;
import com.walker.mode.Test;
import com.walker.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("testJdbcService")
public class TestJdbcServiceImpl implements TestService {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    JdbcDao jdbcDao;
    
    
    @Override
    public Test add(Test test) {
        jdbcDao.executeSql("INSERT INTO TEST_MODE VALUES(?,?,?,?) ", test.getId(), test.getName(), test.getTime(), test.getPwd());
        return test;
    }

    @Override
    public Integer update(Test test) {
        return null;
    }

    @Override
    public Integer delete(Test test) {
        return jdbcDao.executeSql("DELETE FROM TEST_MODE WHERE ID=? ", test.getId());
    }

    @Override
    public Test get(Test test) {
        List<Test> list = jdbcTemplate.query("SELECT * FROM TEST_MODE WHERE ID=? OR NAME LIKE  CONCAT('%', ?, '%') ", new Object[]{test.getId(), test.getName()}, new BeanPropertyRowMapper<Test>(Test.class));
        if(list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<Test> finds(Test test, Page page) {
        List<Test> list = jdbcTemplate.query(
                "SELECT * FROM TEST_MODE WHERE ID=? " +
                        "OR NAME LIKE  CONCAT('%', ?, '%') ",
                new Object[]{test.getId(), test.getName()},
                new BeanPropertyRowMapper<Test>(Test.class));
        return list;
     }
    @Override
    public Integer count(Test test){
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM TEST_MODE WHERE ID=? OR NAME LIKE CONCAT('%', ?, '%'"
        ,new Object[]{test.getId(), test.getName()}
        ,Integer.class
        )
        ;
    }

}