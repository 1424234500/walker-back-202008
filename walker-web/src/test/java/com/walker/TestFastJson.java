package com.walker;

import com.alibaba.fastjson.JSON;
import com.walker.mode.Student;
import org.junit.Test;

public class TestFastJson {


    @Test
    public void test(){
        Student stu = new Student();
        stu.setId("id");
        stu.setName("name");
        stu.setTime("time");
        Response res = Response.makeTrue("info", stu);
        String str = JSON.toJSONString(res);
        System.out.println(str);
        Response tt = JSON.parseObject(str, Response.class);
        System.out.println(tt);

    }

}
