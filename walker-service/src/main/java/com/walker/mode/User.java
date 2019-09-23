package com.walker.mode;

import com.walker.common.util.Bean;

import java.io.Serializable;

/**
 * socket 用户
 *
 * */
public class User extends Bean implements Cloneable, Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public User() {
		
	}
	public User(Bean bean) {
		super(bean);
	}


	public String getId() {
		return get(Key.ID, "");
	}


	public User setId(String id) {
		set(Key.ID, id);
		return this;
	}


	public String getName() {
		return get(Key.NAME, "");
	}


	public User setName(String name) {
		set(Key.NAME, name);
		return this;
	}


	public String getPwd() {
		return get(Key.PWD, "");
	}


	public User setPwd(String pwd) {
		set(Key.PWD, pwd);
		return this;
	}


	
	public boolean equ(String id) {
		return this.getId().equals(id);
	}

//json序列化异常
//	@Override
//	public boolean equals(Object obj) {
////		mode user = (mode)obj;
//		mode user = new mode((Bean)obj);
//
//		return this.getId().equals(user.getId());
//	}


	public boolean isValid() {
		return this.getId().length() > 0;
	}
	@Override
	public String toString() {
		String pwd = getPwd();
		setPwd("");
		String res = super.toString();
		setPwd(pwd);
		return res;
	}
	
	


}
