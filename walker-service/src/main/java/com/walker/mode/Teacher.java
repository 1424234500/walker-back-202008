package com.walker.mode;


import javax.persistence.*;
import java.io.Serializable;

/**
 * jpa实体类测试
 */
@Entity
@Table(name = "W_TEACHER")
public class Teacher implements Cloneable,Serializable{

    @Id     //主键
//    @GeneratedValue(strategy = GenerationType.AUTO)     //自增
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name="id", columnDefinition = "varchar(32) default '' comment '主键' ")
    private String id;
    @Column(name = "name", columnDefinition = "varchar(512) default '' comment '姓名' ")    //255
    private String name;
    @Column(name = "time", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '修改时间' ")
    private String time;
    @Column(name = "pwd", columnDefinition = "varchar(32) default '' comment '密码' ")
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