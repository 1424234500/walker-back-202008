package com.walker.mode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * 基本模型 用于测试 分库分表 walker0 walker1
 */

@Entity
@Table(name = "W_MAN")
public class Man implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	@Id     //主键
	@Column(name="ID", nullable = false, length = 32)
	private String id;
	@Column(name = "NAME", nullable = false, length = 256)    //255
    private String name;
	@Column(name = "SEX", nullable = false, length = 4)    //255
	private Integer sex;
	@Column(name = "PWD", nullable = false, length = 200)    //255
	private String pwd;
	@Column(name = "S_MTIME", nullable = true, length = 32)    //255
    private String time;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Integer getSex() {
		return sex;
	}

	public Man setSex(Integer sex) {
		this.sex = sex;
		return this;
	}

	@Override
	public String toString() {
		return "Man{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", sex='" + sex + '\'' +
				", pwd='" + pwd + '\'' +
				", time='" + time + '\'' +
				'}';
	}

	public String getId() {
		return id;
	}

	public Man setId(String id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Man setName(String name) {
		this.name = name;
		return this;
	}


	public String getPwd() {
		return pwd;
	}

	public Man setPwd(String pwd) {
		this.pwd = pwd;
		return this;
	}

	public String getTime() {
		return time;
	}

	public Man setTime(String time) {
		this.time = time;
		return this;
	}
}