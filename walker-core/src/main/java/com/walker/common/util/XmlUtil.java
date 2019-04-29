package com.walker.common.util;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.io.*;

import com.walker.core.cache.CacheMgr;

/**
 * xml工具类
 */
public class XmlUtil {
	protected static Logger log = Logger.getLogger(XmlUtil.class); 

	public static String toFullXml(Bean bean) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 解析某个节点 不包含root本身
	 * @param level	深度
	 * @param element	节点
	 * @return	该节点的属性 子节点列表
	 */
	@SuppressWarnings("unchecked")
	public static Object turnElement(int level, Element element) {
		String fill = Tools.fillStringBy("", " ", level * 4, 0);
		level ++;
		Object res = null;
		
		//获取 简单属性 节点的属性键值
		List<Attribute> attrs = element.attributes();
        List<Element> elements = element.elements();

        if(attrs.size() > 0 || elements.size() > 0) {
    		BeanLinked value = new BeanLinked();

    		for (Attribute attr : attrs) {
                debug(fill + "**attr " + attr.getName() + " : " + attr.getValue());
                value.put(attr.getName(), attr.getValue());
            }
        	
    		Map<String, List<Element>> index = new LinkedHashMap<String, List<Element>>();
            for (Element item  : elements) {
        		String key = item.getName();
        		List<Element> list = index.get(key);
        		if(list == null) {
        			list = new ArrayList<Element>();
        			index.put(key, list);
        		}
        		list.add(item);
            }
            
	        for(Entry<String, List<Element>> item : index.entrySet()) {
	        	String key = item.getKey();
	        	List<Element> list = item.getValue();
	        	if(list != null) {
	        		if(list.size() == 1) {
	            		debug(fill + "##node map " + key);
	        			value.put(key, turnElement(level, list.get(0)));
	        		}else {
	            		debug(fill + "##node list " + key + " " + list.size());
	        			List<Object> arr = new ArrayList<Object>();
	        			for(Element ele : list) {
	        				arr.add(turnElement(level, ele));
	        			}
	        			value.put(key, arr);
	        		}
	        	}
	        	res = value;
	        }
        }else {
        	res =  element.getTextTrim();
    		debug(fill + "##node str " + res );
        }
		
		
		
        //获取 对象属性 子节点列表
		//简单类型 字符串
		//item:1
		//<item>1</item>
		//
		//对象转换
		//item:{
		//		name: aaa
		//		value: bbb
		//}
		//<item name="aaa" value="bbb" />
		//<item>
		//	<name>aaa</name>
		//	<value>bbb</value>
		//</item>
		//
		//数组转换
		//item:[
		//	aaa,
		//	bbb
		//]
		//<item>a</item
		//<item>b</item>
		
		return res;
	}
	public static String turnElement(Object obj) {
		StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?> ");
		sb.append(turnElement(0, obj, ""));
		return sb.toString();
	}
	public static String turnElement(int level, Object obj, Object parentKey) {
		level = level < 0 ? 0 : level;
		String fill = Tools.fillStringBy("", " ", level * 8, 0);
		String fillNext = Tools.fillStringBy("", " ", (level+1)* 8, 0);
		
		level ++;
		StringBuilder sb = new StringBuilder();
		if(obj instanceof Map) {
			sb.append("\n");
			Map<?,?> map = (Map<?, ?>)obj;
			sb.append(fill).append("<").append(String.valueOf(parentKey)).append(">");
			sb.append("\n");
			for(Object key : map.keySet()) {
				Object value = map.get(key);	
				sb.append(fillNext).append(turnElement(level, value, key)).append("\n") ;
			}
			sb.append(fill).append("</").append(String.valueOf(parentKey)).append(">");
		}else if(obj instanceof List) {
			sb.append("\n");
			List<?> list = (List<?>)obj;
			for(Object item : list) {
				sb.append(fill).append(turnElement(level, item,parentKey)).append("\n");
			}
		}else {//		<key>xxxx</key>\n
			sb
			.append("<").append(String.valueOf(parentKey)).append(">")
			.append(String.valueOf(obj))
			.append("<").append(String.valueOf(parentKey)).append(">")
			;
		}
		String res = sb.toString();
		if(StringUtils.isBlank(res)) {
			Tools.out("aaaaaaaaaaaaaa");
		}
		return res.replace("\n\n\n", "\n\n");
	}
	private static void debug(Object...objects) {
		Tools.out(objects);
	}
	/**
	 * 解析某节点为完整的map 包含root
	 * @param element
	 * @return
	 */
	public static Object parseElement(Element element) {
//		return new BeanLinked().put(element.getName(), turnElement(0, element));
		return turnElement(0, element);
	}
	public static Object parseElement(File file) throws DocumentException {
		// 创建SAXReader的对象reader
        SAXReader reader = new SAXReader();
    	// 通过reader对象的read方法加载books.xml文件,获取docuemnt对象。
        Document document = reader.read(file);
        // 通过document对象获取根节点bookstore
        Element element = document.getRootElement();
        return parseElement(element);
	}	
	public static void saveConfig(Map <?,?>map, File file)  {
		FileUtil.saveAs(turnElement(map), file, false);
	}
	/**
	 * 解析文件
	 * @param filePath
	 * @return
	 * @throws DocumentException
	 */
	public static Object parseElement(String filePath) throws DocumentException {
        return parseElement(new File(filePath));
	}
	public static void saveConfig(Map <?,?>map, String filePath) throws DocumentException  {
        saveConfig(map, new File(filePath));
	}
	/**
	 * 解析当前项目路径配置文件
	 * @param fileName
	 * @return
	 * @throws DocumentException
	 */
	public static Object parseConfig(String fileName) throws DocumentException {
//        return parseElement(XmlUtil.class.getResource("/").getPath()+File.separator + fileName);
        return ClassLoader.getSystemResource("").getPath() + fileName;
	}

    public static void main(String[] args) throws DocumentException {
    	Object bean = parseConfig("test_temp.xml");
//    	debug(JsonUtil.makeJson(bean, 0));
    	debug(JsonUtil.makeJson(bean, 6));
    	
    	
    	String path = ClassLoader.getSystemResource("").getPath() + "plugin.json";
    	String str = FileUtil.readByLines(path, null, "utf-8");
    	log.warn("plugin mgr init file: " + path);
    	log.warn(str);
    	
		Bean bb = JsonUtil.get(str);
    	String s = turnElement(bb);
    	debug(s);
    	
    }
    
}
