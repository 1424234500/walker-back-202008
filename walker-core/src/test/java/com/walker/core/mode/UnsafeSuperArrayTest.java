package com.walker.core.mode;

import com.walker.common.util.Tools;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnsafeSuperArrayTest {

    @Test
    public void set() {
        UnsafeSuperArray array = new UnsafeSuperArray( (long)Integer.MAX_VALUE * 2 );
        System.out.println("Array size:" + array.size()); // 4294967294
        int sum=0;
        for (int i = 0; i < 100; i++) {
            array.set((long)Integer.MAX_VALUE + i, (byte)3);
            sum += array.get((long)Integer.MAX_VALUE + i);
        }
        System.out.println(sum);
        array.set(Integer.MAX_VALUE + 998, (byte)6);

        int res = array.get(Integer.MAX_VALUE + 998);
        Tools.out("get", res, res == 6);
        Assert.assertTrue(res == 6);

    }

    @Test
    public void get() {
    }

    @Test
    public void size() {
    }
}