package com.walker.dao;

import com.walker.mode.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
    spring data jpa方法命名规则

    关键字	方法命名	sql where字句
    And	findByNameAndPwd	where name= ? and pwd =?
    Or	findByNameOrSex	where name= ? or sex=?
    Is,Equals	findById,findByIdEquals	where id= ?
    Between	findByIdBetween	where id between ? and ?
    LessThan	findByIdLessThan	where id < ?
    LessThanEquals	findByIdLessThanEquals	where id <= ?
    GreaterThan	findByIdGreaterThan	where id > ?
    GreaterThanEquals	findByIdGreaterThanEquals	where id > = ?
    After	findByIdAfter	where id > ?
    Before	findByIdBefore	where id < ?
    IsNull	findByNameIsNull	where name is null
    isNotNull,NotNull	findByNameNotNull	where name is not null
    Like	findByNameLike	where name like ?
    NotLike	findByNameNotLike	where name not like ?
    StartingWith

    findByNameStartingWith	where name like '?%'
    EndingWith	findByNameEndingWith	where name like '%?'
    Containing	findByNameContaining	where name like '%?%'
    OrderBy	findByIdOrderByXDesc	where id=? order by x desc
    Not	findByNameNot	where name <> ?
    In	findByIdIn(Collection<?> c)	where id in (?)
    NotIn	findByIdNotIn(Collection<?> c)	where id not  in (?)
    True
    findByAaaTue

    where aaa = true
    False	findByAaaFalse	where aaa = false
    IgnoreCase	findByNameIgnoreCase	where UPPER(name)=UPPER(?)
*/


@Repository
public interface TestRepository extends JpaRepository<Test, Long> {//实体类 主键类型
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
     *  nativeQuery sql
     */
    @Transactional
    @Modifying
    @Query(value = "update test_mode set name=?1 where id=?2", nativeQuery = true)   //占位符传值形式
    int updateTest(String name, Long id);

    /**
     * spel
     */
    @Query("from Test u where u.name=:name")   //SPEL表达式
    Test findTest(@Param("name") String name);// 参数username 映射到数据库字段username

}