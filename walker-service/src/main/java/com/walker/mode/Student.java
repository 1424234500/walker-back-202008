package com.walker.mode;

import java.util.Arrays;

public class Student {
    private String id;

    private String name;

    private String time;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public String toString() {
		return Arrays.toString(new Object[]{"Student", id, name, time});
	}
}