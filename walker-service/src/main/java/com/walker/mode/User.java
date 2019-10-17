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
    private String ID;
    @Column(name = "S_MTIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '修改时间' ")
    private String S_MTIME;
    @Column(name = "S_ATIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '添加时间' ")
    private String S_ATIME;
    @Column(name = "S_FLAG", columnDefinition = "varchar(4) default '0' comment '1是0否' ")
    private String S_FLAG;
    @Column(name = "NAME", columnDefinition = "varchar(512) default 'name' comment '名字' ")    //255
    private String NAME;
    @Column(name = "SEX", columnDefinition = "varchar(4) default '0' comment '1男0女' ")
    private String SEX;
    @Column(name = "NICK_NAME", columnDefinition = "varchar(512) default '' comment '昵称' ")    //255
    private String NICK_NAME;
    @Column(name = "SIGN", columnDefinition = "varchar(512) default '' comment '个性签名' ")    //255
    private String SIGN;
    @Column(name = "EMAIL", columnDefinition = "varchar(512) default '' comment '邮箱' ")    //255
    private String EMAIL;
    @Column(name = "MOBILE", columnDefinition = "varchar(32) default '' comment '电话' ")    //255
    private String MOBILE;


    @Column(name = "DEPT_CODE", columnDefinition = "varchar(32) default 'name' comment '部门' ")    //255
    private String DEPT_CODE;

    @Column(name = "PWD", columnDefinition = "varchar(128) default '' comment '密码' ")
    private String PWD;

    public User(){

    }

    public String getS_MTIME() {
        return S_MTIME;
    }

    public User setS_MTIME(String S_MTIME) {
        this.S_MTIME = S_MTIME;
        return this;
    }

    public String getS_ATIME() {
        return S_ATIME;
    }

    public User setS_ATIME(String S_ATIME) {
        this.S_ATIME = S_ATIME;
        return this;
    }

    public String getS_FLAG() {
        return S_FLAG;
    }

    public User setS_FLAG(String S_FLAG) {
        this.S_FLAG = S_FLAG;
        return this;
    }

    public String getSEX() {
        return SEX;
    }

    public User setSEX(String SEX) {
        this.SEX = SEX;
        return this;
    }

    public String getID() {
        return ID;
    }

    public User setID(String ID) {
        this.ID = ID;
        return this;
    }

    public String getNAME() {
        return NAME;
    }

    public User setNAME(String NAME) {
        this.NAME = NAME;
        return this;
    }

    public String getNICK_NAME() {
        return NICK_NAME;
    }

    public User setNICK_NAME(String NICK_NAME) {
        this.NICK_NAME = NICK_NAME;
        return this;
    }

    public String getSIGN() {
        return SIGN;
    }

    public User setSIGN(String SIGN) {
        this.SIGN = SIGN;
        return this;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public User setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
        return this;
    }

    public String getMOBILE() {
        return MOBILE;
    }

    public User setMOBILE(String MOBILE) {
        this.MOBILE = MOBILE;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "ID='" + ID + '\'' +
                ", S_MTIME='" + S_MTIME + '\'' +
                ", S_ATIME='" + S_ATIME + '\'' +
                ", S_FLAG='" + S_FLAG + '\'' +
                ", NAME='" + NAME + '\'' +
                ", SEX='" + SEX + '\'' +
                ", NICK_NAME='" + NICK_NAME + '\'' +
                ", SIGN='" + SIGN + '\'' +
                ", EMAIL='" + EMAIL + '\'' +
                ", MOBILE='" + MOBILE + '\'' +
                ", DEPT_CODE='" + DEPT_CODE + '\'' +
                ", PWD='" + PWD + '\'' +
                '}';
    }

    public String getDEPT_CODE() {
        return DEPT_CODE;
    }

    public User setDEPT_CODE(String DEPT_CODE) {
        this.DEPT_CODE = DEPT_CODE;
        return this;
    }

    public String getPWD() {
        return PWD;
    }

    public User setPWD(String PWD) {
        this.PWD = PWD;
        return this;
    }


}