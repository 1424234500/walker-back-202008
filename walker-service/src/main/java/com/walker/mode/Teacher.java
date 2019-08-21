package com.walker.mode;


import javax.persistence.*;
import java.io.Serializable;

/**
 * jpa实体类测试
 */
@Entity
@Table(name = "TEACHER")
public class Teacher implements Cloneable,Serializable{

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

    public String getId() {
        return id;
    }

    public Teacher setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Teacher setName(String name) {
        this.name = name;
        return this;
    }

    public String getTime() {
        return time;
    }

    public Teacher setTime(String time) {
        this.time = time;
        return this;
    }

    public String getPwd() {
        return pwd;
    }

    public Teacher setPwd(String pwd) {
        this.pwd = pwd;
        return this;
    }

    public Teacher(){}
    public Teacher(String id, String name, String time, String pwd){
        this.id = id;
        this.name = name;
        this.time = time;
        this.pwd = pwd;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", pwd='" + pwd + '\'' +
                '}';
    }
}