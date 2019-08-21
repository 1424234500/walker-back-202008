package com.walker.common.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class PcTest {

    @Test
    public void getRuntime() {
       Tools.out( Pc.getRuntime());
    }

    @Test
    public void ping() {
        Tools.out(Pc.ping("127.0.0.1", 200));
    }

    @Test
    public void telnet() {
        Tools.out(Pc.telnet("127.0.0.1", 6379, 200));
    }
}