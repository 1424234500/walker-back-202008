package com.walker.mode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 统计模型	某个时间段的统计结果
 *
 * 某台服务器
 * 某个时间点
 * 受理某接口
 * 成功多少次
 * 成功共耗时
 *
 * 失败多少次
 * 失败总耗时
 *
 * (IPPORT, ID, URL, COUNT, TIME, COSTTIME)
 */

@Entity
@Table(name = "W_LOG_TIME")
public class LogTime implements Cloneable, Serializable {
	@Id     //主键
	@Column(name="ID", columnDefinition = "varchar(32) default '' comment '主键' ")
	private String ID;
	@Column(name = "TIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '修改时间' ")
    private String TIME;
	@Column(name = "IPPORT", columnDefinition = "varchar(64) default '' comment 'IPPORT' ")    //255
	private String IPPORT;
	@Column(name = "URL", columnDefinition = "varchar(512) default '' comment 'url' ")    //255
	private String URL;
	@Column(name = "COUNT", columnDefinition = "varchar(64) default '' comment '次数' ")
	private String COUNT;
	@Column(name = "COSTTIME", columnDefinition = "varchar(64) default '' comment '耗时' ")
	private String COSTTIME;

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public String getTIME() {
		return TIME;
	}

	public void setTIME(String TIME) {
		this.TIME = TIME;
	}

	public String getIPPORT() {
		return IPPORT;
	}

	public void setIPPORT(String IPPORT) {
		this.IPPORT = IPPORT;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}

	public String getCOUNT() {
		return COUNT;
	}

	public void setCOUNT(String COUNT) {
		this.COUNT = COUNT;
	}

	public String getCOSTTIME() {
		return COSTTIME;
	}

	public void setCOSTTIME(String COSTTIME) {
		this.COSTTIME = COSTTIME;
	}

	@Override
	public String toString() {
		return "LogTime{" +
				"ID='" + ID + '\'' +
				", TIME='" + TIME + '\'' +
				", IPPORT='" + IPPORT + '\'' +
				", URL='" + URL + '\'' +
				", COUNT='" + COUNT + '\'' +
				", COSTTIME='" + COSTTIME + '\'' +
				'}';
	}
}