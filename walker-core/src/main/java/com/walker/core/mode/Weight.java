package com.walker.core.mode;

/**
 * 数据结构  线 权重
 *
 * 节点之间的距离 关系
 *
 */
public class Weight {

	int num = 1;

	int value = 1;

	public int getWeight(){
		return num * value;
	}


	public int getNum() {
		return num;
	}

	public Weight setNum(int num) {
		this.num = num;
		return this;
	}

	public int getValue() {
		return value;
	}

	public Weight setValue(int value) {
		this.value = value;
		return this;
	}

	@Override
	public String toString() {
		return "" + num + "X" + value + "";
	}
}

