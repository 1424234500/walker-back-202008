package com.walker.socket.server_1.plugin.aop;

import org.apache.log4j.Logger;

import com.walker.common.util.Bean;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.SocketException;

public abstract class Aop<T> {
	protected static Logger log = Logger.getLogger(Aop.class); 

	Bean params;
	Aop(Bean params){
		this.params = params;
	}
	public void error(final Object...objects) throws SocketException {
		throw new SocketException(objects);
	}
	public abstract void doAop(final Msg msg) throws SocketException;
}
