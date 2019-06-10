package com.walker.common.util;

import static org.junit.Assert.*;

import org.junit.Test;


public class JsonUtilTest {

	@Test
	public void test() {

		String str = "";
		

		
		str = "{ }";
		Tools.out("", JsonUtil.get(str));

		str = "{type:message,to:\"all_user\",from:222,data:{type:txt,body:hello} }";
		Tools.out("", JsonUtil.get(str));

		str = "{type:message,to:\"all_socket\",from:222,data:{type:txt,body:hello} }";
		Tools.out("", JsonUtil.get(str));

		str = "{type:monitor,data:{type:show} }";
		Tools.out("", JsonUtil.get(str));

		str = "{type:session  }";
		Tools.out("", JsonUtil.get(str));
		

		
	}

}
