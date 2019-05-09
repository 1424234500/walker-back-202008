package com.walker.core.database;

import java.util.EnumMap;

import org.apache.log4j.Logger;

import com.walker.core.aop.TestAdapter;


/**
 * 连接池 管理器
 * 管理多种连接池
 *
 */
class PoolMgr extends TestAdapter{
	private static Logger log = Logger.getLogger("pool"); 

	private PoolMgr() {
	}
	private static EnumMap<Type, Pool> connMap;
	static {
		connMap = new EnumMap<>(Type.class);
	}
	public static Pool getInstance() {
		return getInstance(null);
	}

	public static Pool getInstance(Type type) {
		Pool conn = connMap.get(type);
		if(conn == null){
			switch(type){
			case JDBC:
				
				break;
			case C3P0:
			default:
				conn = new PoolC3p0Impl();
			}
			connMap.put(type, conn);
		}
		return conn;
	}

	public boolean doTest() {
		return PoolMgr.getInstance() == null;
	}


}

enum Type{
	C3P0,JDBC
}



