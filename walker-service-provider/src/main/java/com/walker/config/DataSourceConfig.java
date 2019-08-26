package com.walker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 数据源源配置
 * <p>
 * redis
 * <p>
 * mysql
 */

@Configuration
public class DataSourceConfig  {
    private Logger log = LoggerFactory.getLogger(getClass());

//    /**
//     * 默认主数据源配置
//     * @return
//     * @throws SQLException
//     */
//    @Bean("dataSource")
//    @ConfigurationProperties(prefix="spring.datasource")
//    public DataSource dataSource() throws SQLException {
//        log.info("init----------dataSource init");
//        return DataSourceBuilder.create().build();
//    }
//
//    @Autowired
//    DataSource dataSource;
//    @Bean("jdbcTemplate")
//    public JdbcTemplate jdbcTemplate(){
//        return  new JdbcTemplate(dataSource);
//    }




}
