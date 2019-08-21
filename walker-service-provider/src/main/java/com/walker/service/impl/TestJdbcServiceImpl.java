package com.walker.service.impl;

import com.walker.common.util.Page;
import com.walker.dao.JdbcDao;
import com.walker.mode.Teacher;
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
    public Teacher add(Teacher test) {
        jdbcDao.executeSql("INSERT INTO TEST_MODE VALUES(?,?,?,?) ", test.getId(), test.getName(), test.getTime(), test.getPwd());
        return test;
    }

    @Override
    public Integer update(Teacher test) {
        return null;
    }

    @Override
    public Integer delete(Teacher test) {
        return jdbcDao.executeSql("DELETE FROM TEST_MODE WHERE ID=? ", test.getId());
    }

    @Override
    public Teacher get(Teacher test) {
        List<Teacher> list = jdbcTemplate.query("SELECT * FROM TEST_MODE WHERE ID=? OR NAME LIKE  CONCAT('%', ?, '%') ", new Object[]{test.getId(), test.getName()}, new BeanPropertyRowMapper<Teacher>(Teacher.class));
        if(list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<Teacher> finds(Teacher test, Page page) {
        List<Teacher> list = jdbcTemplate.query(
                "SELECT * FROM TEST_MODE WHERE ID=? " +
                        "OR NAME LIKE  CONCAT('%', ?, '%') ",
                new Object[]{test.getId(), test.getName()},
                new BeanPropertyRowMapper<Teacher>(Teacher.class));
        return list;
     }
    @Override
    public Integer count(Teacher test){
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM TEST_MODE WHERE ID=? OR NAME LIKE CONCAT('%', ?, '%'"
        ,new Object[]{test.getId(), test.getName()}
        ,Integer.class
        )
        ;
    }

}
