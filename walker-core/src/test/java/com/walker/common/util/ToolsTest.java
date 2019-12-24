package com.walker.common.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class ToolsTest {

    @Test
    public void isOn() {

        for(int a1 = 0; a1 < 9; a1++){
            for(int a2 = a1+1; a2 < 9; a2++){
                for(int b1 = 0; b1 < 9; b1++){
                    for(int b2 = b1 + 1; b2 < 9; b2++){
                        boolean res = Tools.isOn(a1, a2, b1, b2);
//                        if(res)
                        Tools.out(a1, a2, b1, b2, res);

                    }

                }

            }

        }


    }
}