package com.walker.mode;

import lombok.Data;

import javax.persistence.*;

/**
 * jpa实体类测试
 */
@Data
@Entity
@Table(name = "TEST_MODE")
public class Test {

    @Id     //主键
    @GeneratedValue(strategy = GenerationType.AUTO)     //自增
    private Long id;
    @Column(length = 32)    //255
    private String name;
    @Column(length = 32)
    private String time;
    @Column(length = 64)
    private String pwd;

    public Test(Long id, String name, String time, String pwd){
        this.id = id;
        this.name = name;
        this.time = time;
        this.pwd = pwd;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String account) {
        this.time = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}