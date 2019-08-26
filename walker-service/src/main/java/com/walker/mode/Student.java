package com.walker.mode;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * 基本模型 用于测试
 */
public class Student  implements Cloneable, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@Id     //主键
	@Column(name="ID", nullable = false, length = 32)
	private String id;
	@Column(name = "NAME", nullable = false, length = 256)    //255
    private String name;
	@Column(name = "TIME", nullable = true, length = 32)    //255
    private String time;

	public String getId() {
		return id;
	}

	public Student setId(String id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Student setName(String name) {
		this.name = name;
		return this;
	}

	public String getTime() {
		return time;
	}

	public Student setTime(String time) {
		this.time = time;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Student student = (Student) o;
		return Objects.equals(getId(), student.getId());
	}

	@Override
	public String toString() {
		return "Student{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", time='" + time + '\'' +
				'}';
	}
}