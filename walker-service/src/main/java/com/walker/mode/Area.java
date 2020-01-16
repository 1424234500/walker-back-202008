package com.walker.mode;


import com.walker.common.util.TimeUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * jpa实体类 地理信息 省市县区村
 */
@Entity
@Table(name = "W_AREA")
public class Area implements Cloneable,Serializable {

    @Id     //主键
//    @GeneratedValue(strategy = GenerationType.AUTO)     //自增
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "ID", columnDefinition = "varchar(32) default '' comment '主键' ")
    private String ID;
    @Column(name = "S_MTIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '修改时间' ")
    private String S_MTIME = TimeUtil.getTimeYmdHmss();
//    @Column(name = "S_ATIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '添加时间' ")
//    private String S_ATIME;
    @Column(name = "S_FLAG", columnDefinition = "varchar(4) default '0' comment '1是0否' ")
    private String S_FLAG;


    @Column(name = "NAME", columnDefinition = "varchar(512) default 'name' comment '名字' ")    //255
    private String NAME;
    @Column(name = "P_ID", columnDefinition = "varchar(32) default '' comment '上级' ")
    private String P_ID;
    @Column(name = "PATH", columnDefinition = "varchar(512) default '' comment '机构树-编码' ")
    private String PATH;
    @Column(name = "PATH_NAME", columnDefinition = "varchar(998) default '' comment '机构树-中文' ")
    private String PATH_NAME;

    @Column(name = "CODE", columnDefinition = "varchar(32) default '' comment 'code' ")
    private String CODE;

    @Column(name = "LEVEL", columnDefinition = "varchar(4) default '' comment '级别 深度' ")
    private String LEVEL;


    public String getID() {
        return ID;
    }

    public Area setID(String ID) {
        this.ID = ID;
        return this;
    }

    public String getS_MTIME() {
        return S_MTIME;
    }

    public Area setS_MTIME(String s_MTIME) {
        S_MTIME = s_MTIME;
        return this;
    }

    public String getS_FLAG() {
        return S_FLAG;
    }

    public Area setS_FLAG(String s_FLAG) {
        S_FLAG = s_FLAG;
        return this;
    }

    public String getNAME() {
        return NAME;
    }

    public Area setNAME(String NAME) {
        this.NAME = NAME;
        return this;
    }

    public String getP_ID() {
        return P_ID;
    }

    public Area setP_ID(String p_ID) {
        P_ID = p_ID;
        return this;
    }

    public String getPATH() {
        return PATH;
    }

    public Area setPATH(String PATH) {
        this.PATH = PATH;
        return this;
    }

    public String getPATH_NAME() {
        return PATH_NAME;
    }

    public Area setPATH_NAME(String PATH_NAME) {
        this.PATH_NAME = PATH_NAME;
        return this;
    }

    public String getCODE() {
        return CODE;
    }

    public Area setCODE(String CODE) {
        this.CODE = CODE;
        return this;
    }

    public Integer getLEVEL() {
        LEVEL = (LEVEL == null || LEVEL.length() == 0) ? "0" : LEVEL;
        return Integer.valueOf(LEVEL);
    }

    public Area setLEVEL(String LEVEL) {
        this.LEVEL = LEVEL;
        return this;
    }

    @Override
    public String toString() {
        return "Area{" +
                "ID='" + ID + '\'' +
                ", S_MTIME='" + S_MTIME + '\'' +
                ", S_FLAG='" + S_FLAG + '\'' +
                ", NAME='" + NAME + '\'' +
                ", P_ID='" + P_ID + '\'' +
                ", PATH='" + PATH + '\'' +
                ", PATH_NAME='" + PATH_NAME + '\'' +
                ", CODE='" + CODE + '\'' +
                ", LEVEL='" + LEVEL + '\'' +
                '}';
    }
}