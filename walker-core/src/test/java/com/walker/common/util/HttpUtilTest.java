package com.walker.common.util;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.walker.common.util.HttpBuilder.Type;
import com.walker.core.exception.InfoException;

public class HttpUtilTest {

	@Test
	public void test() throws UnsupportedEncodingException, InfoException {
		String[] urls = {"https://mvnrepository.com/search?q=wsspi"
			, "https://www.meituan.com/ptapi/getprovincecityinfo"
		};
		for(String url : urls) {
			try {
				String str = new HttpBuilder(url, Type.GET)
						.setHeaders(new Bean()
//								.put("Pragma", "no-cache")
//								.put("Connection", "")
//								.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
//								.put("Accept-Encoding", "gzip, deflate, br")
//								.put("Accept-Language", "en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7,da;q=0.6,zh-TW;q=0.5")
//								.put("Cookie", "uuid=f7e6e60cabec4f1b9ed1.1573733693.1.0.0; _lx_utm=utm_source%3DBaidu%26utm_medium%3Dorganic; _lxsdk_cuid=16e69d6842dc8-0757f86b2b77cd-18211c0a-100200-16e69d6842dc8; ci=60; rvct=60")
//								.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
//								.put("Upgrade-Insecure-Requests", 1)
								.put("aaa", "bbb")
						)
						.buildString();
			} catch (Exception e) {

			}
		}
	}

}
