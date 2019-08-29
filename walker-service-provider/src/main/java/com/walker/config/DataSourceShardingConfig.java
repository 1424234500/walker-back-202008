package com.walker.config;

import io.shardingsphere.shardingjdbc.spring.boot.SpringBootConfiguration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 数据源源配置   分表分库数据源sharding
 * <p>
 * redis
 * <p>
 * mysql
 *
 *
 * 多数据源事务问题
 * org.springframework.beans.factory.NoUniqueBeanDefinitionException: No qualifying bean of type 'org.springframework.transaction.PlatformTransactionManager' available: expected single matching bean but found 2: transactionManager,shardingTransactionManager
 */

@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
//        basePackages = {"com.walker.daosharding"}   //sharding数据源专用路径dao jpa仓库
//)
public class DataSourceShardingConfig extends SpringBootConfiguration {
    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * sharding数据源配置 重写or覆盖
     */
    @Primary
    @Override
    @Bean("shardingDataSource")
    public DataSource dataSource() throws SQLException {
        DataSource ds = super.dataSource();
        log.info(Config.PRE + "sharding--------------dataSource init " + ds.toString());
        return ds;
    }
    @Bean(name = "shardingJdbcTemplate")
    public JdbcTemplate shardingJdbcTemplate(@Qualifier("shardingDataSource") DataSource shardingDataSource) throws  Exception{
        return new JdbcTemplate(shardingDataSource);
    }
    @Bean(name = "shardingSqlSessionFactory")
    public SqlSessionFactory shardingSqlSessionFactory(@Qualifier("shardingDataSource") DataSource dataSource) throws Exception{
        SqlSessionFactoryBean bean=new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        return bean.getObject();
    }

//    //创建事务管理器
//    @Bean(name = "shardingTransactionManager")
//    public DataSourceTransactionManager shardingTransactionManager(@Qualifier("shardingDataSource") DataSource dataSource){
//        return new DataSourceTransactionManager(dataSource);
//    }
    //创建SqlSessionTemplate
    @Bean(name = "shardingSqlSessionTemplate")
    public SqlSessionTemplate shardingSqlSessionTemplate(@Qualifier("shardingSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws  Exception{
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
