package com.walker.mode;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * jpa实体类 部门
 */
@Entity
@Table(name = "W_DEPT")
public class Dept implements Cloneable,Serializable {

    @Id     //主键
//    @GeneratedValue(strategy = GenerationType.AUTO)     //自增
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "ID", columnDefinition = "varchar(32) default '' comment '主键' ")
    private String ID;
    @Column(name = "S_MTIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '修改时间' ")
    private String S_MTIME;
//    @Column(name = "S_ATIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '添加时间' ")
//    private String S_ATIME;
    @Column(name = "S_FLAG", columnDefinition = "varchar(4) default '0' comment '1是0否' ")
    private String S_FLAG;


    @Column(name = "NAME", columnDefinition = "varchar(512) default 'name' comment '名字' ")    //255
    private String NAME;
    @Column(name = "P_ID", columnDefinition = "varchar(32) default '' comment '上级部门' ")
    private String P_ID;
    @Column(name = "PATH", columnDefinition = "varchar(512) default '' comment '机构树-编码' ")
    private String PATH;
    @Column(name = "PATH_NAME", columnDefinition = "varchar(998) default '' comment '机构树-中文' ")
    private String PATH_NAME;


    @Override
    public String toString() {
        return "Dept{" +
                "ID='" + ID + '\'' +
                ", S_MTIME='" + S_MTIME + '\'' +
//                ", S_ATIME='" + S_ATIME + '\'' +
                ", S_FLAG='" + S_FLAG + '\'' +
                ", NAME='" + NAME + '\'' +
                ", P_ID='" + P_ID + '\'' +
                ", PATH='" + PATH + '\'' +
                ", PATH_NAME='" + PATH_NAME + '\'' +
                '}';
    }

    public String getPATH_NAME() {
        return PATH_NAME;
    }

    public Dept setPATH_NAME(String PATH_NAME) {
        this.PATH_NAME = PATH_NAME;
        return this;
    }

    public String getPATH() {
        return PATH;
    }

    public Dept setPATH(String PATH) {
        this.PATH = PATH;
        return this;
    }

    public String getID() {
        return ID;
    }

    public Dept setID(String ID) {
        this.ID = ID;
        return this;
    }

    public String getS_MTIME() {
        return S_MTIME;
    }

    public Dept setS_MTIME(String s_MTIME) {
        S_MTIME = s_MTIME;
        return this;
    }

//    public String getS_ATIME() {
//        return S_ATIME;
//    }
//
//    public Dept setS_ATIME(String s_ATIME) {
//        S_ATIME = s_ATIME;
//        return this;
//    }

    public String getS_FLAG() {
        return S_FLAG;
    }

    public Dept setS_FLAG(String s_FLAG) {
        S_FLAG = s_FLAG;
        return this;
    }

    public String getNAME() {
        return NAME;
    }

    public Dept setNAME(String NAME) {
        this.NAME = NAME;
        return this;
    }

    public String getP_ID() {
        return P_ID;
    }

    public Dept setP_ID(String p_ID) {
        P_ID = p_ID;
        return this;
    }
}