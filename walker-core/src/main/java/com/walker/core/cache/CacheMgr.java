package com.walker.core.cache;

import com.walker.common.setting.SettingUtil;
import com.walker.common.util.Bean;
import com.walker.common.util.Context;
import com.walker.common.util.FileUtil;
import com.walker.core.aop.TestAdapter;
import com.walker.core.database.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 缓存构造 构造完毕后并初始化缓存 
 * 单例并发交由具体实现控制 
 * 此处不做单例控制
 *
 */
public class CacheMgr extends TestAdapter{
	private static Logger log = LoggerFactory.getLogger("Cache");
	static public Type DEFAULT_TYPE = Type.MAP;
	
	public CacheMgr() {
	}
    private static class SingletonFactory{           
        private static Cache<String> cache;
        static {
        	log.warn("singleton instance construct " + SingletonFactory.class);
			cache = getInstance(Type.MAP);
			reload(cache);
        }
    }
    
    
	public static Cache<String> getInstance() {
		return SingletonFactory.cache;
	}

	public static Cache<String> getInstance(Type type) {
		Cache<String> cache = null;
		switch (type) {
		case MAP:
			cache = new CacheMapImpl();
			break;
		case EHCACHE:
			cache = new CacheEhcacheImpl();
			break;
		case REDIS:
			cache = new CacheRedisImpl();
			break;
		}
		return cache;
	}
	
	@Override
	public boolean doInit() {
		return doTest();
	}

	/**
	 * 初始化cache 系统级数据 环境设置读取 词典加载 额外配置项
	 * 1.加载配置文件
	 * 2.加载数据库 
	 */
	public static void reload(Cache<String> cache){
		log.warn("初始化缓存");
		cache.put("cache:dir:project", Context.getPathRoot());
		cache.put("cache:dir:conf", Context.getPathConf());
		String[] dd  = {new File("").getAbsolutePath(), Context.class.getResource("/").getPath(), new File("").getAbsolutePath() + File.separator + Context.getConfName()};
		List<String> fff = new ArrayList<>();
		int k = 0;
		for(int i = 0; i < dd.length ; i++) {
			File dir = new File(dd[i]);
			if(!dir.isDirectory()){
				log.warn(i + "\t配置加载路径不存在 跳过 " + dd[i]);
				continue;
			}
			log.warn(i + "\t配置加载路径 " + dd[i]);
			for (File item : dir.listFiles()) {
				String path = item.getAbsolutePath();
				if (FileUtil.check(path) == 0 && path.endsWith(".properties")) {
					log.warn(k++ + "\t解析文件存入cache " + path);
					cache.putAll(SettingUtil.getSetting(path));
					fff.add(path);
				}else{
					log.warn(k++ + "\t解析文件存入cache 不匹配 跳过 " + path);
				}
			}
		}
		cache.put("files", fff);

		//initOracle(cache);
		log.warn("初始化完毕------------------ ");

	}
	@SuppressWarnings("unused")
	private static void initOracle(Cache<String> cache) {
		try {
			Dao dao = new Dao();
			//key value info time
			for(Map<String, Object> keyValue : dao.find("select * from sys_config")){
				String key = (String) keyValue.get("KEY");
				Object value = keyValue.get("VALUE");
				cache.put(key, value);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public boolean doTest() {
		Cache<String> cache = getInstance();
		if(cache == null){
			return false;
		}
		
		List<Object> list = new ArrayList<>();
		list.add("string item");
		list.add(1111111);
		list.add(new Bean().put("key of list map", "aldkjfakljf").put("keyint", 2222));
		Bean map = new Bean().put("key1", 111).put("key2", 222);
		Bean bean = new Bean().put("key1", 111).put("key2", map);
		Bean bean2 = new Bean().put("key1", 333).put("key2", map).put("key3", bean);
		list.add(bean);
		list.add(bean2);
		list.add(map);
		
		cache.put("int", 1);
		cache.put("long", 998);
		cache.put("string", "the is a string");
		cache.put("map", 
			new Bean().put("key-int", 2)
				.put("key-map", new Bean().put("key-map-key-int", 3))
				.put("key-list", list)
				.put("list", list)
				.put("map1", map)
				.put("map2", bean)
				.put("map3", bean2)
				);
		cache.put("list", list);
		
		return cache.get("int", 0) == 1;
	}

	public enum Type {
		MAP, EHCACHE, REDIS,
	}


}


