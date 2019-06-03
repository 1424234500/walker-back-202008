package com.walker.socket.client;

import com.walker.common.util.Tools;

/**
 * 模板 便于使用
 * @author walker
 *
 * @param <T>
 */
public abstract class ClientAdapter implements Client{
	
	public String out(Object...objects) {
		String res = Tools.out(objects);
		return res;
	}
	
	
}