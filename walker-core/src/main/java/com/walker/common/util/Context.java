package com.walker.common.util;

import com.walker.core.cache.CacheMgr;
import com.walker.core.exception.ErrorException;

import java.io.File;
import java.net.URL;

/**
 * 运行上下文 
 * 
 * 
 * 常量配置池
 * @author walker
 *
 */
public class Context {
	
	public final static String YES = "1";
	public final static String NO = "0";
	public static String valueFlag(String str) {
		if(str.equals(YES)) {
			return YES;
		}
		return NO;
	}
	static {
		//此时尚未加载log4j配置
		System.out.println("Thread.currentThread().getContextClassLoader().getResource(\"\") \t" + Thread.currentThread().getContextClassLoader().getResource("")); 
		System.out.println("ClassLoader.getSystemResource(\"\") \t"+ ClassLoader.getSystemResource("")); 

		System.out.println("Context.class.getClassLoader().getResource(\"\") \t"+ Context.class.getClassLoader().getResource("")); 
		System.out.println("Context.class.getResource(\"\") \t"+ Context.class.getResource("")); 
		System.out.println("Context.class.getResource(\"/\") \t"+ Context.class.getResource("/")); 
        
		System.out.println("new File(\"\").getAbsolutePath() \t" + new File("").getAbsolutePath()); 
		System.out.println("System.getProperty(\"user.dir\") \t" + System.getProperty("user.dir"));
		System.out.println(" System.getProperty(\"java.class.path\") \t" +  System.getProperty("java.class.path") );
//        System.out.println("getClass().getProtectionDomain().getCodeSource().getLocation() \t"+ Context.class.getClass().getProtectionDomain().getCodeSource().getLocation() );


		System.out.println("----------------------------");
		System.out.println("----root---" + getPathRoot());
		System.out.println("----conf---" + getPathConf());
		System.out.println("----------------------------");

	}

