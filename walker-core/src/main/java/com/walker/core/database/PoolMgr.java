package com.walker.core.database;

import com.walker.core.aop.TestAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;


/**
 * 连接池 管理器
 * 管理多种连接池
 *
 */
class PoolMgr extends TestAdapter{
	private static Logger log = LoggerFactory.getLogger("pool");

	private PoolMgr() {
	}
	private static EnumMap<Type, Pool> connMap;
	static {
		connMap = new EnumMap<>(Type.class);
	}
	public static Pool getInstance() {
		return getInstance(Type.C3P0);
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
			log.warn("pool new " + type);
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



