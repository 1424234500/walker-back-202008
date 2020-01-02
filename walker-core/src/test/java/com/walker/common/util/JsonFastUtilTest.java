package com.walker.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class JsonFastUtilTest {

    @Test
    public void toString1() {


        Bean b = null;
        String beanStr = JsonFastUtil.toString(new Bean().put("key", "kv").put("bean",new Bean().put("inner", "value")));
        Bean b2 =  JsonFastUtil.get(beanStr, new TypeReference<Bean>(){});
        Tools.out(b2.getClass(), b2);

        Bean b3 =  JSON.parseObject(beanStr, new TypeReference<Bean>(){});
        Tools.out(b3.getClass(), b3);


        b =  JSON.parseObject(beanStr, Bean.class);
        Tools.out(b.getClass(), b);

        String str = "{'hello':'world'}";
        Map<String, String> map = JsonFastUtil.get(str);
        Tools.out(str, map);


    }

}