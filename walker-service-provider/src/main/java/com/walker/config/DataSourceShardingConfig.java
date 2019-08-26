package com.walker.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
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
@EnableTransactionManagement
//@EnableJpaRepositories(
//        basePackages = {"com.walker.dao"}
//)
public class DataSourceShardingConfig extends SpringBootConfiguration {
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 默认主数据源配置
     * @return
     * @throws SQLException
     */
    @Primary
    @Override
    @Bean("shardingDataSource")
    public DataSource dataSource() throws SQLException {
        DataSource ds = super.dataSource();
        log.info("dataSource init " + ds.toString());
        return ds;
    }




}
