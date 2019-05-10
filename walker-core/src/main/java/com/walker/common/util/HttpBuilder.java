package com.walker.common.util;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import com.walker.core.exception.InfoException;

/**
 * builder模式构建动态设置的http各种请求
 * @author walker
 *
 *
 */
public class HttpBuilder {
	private Type type;
	private String url;
	private Map<?,?> data;
	private String encode;
	private String decode;
	private Map<?,?> headers;
	
	private int  requestTimeout = 0;
	private int connectTimeout = 0;
	private int socketTimeout = 0;
	
	private HttpRequestBase request;
	private RequestConfig timeout;
	
	public Type getType() {
		return type;
	}
	public HttpBuilder setType(Type type) {
		this.type = type;
		return this;
	}
	public String getUrl() {
		return url;
	}
	public HttpBuilder setUrl(String url) {
		this.url = url;
		return this;
	}
	public Map<?, ?> getData() {
		return data;
	}
	public HttpBuilder setData(Map<?, ?> data) {
		this.data = data;
		return this;
	}
	public String getEncode() {
		return encode;
	}
	public HttpBuilder setEncode(String encode) {
		this.encode = encode;
		return this;
	}
	public String getDecode() {
		return encode;
	}
	public HttpBuilder setDecode(String decode) {
		this.decode = decode;
		return this;
	}
	public Map<?, ?> getHeaders() {
		return headers;
	}
	public HttpBuilder setHeaders(Map<?, ?> headers) {
		this.headers = headers;
		return this;
	}
	public int getRequestTimeout() {
		return requestTimeout;
	}
	public HttpBuilder setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
		return this;
	}
	public int getConnectTimeout() {
		return connectTimeout;
	}
	public HttpBuilder setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}
	public int getSocketTimeout() {
		return socketTimeout;
	}
	public HttpBuilder setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
		return this;
	}
	HttpBuilder(String url, Type type){
		setUrl(url);
		setType(type);
	}
	
	String buildString() throws UnsupportedEncodingException, InfoException  {
		makeData();
		timeout = HttpUtil.makeTimeoutConfig(requestTimeout, connectTimeout, socketTimeout);
		return HttpUtil.executeString(request, url, encode, decode, headers, timeout);
	}
	
	
	private void makeData() throws UnsupportedEncodingException {
		switch(this.type) {
			case DELETE:
				if(data != null && data.size() > 0)
					url = HttpUtil.makeUrl(url, data, encode);
				request = new HttpDelete();
				break;
			case PUT:
				HttpPut put = new HttpPut();
				if(data != null && data.size() > 0)
					put.setEntity(new StringEntity(JsonUtil.makeJson(data), encode));
				request = put;
				break;
			case POST:
				HttpPost post = new HttpPost();
				if(data != null && data.size() > 0)
					post.setEntity(new StringEntity(JsonUtil.makeJson(data), encode));
				request = post;
			break;
			case GET:
			default:
				if(data != null && data.size() > 0)
					url = HttpUtil.makeUrl(url, data, encode);
				request = new HttpGet();
		}
		
		if(decode == null || decode.length() == 0) {
			decode = encode;
		}
		
	}


	enum Type{
		PUT, GET, POST, DELETE,
	}
	
}
