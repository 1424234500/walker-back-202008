package com.walker.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 数据源源配置
 * <p>
 * redis
 * <p>
 * mysql
 */

@Configuration
@EnableTransactionManagement
//@MapperScan("com.example.db.dao")
public class DataSourceConfig {
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Bean(name = "sqlSessionFactory")
//    public SqlSessionFactory sqlSessionFactoryBean() {
//        SqlSessionFactoryBean sqlsession = new SqlSessionFactoryBean();
//        sqlsession.setDataSource(dataSource);
//        try {
//            //添加XML目录
//            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//            sqlsession.setMapperLocations(resolver.getResources("classpath:mapping/*.xml"));
//            return sqlsession.getObject();
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Bean
//    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
//        return new SqlSessionTemplate(sqlSessionFactory);
//    }
//
//    @Bean
//    public PlatformTransactionManager annotationDrivenTransactionManager() {
//        return new DataSourceTransactionManager(dataSource);
//    }
//
//
//    @Bean(name = "exampleSequence")
//    public OracleSequenceMaxValueIncrementer exampleSequenceBean(){
//        OracleSequenceMaxValueIncrementer exampleSequence = new OracleSequenceMaxValueIncrementer();
//        exampleSequence.setIncrementerName("EXAMPLE_SEQ");
//        exampleSequence.setDataSource(dataSource);
//        return exampleSequence;
//    }

}
