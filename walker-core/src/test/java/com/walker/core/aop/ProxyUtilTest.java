package com.walker.core.aop;

import com.walker.common.util.Tools;
import com.walker.core.pipe.Pipe;
import com.walker.core.pipe.PipeListImpl;
import com.walker.core.pipe.PipeMgr;
import com.walker.core.route.SubPub;
import com.walker.core.route.SubPubMapImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProxyUtilTest {

    @Test
    public void getProxy() {
        int dep = 3;
//        Pipe<String> pipeListImpl = PipeMgr.getPipe(PipeMgr.Type.PIPE, "");
        SubPub<String, String> pipeListImpl = new SubPubMapImpl<>();
        pipeListImpl.init(1);
        for(int i = 0; i < dep; i++){
            pipeListImpl.publish("ch", "key" + i);
        }

        Tools.out("-----------------");
        SubPub<String, String> pipeListImplProxy = ProxyUtil.getProxy(pipeListImpl);
        for(int i = 0; i < dep; i++){
            pipeListImplProxy.publish("ch", "key" + i);
            pipeListImplProxy.unSubscribe("ch", null);
        }

    }
}