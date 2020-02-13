package com.walker;

import com.walker.common.util.TimeUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
    public static int size = 8;


    static{



    }
    public void out(Object...objects){
        System.out.println(TimeUtil.getTimeYmdHmss() + ": " + Arrays.toString(objects));
    }
    @Test
    public void test(){
        out("hello test" );
    }


}
