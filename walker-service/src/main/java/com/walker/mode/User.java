package com.walker.mode;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * jpa实体类 用户
 */
@Entity
@Table(name = "W_USER")
public class User implements Cloneable,Serializable{

    @Id     //主键
//    @GeneratedValue(strategy = GenerationType.AUTO)     //自增
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name="ID", columnDefinition = "varchar(32) default '' comment '主键' ")
    private String id;
    @Column(name = "S_MTIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '修改时间' ")
    private String sMtime;
    @Column(name = "S_ATIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '添加时间' ")
    private String sAtime;
    @Column(name = "S_FLAG", columnDefinition = "varchar(4) default '0' comment '是1否0有效' ")


    private String sFlag;
    @Column(name = "NAME", columnDefinition = "varchar(512) default 'name' comment '姓名' ")    //255
    private String name;
    @Column(name = "NICK_NAME", columnDefinition = "varchar(512) default '' comment '昵称' ")    //255
    private String nickName;
    @Column(name = "SIGN", columnDefinition = "varchar(512) default '' comment '个性签名' ")    //255
    private String sign;

    @Column(name = "EMAIL", columnDefinition = "varchar(512) default '' comment '邮箱' ")    //255
    private String email;
    @Column(name = "MOBILE", columnDefinition = "varchar(32) default '' comment '电话' ")    //255
    private String mobile;


    @Column(name = "DEPT_CODE", columnDefinition = "varchar(32) default 'name' comment '部门' ")    //255
    private String deptCode;


    @Column(name = "PWD", columnDefinition = "varchar(128) default '' comment '密码' ")
    private String pwd;

    public User(){
        
    }
    public User(String id, String sMtime, String sAtime, String sFlag, String name, String nickName, String sign, String email, String mobile, String deptCode, String pwd) {
        this.id = id;
        this.sMtime = sMtime;
        this.sAtime = sAtime;
        this.sFlag = sFlag;
        this.name = name;
        this.nickName = nickName;
        this.sign = sign;
        this.email = email;
        this.mobile = mobile;
        this.deptCode = deptCode;
        this.pwd = pwd;
    }

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public String getsMtime() {
        return sMtime;
    }

    public User setsMtime(String sMtime) {
        this.sMtime = sMtime;
        return this;
    }

    public String getsAtime() {
        return sAtime;
    }

    public User setsAtime(String sAtime) {
        this.sAtime = sAtime;
        return this;
    }

    public String getsFlag() {
        return sFlag;
    }

    public User setsFlag(String sFlag) {
        this.sFlag = sFlag;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public User setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public User setSign(String sign) {
        this.sign = sign;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public User setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public User setDeptCode(String deptCode) {
        this.deptCode = deptCode;
        return this;
    }

    public String getPwd() {
        return pwd;
    }

    public User setPwd(String pwd) {
        this.pwd = pwd;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", sMtime='" + sMtime + '\'' +
                ", sAtime='" + sAtime + '\'' +
                ", sFlag='" + sFlag + '\'' +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                ", sign='" + sign + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", deptCode='" + deptCode + '\'' +
                '}';
    }
}