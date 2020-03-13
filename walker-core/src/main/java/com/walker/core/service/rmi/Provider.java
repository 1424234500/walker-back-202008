package com.walker.core.service.rmi;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.walker.common.util.ClassUtil;
import com.walker.common.util.Tools;
import com.walker.core.aop.TestAdapter;
import com.walker.core.cache.Cache;
import com.walker.core.cache.CacheMgr;
import com.walker.core.service.service.ServiceClass;


/**
 * Rmi 远程调用提供者 服务端 提供服务
 *
 */
public class Provider extends TestAdapter{
	private static Logger log = Logger.getLogger(Provider.class); 
	private static Map<String, Remote> map;
	static {
		map = new HashMap<>();
	}
	public boolean doInit() {
		log.info("********初始化远程调用 rmi*************");
		Cache<String> cache = CacheMgr.getInstance();
		int port = cache.get("port_rmi", 8091);
		String clzs = cache.get("on_list_service", "");
		String clzss[] = clzs.split(",");
		
		//bind rmi端口
        try {
			LocateRegistry.createRegistry(port);
		} catch (RemoteException e) {
			e.printStackTrace();
			return false;
		}

		for(int i = 0; i < clzss.length; i++){
			try{
				Remote obj = (Remote) ClassUtil.newInstance(clzss[i]);
				String name = obj.getClass().getSimpleName();
				if(name.endsWith("Impl")){
					name = name.substring(0, name.length() - "Impl".length());
				}
				String url = "rmi://localhost:" + port + "/" + name;	
				
				try{
		            Naming.rebind(url, obj);
					map.put(url, obj);
					log.info("###publish.ok." + i + " " + url);
				}catch(Exception e){
					e.printStackTrace();
					log.error("###publish.error." + i + " " + url + " " + e.toString());
				}
			}catch(Exception e){
				return false;
			}
		}  
    	log.info("********启动远程服务完成 rmi over*************");
    	
		log.info("--! 测试完毕 ------------------- ");
    	return true;
	}

	public boolean doTest(){
    	log.info("-- 开始测试WebService --------------");
		try {
			int port = CacheMgr.getInstance().get("port_rmi", 8091);
        	ServiceClass hello = (ServiceClass) Naming.lookup("rmi://localhost:"+ port + "/ServiceClass");
            Tools.out(hello.test("hello rmi"));
		} catch (Exception e) {
			e.printStackTrace();
			log.error("测试web service error !" + e.toString());
			return false;
		}
		return true;
	}
	
	
	public static String getUrl(){
		if(map.size() > 0){
			return map.keySet().iterator().next();
		}
		return "";
	}
	
	

}
