package com.walker.config;

import com.walker.ApplicationTests;
import com.walker.common.util.Tools;
import com.walker.mode.Teacher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import static org.junit.Assert.*;

public class DataSourceConfigTest extends ApplicationTests {
    @Autowired
    @Qualifier("dataSource")
    DataSource dataSource;
    @Autowired
    @Qualifier("shardingDataSource")
    DataSource shardingDataSource;


    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    @Qualifier("shardingJdbcTemplate")  //同上都是同一个数据源同一个template
    JdbcTemplate shardingJdbcTemplate;



    @Test
    public void test(){
        Tools.out("walker normarl ds");
        Tools.out(dataSource);
        Tools.out(jdbcTemplate);
        jdbcTemplate.execute("insert into TEACHER values('id','name','pwd','time' ) ");
        Tools.out(jdbcTemplate.queryForList( "select * from TEACHER "));

        Tools.out("walker0,walker1 sharding ds");
        Tools.out(shardingDataSource);
        Tools.out(shardingJdbcTemplate);
        shardingJdbcTemplate.execute("insert into TEACHER values('id1','name','pwd','time' ) ");
        Tools.out(shardingJdbcTemplate.queryForList( "select * from TEACHER " ));






    }


}