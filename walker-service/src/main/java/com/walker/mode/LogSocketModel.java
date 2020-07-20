package com.walker.mode;

import com.walker.common.util.LangUtil;
import com.walker.common.util.TimeUtil;
import com.walker.service.Config;
import com.walker.system.Pc;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 监控模型 单次接口
 *
 * 分类	CATE
 *
 * 哪个用户	USER
 * 从哪个主机	IP_PORT_FROM
 * 什么时间	S_MTIME
 * 访问了什么东西	URL
 * 如何访问	WAY
 * 访问的目标服务器	IP_PORT_TO
 * 带了什么参数	ARGS
 * 是否抛出异常	IS_EXCEPTION
 * 抛出了什么异常	EXCEPTION
 * 是否访问成功	IS_OK
 * 访问的结果	RES
 * 消耗了多少时间	COST
 *
 * 其他说明	ABOUT
 *
 *
 */

@Entity
@Table(name = "W_LOG_SOCKET_MODEL"
		, indexes = {
//                @Index(name = "INDEX_W_AREA_PATH", columnList = "PATH"),      //索引不能超过1000
//                @Index(name = "INDEX_W_AREA_PATH_NAME", columnList = "PATH_NAME"),
		@Index(name = "INDEX_W_LOG_SOCKET_MODEL_PLUGIN", columnList = "PLUGIN")
		, @Index(name = "INDEX_W_LOG_SOCKET_MODEL_TIME", columnList = "S_MTIME")
		, @Index(name = "INDEX_W_LOG_SOCKET_MODEL_IP_PORT", columnList = "IP_PORT")
}
)

public class LogSocketModel implements Cloneable, Serializable {
	@Id     //主键
	@Column(name="ID", columnDefinition = "varchar(32) default '' comment '主键' ")
	private String ID;
	@Column(name = "PLUGIN", columnDefinition = "varchar(256) default 'plugin' comment '插件' ")
    private String PLUGIN;
	@Column(name = "NET_COUNT", columnDefinition = "varchar(32) default '0' comment '网络次数' ")
	private String NET_COUNT;
	@Column(name = "NET_COST", columnDefinition = "varchar(32) default '0' comment '网络耗时' ")
	private String NET_COST;
	@Column(name = "WAIT_COUNT", columnDefinition = "varchar(32) default '0' comment '等待次数' ")
	private String WAIT_COUNT;
	@Column(name = "WAIT_COST", columnDefinition = "varchar(32) default '0' comment '等待耗时' ")
	private String WAIT_COST;
	@Column(name = "DONE_COUNT", columnDefinition = "varchar(32) default '0' comment '处理次数' ")
	private String DONE_COUNT;
	@Column(name = "DONE_COST", columnDefinition = "varchar(32) default '0' comment '处理耗时' ")
	private String DONE_COST;

	@Column(name = "IP_PORT", columnDefinition = "varchar(64) default '' comment '节点ip:port' ")
	private String IP_PORT;

	@Column(name = "S_MTIME", columnDefinition = "varchar(32) default '' comment '修改时间' ")    //255
	private String S_MTIME = TimeUtil.getTimeYmdHmss();


	public String getID() {
		return ID;
	}

	public LogSocketModel setID(String ID) {
		this.ID = ID;
		return this;
	}

	public String getPLUGIN() {
		return PLUGIN;
	}

	public LogSocketModel setPLUGIN(String PLUGIN) {
		this.PLUGIN = PLUGIN;
		return this;
	}

	public String getNET_COUNT() {
		return NET_COUNT;
	}

	public LogSocketModel setNET_COUNT(String NET_COUNT) {
		this.NET_COUNT = NET_COUNT;
		return this;
	}

	public String getNET_COST() {
		return NET_COST;
	}

	public LogSocketModel setNET_COST(String NET_COST) {
		this.NET_COST = NET_COST;
		return this;
	}

	public String getWAIT_COUNT() {
		return WAIT_COUNT;
	}

	public LogSocketModel setWAIT_COUNT(String WAIT_COUNT) {
		this.WAIT_COUNT = WAIT_COUNT;
		return this;
	}

	public String getWAIT_COST() {
		return WAIT_COST;
	}

	public LogSocketModel setWAIT_COST(String WAIT_COST) {
		this.WAIT_COST = WAIT_COST;
		return this;
	}

	public String getDONE_COUNT() {
		return DONE_COUNT;
	}

	public LogSocketModel setDONE_COUNT(String DONE_COUNT) {
		this.DONE_COUNT = DONE_COUNT;
		return this;
	}

	public String getDONE_COST() {
		return DONE_COST;
	}

	public LogSocketModel setDONE_COST(String DONE_COST) {
		this.DONE_COST = DONE_COST;
		return this;
	}

	public String getIP_PORT() {
		return IP_PORT;
	}

	public LogSocketModel setIP_PORT(String IP_PORT) {
		this.IP_PORT = IP_PORT;
		return this;
	}

	public String getS_MTIME() {
		return S_MTIME;
	}

	public LogSocketModel setS_MTIME(String s_MTIME) {
		S_MTIME = s_MTIME;
		return this;
	}
}