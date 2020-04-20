package com.walker.core.mode;

import com.walker.common.util.Tools;
import org.junit.Test;

import static org.junit.Assert.*;

public class NodeListTest {
    int size = 6;

    @Test
    public void setLine() {

        NodeList<String, Weight> list = new NodeList<>();
        String now = "";
        list.add("n0");
        for(int i = 1; i < size; i++){
            now = "n" + i;
            list.add(now);
            list.setLine(list.get(i - 1), now, new Weight().setNum(i).setValue(i));
        }
        Tools.out(list.toString());
        Tools.out(list.getLineN(0, 1, null));
        Tools.out(list.getLineN(1, 2, null));
        Tools.out(list.getLinesN(0, 2, null));
        Tools.out(list.getLines(null, null, null));
        Tools.out(list.getLines("n1", null, null));
        Tools.out(list.getLines(null, "n2", null));

        list.reverse();
        Tools.out(list.toString());
        Tools.out(list.getLineN(0, 1, null));
        Tools.out(list.getLineN(1, 2, null));
        Tools.out(list.getLinesN(0, 2, null));
        Tools.out(list.getLines(null, null, null));
        Tools.out(list.getLines("n1", null, null));
        Tools.out(list.getLines(null, "n3", null));







    }
}