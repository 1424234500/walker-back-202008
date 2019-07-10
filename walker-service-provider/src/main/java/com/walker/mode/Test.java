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
//    @GeneratedValue(strategy = GenerationType.AUTO)     //自增
    @Column(name="ID", nullable = false, length = 32)
    private String id;
    @Column(name = "NAME", nullable = false, length = 256)    //255
    private String name;
    @Column(name = "TIME", nullable = false, length = 32)
    private String time;
    @Column(name = "PWD", nullable = true, length = 64)
    private String pwd;

    public Test(){}
    public Test(String id, String name, String time, String pwd){
        this.id = id;
        this.name = name;
        this.time = time;
        this.pwd = pwd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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