	/*
Tomcat web
Thread.currentThread().getContextClassLoader().getResource("") 	file:/home/walker/e/workspace_my/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/walker-web/WEB-INF/classes/
ClassLoader.getSystemResource("") 	null
Context.class.getClassLoader().getResource("") 	file:/home/walker/e/workspace_my/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/walker-web/WEB-INF/classes/
Context.class.getResource("") 	jar:file:/home/walker/e/workspace_my/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/walker-web/WEB-INF/lib/walker-core-0.0.1.jar!/com/walker/common/util/
Context.class.getResource("/") 	file:/home/walker/e/workspace_my/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/walker-web/WEB-INF/classes/
new File("").getAbsolutePath() 	/home/walker/e/help_note/shell
System.getProperty("user.dir") 	/home/walker/e/help_note/shell
 System.getProperty("java.class.path") 	/home/walker/software/apache-tomcat-8.5.40/bin/bootstrap.jar:/home/walker/software/apache-tomcat-8.5.40/bin/tomcat-juli.jar:/home/walker/software/jdk1.8.0_211/lib/tools.jar

java jar
Thread.currentThread().getContextClassLoader().getResource("") 	file:/home/walker/e/workspace_my/walker/walker-socket/target/classes/
ClassLoader.getSystemResource("") 	file:/home/walker/e/workspace_my/walker/walker-socket/target/classes/
Context.class.getClassLoader().getResource("") 	file:/home/walker/e/workspace_my/walker/walker-socket/target/classes/
Context.class.getResource("") 	file:/home/walker/e/workspace_my/walker/walker-core/target/classes/com/walker/common/util/
Context.class.getResource("/") 	file:/home/walker/e/workspace_my/walker/walker-socket/target/classes/
new File("").getAbsolutePath() 	/home/walker/e/workspace_my/walker/walker-socket
System.getProperty("user.dir") 	/home/walker/e/workspace_my/walker/walker-socket
 System.getProperty("java.class.path") 	/home/walker/e/workspace_my/walker/walker-socket/target/classes:/home/walker/e/workspace_my/walker/walker-core/target/classes:/home/walker/.m2/repository/net/sf/ehcache/ehcache/2.10.0/ehcache-2.10.0.jar:/home/walker/.m2/repository/net/sourceforge/jwbf/3.0.0/jwbf-3.0.0.jar:/home/walker/.m2/repository/org/jdom/jdom2/2.0.5/jdom2-2.0.5.jar:/home/walker/.m2/repository/com/fasterxml/jackson/core/jackson-core/2.4.3/jackson-core-2.4.3.jar:/home/walker/.m2/repository/com/fasterxml/jackson/core/jackson-databind/2.4.3/jackson-databind-2.4.3.jar:/home/walker/.m2/repository/com/fasterxml/jackson/core/jackson-annotations/2.4.0/jackson-annotations-2.4.0.jar:/home/walker/.m2/repository/org/apache/httpcomponents/httpclient/4.3.4/httpclient-4.3.4.jar:/home/walker/.m2/repository/org/apache/httpcomponents/httpcore/4.3.2/httpcore-4.3.2.jar:/home/walker/.m2/repository/commons-logging/commons-logging/1.1.3/commons-logging-1.1.3.jar:/home/walker/.m2/repository/org/apache/httpcomponents/fluent-hc/4.3.4/fluent-hc-4.3.4.jar:/home/walker/.m2/repository/org/apache/httpcomponents/httpmime/4.3.4/httpmime-4.3.4.jar:/home/walker/.m2/repository/javax/inject/javax.inject/1/javax.inject-1.jar:/home/walker/.m2/repository/com/google/guava/guava/18.0/guava-18.0.jar:/home/walker/.m2/repository/com/google/code/findbugs/jsr305/3.0.0/jsr305-3.0.0.jar:/home/walker/.m2/repository/redis/clients/jedis/3.0.0/jedis-3.0.0.jar:/home/walker/.m2/repository/org/apache/commons/commons-pool2/2.4.3/commons-pool2-2.4.3.jar:/home/walker/.m2/repository/mysql/mysql-connector-java/5.1.38/mysql-connector-java-5.1.38.jar:/home/walker/.m2/repository/com/mchange/c3p0/0.9.5.4/c3p0-0.9.5.4.jar:/home/walker/.m2/repository/com/mchange/mchange-commons-java/0.2.15/mchange-commons-java-0.2.15.jar:/home/walker/.m2/repository/org/quartz-scheduler/quartz/2.3.0/quartz-2.3.0.jar:/home/walker/.m2/repository/com/zaxxer/HikariCP-java6/2.3.13/HikariCP-java6-2.3.13.jar:/home/walker/.m2/repository/com/alibaba/dubbo/2.5.3/dubbo-2.5.3.jar:/home/walker/.m2/repository/org/javassist/javassist/3.15.0-GA/javassist-3.15.0-GA.jar:/home/walker/.m2/repository/org/jboss/netty/netty/3.2.5.Final/netty-3.2.5.Final.jar:/home/walker/.m2/repository/org/glassfish/main/javaee-api/javax.jws/3.1.2.2/javax.jws-3.1.2.2.jar:/home/walker/.m2/repository/com/belerweb/pinyin4j/2.5.0/pinyin4j-2.5.0.jar:/home/walker/.m2/repository/org/apache/poi/poi/3.9/poi-3.9.jar:/home/walker/.m2/repository/commons-codec/commons-codec/1.5/commons-codec-1.5.jar:/home/walker/.m2/repository/org/apache/poi/poi-ooxml/3.8/poi-ooxml-3.8.jar:/home/walker/.m2/repository/org/apache/poi/poi-ooxml-schemas/3.8/poi-ooxml-schemas-3.8.jar:/home/walker/.m2/repository/org/apache/xmlbeans/xmlbeans/2.3.0/xmlbeans-2.3.0.jar:/home/walker/.m2/repository/stax/stax-api/1.0.1/stax-api-1.0.1.jar:/home/walker/.m2/repository/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:/home/walker/.m2/repository/xml-apis/xml-apis/1.0.b2/xml-apis-1.0.b2.jar:/home/walker/.m2/repository/io/netty/netty-all/4.1.24.Final/netty-all-4.1.24.Final.jar:/home/walker/.m2/repository/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar:/home/walker/.m2/repository/junit/junit/4.0/junit-4.0.jar:/home/walker/.m2/repository/log4j/log4j/1.2.14/log4j-1.2.14.jar:/home/walker/.m2/repository/org/slf4j/slf4j-log4j12/1.7.2/slf4j-log4j12-1.7.2.jar:/home/walker/.m2/repository/org/json/json/20160810/json-20160810.jar:/home/walker/.m2/repository/org/apache/directory/studio/org.apache.commons.io/2.4/org.apache.commons.io-2.4.jar:/home/walker/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar:/home/walker/.m2/repository/org/apache/commons/commons-lang3/3.8/commons-lang3-3.8.jar:/home/walker/software/eclipse/configuration/org.eclipse.osgi/400/0/.cp/lib/javaagent-shaded.jar


	*/	
	/**
	 * 文件路径
	 * @return
	 */
	public static String getPathRoot() {
//        return new File("").getAbsolutePath(); 
		URL url = Context.class.getResource("/");///walker/walker-socket/target/classes/
		if(url != null) {
			File f = new File(url.getFile());
			try {
				f = new File(f.getParent());
				f = new File(f.getParent());
				Tools.out("proj path", f.getAbsolutePath());
			}catch (Exception e) {
				throw new ErrorException("项目根路径异常", url);
			}
			return f.getAbsolutePath();
		}else {
			return new File("").getAbsolutePath();
		}
	}
	/**
	 * 根路径root
	 * @param file
	 * @return
	 */
	public static String getPathRoot(String file) {
		return getPathRoot() + File.separator + file;
	}
	/**
	 * 配置文件路径
	 */
	public static String getPathConf() {
//		web项目需要配置于WEB-INF/classes 	spring.xml	 web.xml寻址classpath:   ? 
//		System.setProperty("path_conf", "conf");
		String res = "";
		URL url = Context.class.getResource("/");
		if(url != null) {
			String root = url.getPath();
			if(root.contains("WEB-INF")) {
				res = root;
			}
		}
		if(res.length() == 0) {
			res = getPathRoot(getConfName());
		}
//		/walker/walker-socket/target/classes/
//		/home/walker/e/workspace_my/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/walker-web/WEB-INF/classes/
		return res;
	}
	public static void setConfName(String name){
		System.setProperty("path_conf", name);
	}
	public static String getConfName(){
		return System.getProperty("path_conf", "conf");
	}
	public static String getPathConf(String file) {
		return getPathConf() + File.separator + file;
	}

	
	
	
	public static String beginTip(Class<?> clz) {
		return beginTip(clz.getName());
	}	
	public static String beginTip(String tip) {
		return tip + " begin";
	}
	public static String endTip(Class<?> clz) {
		return endTip(clz.getName());
	}	
	public static String endTip(String tip) {
		return tip + " end";
	}
	
	public static String okTip(Class<?> clz) {
		return okTip(clz.getName());
	}	
	public static String okTip(String tip) {
		return tip + " ok";
	}

	public static String errorTip(Class<?> clz) {
		return errorTip(clz.getName());
	}	
	public static String errorTip(String tip) {
		return tip + " error";
	}
	
	
	

	/**
	 * 默认数据库操作一次大小
	 */
	public static int getDbOnce() {
		return CacheMgr.getInstance().get("DB_ONCE", 500);
	}
	/**
	 * 默认分页每页数量
	 */
	public static int getShowNum() {
		return CacheMgr.getInstance().get("SHOW_NUM", 5);
	}


}
