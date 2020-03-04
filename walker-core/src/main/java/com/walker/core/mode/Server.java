package com.walker.core.mode;


/**
 * ssh服务器管理
 */
public class Server {
	String name;	//中文名
	String group;	//分组
	String ip = "127.0.0.1";
	String id = "walker";
	String pwd = "";

	String encode = "utf-8";
	public Server(){

	}
	public Server(String ip, String id, String pwd){
		this.ip = ip;
		this.id = id;
		this.pwd = pwd;
	}

	public String getName() {
		return name;
	}

	public Server setName(String name) {
		this.name = name;
		return this;
	}

	public String getGroup() {
		return group;
	}

	public Server setGroup(String group) {
		this.group = group;
		return this;
	}

	public String getIp() {
		return ip;
	}

	public Server setIp(String ip) {
		this.ip = ip;
		return this;
	}

	public String getId() {
		return id;
	}

	public Server setId(String id) {
		this.id = id;
		return this;
	}

	public String getPwd() {
		return pwd;
	}

	public Server setPwd(String pwd) {
		this.pwd = pwd;
		return this;
	}

	public String getEncode() {
		return encode;
	}

	public Server setEncode(String encode) {
		this.encode = encode;
		return this;
	}

	@Override
	public String toString() {
		return "Server{" +
				"name='" + name + '\'' +
				", group='" + group + '\'' +
				", ip='" + ip + '\'' +
				", id='" + id + '\'' +
				", pwd='" + pwd + '\'' +
				", encode='" + encode + '\'' +
				'}';
	}
}
