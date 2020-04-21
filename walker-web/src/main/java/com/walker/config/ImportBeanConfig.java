package com.walker.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walker.core.annotation.*;
import com.walker.dao.JdbcDao;
import com.walker.service.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;

/**
 * spring扩展 自定义导入beanDefin
 *
 * 模拟mybatis MapperScannerRegistrar
 * MybatisAutoConfiguration
 *      @Configuration
 *     @Import({MybatisAutoConfiguration.AutoConfiguredMapperScannerRegistrar.class})
 *     @ConditionalOnMissingBean({MapperFactoryBean.class})
 * 扫描注解
 * 动态代理
 * 反射
 *
 * xml配置bean
 * @Bean 被扫描
 * 其他被扫描的类 @Import 导入该配置类
 *
 *
 */
public class ImportBeanConfig implements ImportBeanDefinitionRegistrar {

    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    JdbcDao jdbcDao;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        log.info(Config.getPre() + " ImportBeanConfig ");

//        WalkerJdbc 仿造mybatis实现 代理注入
//        扫描注解
        Tracker[] trackerClass = {
            new Tracker(WalkerJdbc.class, ElementType.TYPE,  new OnAnnotation(){
                @Override
                public Status make(Annotation annotation, ElementType type, Object object, Class<?> cls) {
                    if(type.equals(ElementType.TYPE)){//类

                        Class<?> mapper = (Class<?>)object;
                        log.debug("begin import bean " + mapper.toString());

                        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();

                        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
                        beanDefinition.setBeanClass(WalkerJdbcFactory.class);
                        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(mapper);

                        beanDefinitionRegistry.registerBeanDefinition(mapper.getName(), beanDefinition);

                        log.debug("end   import bean " + mapper.toString());

                        return Status.STOP_CLASS;
                    }

                    return null;
                }
            }),
        };
        Tracker[] trackerField = {
//                new Tracker(DBConstraints.class, ElementType.FIELD,  new DBTableTracker()),
//                new Tracker(DBSQLInteger.class, ElementType.FIELD,  new DBTableTracker()),
//                new Tracker(DBSQLString.class, ElementType.FIELD,  new DBTableTracker()),
//                new Tracker(DBTable.class, ElementType.FIELD,  new DBTableTracker()),
        };
        Tracker[] trackerMethod = {
//                new Tracker(UseCase.class, ElementType.METHOD,  new UseCaseTracker()),
//                new Tracker(Test.class, ElementType.METHOD,  new TestTracker()),
        };

        Tracker[][] trackerAll = { trackerClass, trackerField, trackerMethod }; //分层
        TrackerUtil.make("", trackerAll);



    }






    /**
     * MapperFactoryBean
     * @param <T>
     */
    class WalkerJdbcFactoryBean<T> implements FactoryBean<T> {


        private Class<T> mapperInterface;

        private boolean addToConfig = true;

        public WalkerJdbcFactoryBean() {
            //intentionally empty
        }

        public WalkerJdbcFactoryBean(Class<T> mapperInterface) {
            this.mapperInterface = mapperInterface;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T getObject() throws Exception {
//        return getSqlSession().getMapper(this.mapperInterface);
//        handler MapperProxy
            return WalkerJdbcFactory.getInstance(mapperInterface, jdbcDao);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Class<T> getObjectType() {
            return this.mapperInterface;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSingleton() {
            return true;
        }

        //------------- mutators --------------

        /**
         * Sets the mapper interface of the MyBatis mapper
         *
         * @param mapperInterface class of the interface
         */
        public void setMapperInterface(Class<T> mapperInterface) {
            this.mapperInterface = mapperInterface;
        }

        /**
         * Return the mapper interface of the MyBatis mapper
         *
         * @return class of the interface
         */
        public Class<T> getMapperInterface() {
            return mapperInterface;
        }

        /**
         * If addToConfig is false the mapper will not be added to MyBatis. This means
         * it must have been included in mybatis-config.xml.
         * <p/>
         * If it is true, the mapper will be added to MyBatis in the case it is not already
         * registered.
         * <p/>
         * By default addToCofig is true.
         *
         * @param addToConfig
         */
        public void setAddToConfig(boolean addToConfig) {
            this.addToConfig = addToConfig;
        }

        /**
         * Return the flag for addition into MyBatis config.
         *
         * @return true if the mapper will be added to MyBatis in the case it is not already
         * registered.
         */
        public boolean isAddToConfig() {
            return addToConfig;
        }
    }
}


