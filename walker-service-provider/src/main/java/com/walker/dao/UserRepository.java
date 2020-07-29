package com.walker.dao;

import com.walker.mode.Role;
import com.walker.mode.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * sharding 分库 & 分表
 */
@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor {//实体类 主键类型 自定义查询
//    List<T> findAll();
//    List<T> findAll(Sort var1);
//    List<T> findAllById(Iterable<ID> var1);
//    <S extends T> List<S> saveAll(Iterable<S> var1);
//    void flush();
//    <S extends T> S saveAndFlush(S var1);
//    void deleteInBatch(Iterable<T> var1);
//    void deleteAllInBatch();
//    T getOne(ID var1);
//    <S extends T> List<S> findAll(Example<S> var1);
//    <S extends T> List<S> findAll(Example<S> var1, Sort var2);

    /**
     * JPQL查询 删除
     * @Cacheable 缓存方法操作
     */
    @Transactional
    @Modifying
    @Query("delete from User t where t.ID in (?1) ")
    Integer selfDeleteAll(List<String> ids);

    @Query(value = "select t from User t where (t.ID=?1 or t.EMAIL=?1 or t.MOBILE=?1) and (t.PWD=?2 or t.PWD=?3) " )
    User auth(String id, String pwd, String makeStr);
}
