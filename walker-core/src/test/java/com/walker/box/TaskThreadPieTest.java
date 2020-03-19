package com.walker.box;

import com.walker.common.util.ThreadUtil;
import com.walker.common.util.Tools;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class TaskThreadPieTest {

    @Test
    public void start() {


        List<String> ll = new ArrayList<>();
        for(int i = 0; i < 9999; i++){
            ll.add("t" + i);
        }
        Iterator<String> it = ll.iterator();
        new TaskThreadPie(ll.size()){

            @Override
            void onStartThread(int threadNo) {

                String task = ll.get(threadNo);
                Tools.out(threadNo, "run", task);

                ThreadUtil.sleep(10);
            }
        }
        .setDetail(false)
        .setSleepTimeSch(2000)
        .setThreadSize(3)
        .start();
    }
    @Test
    public void start2() {

        List<String> ll = new ArrayList<>();
        for(int i = 0; i < 9999; i++){
            ll.add("t" + i);
        }
        Iterator<String> it = ll.iterator();
        new TaskThreadPie(ll.size()){

            @Override
            void onStartThread(int threadNo) {

//                String task = ll.get(threadNo);
                if(! it.hasNext()){
                    Tools.out("it not has next", threadNo);
                    return;
                }
                String task = it.next();

                Tools.out(threadNo, "run", task);

                ThreadUtil.sleep(10);
            }
        }
        .setDetail(false)
        .setSleepTimeSch(2000)
        .setThreadSize(3)
        .start();
    }

}