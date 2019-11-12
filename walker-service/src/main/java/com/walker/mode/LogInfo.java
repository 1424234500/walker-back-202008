package com.walker.mode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 基本模型 用于测试 分库分表 walker0 walker1
 */

@Entity
@Table(name = "W_LOG_INFO")
public class LogInfo implements Cloneable, Serializable {
	@Id     //主键
	@Column(name="ID", columnDefinition = "varchar(32) default '' comment '主键' ")
	private String id;
	@Column(name = "TIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '修改时间' ")
    private String time;
	@Column(name = "USERID", columnDefinition = "varchar(32) default '' comment '用户id' ")    //255
	private String USERID;
	@Column(name = "URL", columnDefinition = "varchar(512) default '' comment 'url' ")    //255
	private String URL;
	@Column(name = "IP", columnDefinition = "varchar(64) default '' comment 'ip:port' ")
	private String IP;
	@Column(name = "HOST", columnDefinition = "varchar(64) default '' comment 'host' ")
	private String HOST;
	@Column(name = "MAC", columnDefinition = "varchar(64) default '' comment 'Mac' ")
	private String MAC;
	@Column(name = "ABOUT", columnDefinition = "varchar(512) default '' comment '说明' ")
	private String ABOUT;

	@Override
	public String toString() {
		return "LogInfo{" +
				"id='" + id + '\'' +
				", time='" + time + '\'' +
				", USERID='" + USERID + '\'' +
				", URL='" + URL + '\'' +
				", IP='" + IP + '\'' +
				", HOST='" + HOST + '\'' +
				", MAC='" + MAC + '\'' +
				", ABOUT='" + ABOUT + '\'' +
				'}';
	}

	public String getHOST() {
		return HOST;
	}

	public LogInfo setHOST(String HOST) {
		this.HOST = HOST;
		return this;
	}

	public String getId() {
		return id;
	}

	public LogInfo setId(String id) {
		this.id = id;
		return this;
	}

	public String getTime() {
		return time;
	}

	public LogInfo setTime(String time) {
		this.time = time;
		return this;
	}

	public String getUSERID() {
		return USERID;
	}

	public LogInfo setUSERID(String USERID) {
		this.USERID = USERID;
		return this;
	}

	public String getURL() {
		return URL;
	}

	public LogInfo setURL(String URL) {
		this.URL = URL;
		return this;
	}

	public String getIP() {
		return IP;
	}

	public LogInfo setIP(String IP) {
		this.IP = IP;
		return this;
	}

	public String getMAC() {
		return MAC;
	}

	public LogInfo setMAC(String MAC) {
		this.MAC = MAC;
		return this;
	}

	public String getABOUT() {
		return ABOUT;
	}

	public LogInfo setABOUT(String ABOUT) {
		this.ABOUT = ABOUT;
		return this;
	}
}