package com.walker.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

/**
 * Http tools for: http get http post with encode stream userAgent
 * 
 *
 */
public class HttpUtil {
	private static Logger log = Logger.getLogger("Http");
	
	private final static int DEFAULT_BUFFER = 4096;
	private final static String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
	private final static String DEFAULT_ENCODE = "utf-8";

	public static String encode(String str) throws Exception {
		return URLEncoder.encode(str, DEFAULT_ENCODE).replaceAll("\\+", "%20");
	}

	public static String get(String url, Map<?, ?> data) throws Exception {
		url = url + "?";
		StringBuilder ddd = new StringBuilder();
		for (Object key : data.keySet()) {
			String str = data.get(key) == null ? "null" : data.get(key).toString();
			ddd.append(key).append("=").append(encode(str)).append("&");
		}
		if(ddd.length() > 0){
			ddd.setLength(ddd.length() - 1);
		}
		// ddd = encode(ddd);
		// ddd = URLEncoder.encode(ddd);
		url = url + ddd.toString();
		return get(url, DEFAULT_ENCODE);
	}

	public static String get(String url) throws Exception {
		return get(url, DEFAULT_ENCODE);
	}

	public static String post(String url, List<?> listBean) throws Exception {
		return post(url, listBean, DEFAULT_ENCODE, null);
	}

	public static String post(String url, Map<?, ?> bean) throws Exception {
		return post(url, bean, DEFAULT_ENCODE, null);
	}

	public static String post(String url, String data) throws Exception {
		return post(url, data, DEFAULT_ENCODE, null);
	}

	public static String post(String url, Map<?, ?> bean, String encode, String userAgent) throws Exception {
		return post(url, JsonUtil.makeJson(bean), encode, userAgent);
	}

	public static String post(String url, List<?> bean, String encode, String userAgent) throws Exception {
		return post(url, JsonUtil.makeJson(bean), encode, userAgent);
	}

	/**
	 * 解析httpResponse的数据体
	 */
	private static String getResponseData(HttpResponse response, String charset) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			is = response.getEntity().getContent();
			IOUtils.copy(is, out);
			return new String(out.toByteArray(), charset);
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(is);
		}
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
	public static String post(String url, String data, String encode, String userAgent) throws Exception {
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

	public static String get(String url, String encode) throws Exception {
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

	public static void download(String url, String saveFilePath) throws Exception {
		File file = new File(saveFilePath);
		if(file.isDirectory()) {
			throw new Exception("save to dir?");
		}else {
			if(file.exists()) {
				file.delete();
			}
			file.createNewFile();
		}
		download(url, file);
	}
	/**
	 * 下载文件 统计耗时
	 * @param url
	 * @param file
	 * @throws Exception
	 */
	public static void download(String url, File file) throws Exception {
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
