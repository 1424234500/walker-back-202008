package com.walker.service.impl;

import com.walker.common.util.Page;
import com.walker.core.database.SqlUtil;
import com.walker.dao.JdbcDao;
import com.walker.mode.Teacher;
import com.walker.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("teacherJdbcService")
public class TeacherJdbcServiceImpl implements TeacherService {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    JdbcDao jdbcDao;
    
    
    @Override
    public Teacher add(Teacher test) {
        jdbcDao.executeSql("INSERT INTO TEACHER VALUES(?,?,?,?) ", test.getId(), test.getName(), test.getTime(), test.getPwd());
        return test;
    }

    @Override
    public Integer update(Teacher test) {
        return null;
    }

    @Override
    public Integer delete(Teacher test) {
        return jdbcDao.executeSql("DELETE FROM TEACHER WHERE ID=? ", test.getId());
    }

    @Override
    public Teacher get(Teacher test) {
        List<Teacher> list = jdbcTemplate.query("SELECT * FROM TEACHER WHERE ID=? OR NAME LIKE  CONCAT('%', ?, '%') ", new Object[]{test.getId(), test.getName()}, new BeanPropertyRowMapper<Teacher>(Teacher.class));
        if(list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<Teacher> finds(Teacher test, Page page) {
        String sql = "SELECT * FROM TEACHER WHERE ID=? " +
                "OR NAME LIKE  CONCAT('%', ?, '%') ";
        sql = SqlUtil.makeSqlPage(jdbcDao.getDs(), sql, page.getNowpage(), page.getPagenum());
        List<Teacher> list = jdbcTemplate.query(
                sql,
                new Object[]{test.getId(), test.getName()},
                new BeanPropertyRowMapper<Teacher>(Teacher.class));
        return list;
     }
    @Override
    public Integer count(Teacher test){
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM TEACHER WHERE ID=? OR NAME LIKE CONCAT('%', ?, '%'"
        ,new Object[]{test.getId(), test.getName()}
        ,Integer.class
        )
        ;
    }

}
