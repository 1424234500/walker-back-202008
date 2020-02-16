package com.walker.mode;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * jpa实体类 任务执行记录
 */
@Entity
@Table(name = "W_JOB_HIS")
public class JobHis implements Cloneable,Serializable{

    @Id     //主键
//    @GeneratedValue(strategy = GenerationType.AUTO)     //自增
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name="ID", columnDefinition = "varchar(32) default '' comment '主键' ")
    private String ID;
    @Column(name="JOB_NAME", columnDefinition = "varchar(32) default '' comment '任务ID' ")
    private String JOB_NAME;

    @Column(name = "S_TIME_START", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '开始时间' ")
    private String S_TIME_START;
    @Column(name = "S_TIME_STOP", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '结束时间' ")
    private String S_TIME_STOP;
    @Column(name = "S_TIME_COST", columnDefinition = "varchar(32) default '0' comment '耗时' ")
    private String S_TIME_COST;

    @Column(name = "IP_PORT", columnDefinition = "varchar(64) default '127.0.0.1:8080' comment '节点' ")
    private String IP_PORT;
    @Column(name = "STATUS", columnDefinition = "varchar(10) default '0' comment '状态0失败1执行中2成功' ")    //255
    private String STATUS;
    @Column(name = "INFO", columnDefinition = "varchar(128) default '' comment 'INFO' ")
    private String INFO;
    @Column(name = "TIP", columnDefinition = "varchar(888) default '' comment 'TIP' ")
    private String TIP;
    @Column(name = "ABOUT", columnDefinition = "varchar(888) default '' comment '说明' ")    //255
    private String ABOUT;


    public JobHis(){

    }

    public String getID() {
        return ID;
    }

    public JobHis setID(String ID) {
        this.ID = ID;
        return this;
    }

    public String getTIP() {
        return TIP;
    }

    public JobHis setTIP(String TIP) {
        if(TIP != null && TIP.length() > 800){
            TIP = TIP.substring(0, 800);
        }
        this.TIP = TIP;
        return this;
    }

    public String getS_TIME_START() {
        return S_TIME_START;
    }

    public JobHis setS_TIME_START(String S_TIME_START) {
        this.S_TIME_START = S_TIME_START;
        return this;
    }

    public String getS_TIME_STOP() {
        return S_TIME_STOP;
    }

    public JobHis setS_TIME_STOP(String S_TIME_STOP) {
        this.S_TIME_STOP = S_TIME_STOP;
        return this;
    }

    public String getS_TIME_COST() {
        return S_TIME_COST;
    }

    public JobHis setS_TIME_COST(String S_TIME_COST) {
        this.S_TIME_COST = S_TIME_COST;
        return this;
    }

    public String getIP_PORT() {
        return IP_PORT;
    }

    public JobHis setIP_PORT(String IP_PORT) {
        this.IP_PORT = IP_PORT;
        return this;
    }

    public String getJOB_NAME() {
        return JOB_NAME;
    }

    public JobHis setJOB_NAME(String JOB_NAME) {
        this.JOB_NAME = JOB_NAME;
        return this;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public JobHis setSTATUS(String STATUS) {
        this.STATUS = STATUS;
        return this;
    }

    public String getINFO() {
        return INFO;
    }

    public JobHis setINFO(String INFO) {
        this.INFO = INFO;
        return this;
    }

    public String getABOUT() {
        return ABOUT;
    }

    public JobHis setABOUT(String ABOUT) {
        this.ABOUT = ABOUT;
        return this;
    }

    @Override
    public String toString() {
        return "JobHis{" +
                "ID='" + ID + '\'' +
                ", S_TIME_START='" + S_TIME_START + '\'' +
                ", S_TIME_STOP='" + S_TIME_STOP + '\'' +
                ", S_TIME_COST='" + S_TIME_COST + '\'' +
                ", IP_PORT='" + IP_PORT + '\'' +
                ", STATUS='" + STATUS + '\'' +
                ", INFO='" + INFO + '\'' +
                ", TIP='" + TIP + '\'' +
                ", ABOUT='" + ABOUT + '\'' +
                '}';
    }
}