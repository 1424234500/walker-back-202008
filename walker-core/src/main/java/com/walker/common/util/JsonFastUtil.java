package com.walker.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * fastjson 对象 序列化存取
 */
public class JsonFastUtil {


    public static <T> String toString(T obj){
        return JSON.toJSONString(obj);
    }
    public static <T> T get(String str){
        return JSON.parseObject(str, new TypeReference<T>(){});
    }
    public static <T> T get(String str, TypeReference<T> t){
        return JSON.parseObject(str, t);
    }
    public static <T> T get1(String str, TypeReference<T> t){
        return JSON.parseObject(str, new TypeReference<T>(){});
    }


    public static void main(String[] args) {


        Bean b = null;
        String beanStr = toString(new Bean().put("key", "kv").put("bean",new Bean().put("inner", "value")));
        Bean b2 =  get(beanStr, new TypeReference<Bean>(){});
        Tools.out(b2.getClass(), b2);

        Bean b3 =  JSON.parseObject(beanStr, new TypeReference<Bean>(){});
        Tools.out(b3.getClass(), b3);


        b =  JSON.parseObject(beanStr, Bean.class);
        Tools.out(b.getClass(), b);


    }



}
