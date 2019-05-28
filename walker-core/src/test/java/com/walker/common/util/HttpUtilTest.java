package com.walker.common.util;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.walker.common.util.HttpBuilder.Type;
import com.walker.core.exception.InfoException;

public class HttpUtilTest {

	@Test
	public void test() throws UnsupportedEncodingException, InfoException {
		try {
		String url = "https://mvnrepository.com/search?q=wsspi";
		String str = new HttpBuilder(url, Type.POST).buildString();
		}catch(Exception e) {
			
		}
	}

}
