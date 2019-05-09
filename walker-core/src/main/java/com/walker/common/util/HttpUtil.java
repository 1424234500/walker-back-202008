package com.walker.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.*; 
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.protocol.*;
import org.apache.log4j.Logger;

import com.walker.core.exception.InfoException;

/**
 * Http工具类
 * 
 * 模拟浏览器header
 * encode 编码 解码
 * userAgent	身份
 * connect time	超时
 * 
 * 支持restful接口访问
 * get
 * post
 * put
 * delete
 * 
 * 文件下载url
 * 
 *
 *4xx状态码表示客户端错误，主要有下面几种。
400 Bad Request：服务器不理解客户端的请求，未做任何处理。
401 Unauthorized：用户未提供身份验证凭据，或者没有通过身份验证。
403 Forbidden：用户通过了身份验证，但是不具有访问资源所需的权限。
404 Not Found：所请求的资源不存在，或不可用。
405 Method Not Allowed：用户已经通过身份验证，但是所用的 HTTP 方法不在他的权限之内。
410 Gone：所请求的资源已从这个地址转移，不再可用。
415 Unsupported Media Type：客户端要求的返回格式不支持。比如，API 只能返回 JSON 格式，但是客户端要求返回 XML 格式。
422 Unprocessable Entity ：客户端上传的附件无法处理，导致请求失败。
429 Too Many Requests：客户端的请求次数超过限额。
500 Internal Server Error：客户端请求有效，服务器处理时发生了意外。
503 Service Unavailable：服务器无法处理请求，一般用于网站维护状态。
 *
 */
public class HttpUtil {
	private static Logger log = Logger.getLogger("Http");
	
	private final static int DEFAULT_BUFFER = 4096;
	private final static String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
	private final static String DEFAULT_ENCODE = "utf-8";

	/**
	 * 参数编码
	 * @param str
	 * @return
	 */
	public static String argsEncode(String str, String encode){
		try {
			return URLEncoder.encode(str, encode).replaceAll("\\+", "%20");
		}catch(UnsupportedEncodingException e) {
			throw new InfoException(str, e.getMessage(), e);
		}
	}

	public static String makeUrl(String url, Map<?, ?> data, String encode){
		url = url + "?";
		StringBuilder ddd = new StringBuilder();
		for (Object key : data.keySet()) {
			ddd.append(key).append("=").append(argsEncode(String.valueOf(data.get(key)), encode)).append("&");
		}
		if(ddd.length() > 0){
			ddd.setLength(ddd.length() - 1);
		}
		url = url + ddd.toString();
		return url;
	} 
	
	/**
	 * 创建新的浏览器端httpClient
	 */
	private static HttpClient makeHttpClient(){
		//创造HttpClient浏览器端
//		BasicHttpParams httpParameters = new BasicHttpParams();
//		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);// 连接超时
//		HttpConnectionParams.setSoTimeout(httpParameters, 300000);//
//		HttpClient client = new DefaultHttpClient(httpParameters);
//		client.getParams().setIntParameter("http.socket.timeout", 15000);
		
		HttpClient client = HttpClientBuilder.create()
				.addInterceptorFirst(
				new HttpRequestInterceptor() {
					@Override
					public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
						
					}}
				).build();

		return client;
	}
	
	/**
	 * httpClient连接池
	 */
	private static Map<String, HttpClient> mapClient;
	static{
		mapClient = new HashMap<String, HttpClient>();
	}
	/**
	 * 获取一个浏览器端httpClient 单例 或者 连接池控制
	 * 
	 * @param name null or '' 则每次返回一个新浏览器 否则缓存返回历史的同名浏览器
	 */
	public static HttpClient getClient(String name){
		HttpClient client = null;
		if(name == null || name.length() == 0){
			client = makeHttpClient();
		}else if(mapClient.containsKey(name)){
			client = (HttpClient) mapClient.get(name);
		}else{
			client = makeHttpClient();
			mapClient.put(name, client); //加入缓存
		}
	
		return client;
	}
	/**
	 * 获取新的浏览器httpClient
	 * @return
	 */
	public static HttpClient getClient(){
		return getClient("");
	}
	
	public static String post(String url, String data, String encode, String userAgent) {
		//默认header 编码处理
		userAgent = userAgent == null || userAgent.length() == 0 ? DEFAULT_USER_AGENT : userAgent;
		encode = encode == null || encode.length() == 0 ? DEFAULT_ENCODE : encode;

		log.info(Arrays.toString(new String[] { "dopost", url, data, encode, userAgent }));

		HttpClient client = getClient();

		//创造post请求 参数
		HttpPost httpPost = new HttpPost(new URI(url));
		httpPost.setEntity(new StringEntity(data, encode));
		httpPost.setHeader("User-Agent", userAgent);
		httpPost.setHeader("Keep-Alive", "15000");
		HttpResponse response = null;
		try{
			response = client.execute(httpPost);
		}catch(Exception e){
			log.info("post error " + e.toString());
			throw e;
		}
		String res = "";
		try{
			res = getResponseData(response, encode);	
		}catch(Exception e){
			log.info("parse error " + e.toString());
			throw e;
		}
		
		return res;
	}

	public static String get(String url, String encode) {
		//默认header 编码处理
		encode = encode == null || encode.length() == 0 ? DEFAULT_ENCODE : encode;
		
		log.info(Arrays.toString(new String[] { "doget", url, encode }));
		HttpClient client = getClient();
		HttpGet httpGet = new HttpGet(new URI(url));
		
		HttpResponse response = null;
		try{
			response = client.execute(httpGet);
		}catch(Exception e){
			log.info("get error " + e.toString());
			throw e;
		}
		String res = "";
		try{
			res = getResponseData(response, encode);	
		}catch(Exception e){
			log.info("parse error " + e.toString());
			throw e;
		}
		
		return res;
	}

	public static void download(String url, String saveFilePath) {
		File file = new File(saveFilePath);
		if(file.isDirectory()) {
			throw new InfoException("save to dir?");
		}else {
			if(file.exists()) {
				file.delete();
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				throw new InfoException(e.getMessage());
			}
		}
		download(url, file);
	}
	/**
	 * 下载文件 统计耗时
	 * @param url
	 * @param file
	 * @throws Exception
	 */
	public static void download(String url, File file) {
		StopWatch sw = new StopWatch();
		sw.start();
		log.warn(Arrays.toString(new String[] {"download", url, file.getAbsolutePath()}));
		long length = 0;
		
		FileOutputStream op = null;
		InputStream is = null;
		try {
			op = new FileOutputStream(file);
			URL ur = new URL(url);
			is = ur.openStream();
			int flushSize = 1024 * 1024 * 10;
			int count = 0;
			int size = 0;
			byte[] buffer = new byte[DEFAULT_BUFFER];
			while((size = is.read(buffer)) != -1) {
				op.write(buffer, 0, size);
				count += size;
				length += size;
				if(count > flushSize) {
					op.flush();
					count = 0;
				}
			}
			op.flush();
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if(op != null) {
				op.flush();
				op.close();
			}
			if(is != null) {
				is.close();
			}
			sw.split();
			log.warn(Arrays.toString(new String[] {"download", url, file.getAbsolutePath(), "size", Tools.calcSize(length), "cost", sw.toSplitString()}));
		}
	}
	
	
	
	
	
}
