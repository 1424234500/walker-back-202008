package com.walker.dao;

import com.walker.mode.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

 获取全部字段，返回的类型就是对应的实体类或者实体类的集合。
 当不是全部字段时，返回类型就不能是实体类了。

 单个字段单条记录时，返回类型最好用对应字段的类型或者Object。
 单个字段多条记录时，返回类型最好用List<Object>或者List<字段对应类型>。
 多个字段时，不论是多条记录还是单条记录，返回类型都应该是List<Object[]>

 Example api的组成
 Probe: 含有对应字段的实例对象。
 ExampleMatcher：ExampleMatcher携带有关如何匹配特定字段的详细信息，相当于匹配条件。
 Example：由Probe和ExampleMatcher组成，用于查询。
 属性不支持嵌套或者分组约束，比如这样的查询 firstname = ?0 or (firstname = ?1 and lastname = ?2)
 灵活匹配只支持字符串类型，其他类型只支持精确匹配

*/


@Repository
public interface TestRepository extends JpaRepository<Test, String> {//实体类 主键类型
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
     *  nativeQuery sql 原生sql方式 占位符 表名 字段名
     */
    @Transactional
    @Modifying
    @Query(value = "UPDATE TEST_MODE T SET T.NAME =?1 WHERE T.ID=?2", nativeQuery = true)   //占位符传值形式
    int selfUpdateSql(String name, String id);


    /**
     * JPQL sql 非原生方式 hql?  占位符 表对应的 类名 属性名
     */
    @Transactional
    @Modifying
    @Query(value = "update Test t set t.name =?1 where t.id=?2")   //占位符传值形式
    int selfUpdateJPQL(String name, String id);

    /**
     * JPQL删除
     */
    @Query("delete from Test t where t.id=?1 ")
    int selfDeleteJPQL(String id);


    /**
     * JPQL 2   别名
     */
    @Query("from Test u where u.name=:name")
    Test selfFindByName(@Param("name") String name);

    @Query("from Test u where u.id=?1")
    Test selfFindById(String id);
    /**
     * JPQL 分页查询定制
     */
    @Query("select t from Test t where t.name like CONCAT('%', ?1, '%') ")
    List<Test> selfFindPage(String name, Pageable page);

    /**
     * JPQL查询数量
     */
    @Query("select count(t.id) from Test t where t.name like CONCAT('%', ?1, '%') ")
    int selfCount(String name);




}
