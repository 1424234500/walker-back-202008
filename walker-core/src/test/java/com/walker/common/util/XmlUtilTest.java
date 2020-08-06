package com.walker.common.util;

import org.dom4j.DocumentException;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.*;

public class XmlUtilTest {

    @Test
    public void toFullXml() throws DocumentException, FileNotFoundException {
        String file = "/home/walker/test.xml";
        FileUtil.saveAs("<html href='www.baidu.com'> <img src='www.ddf.png' >aacb</img> </html>", file, false);

        Object bean = XmlUtil.parseElement(file);
//    	debug(JsonUtil.makeJson(bean, 0));
        debug(JsonUtil.makeJson(bean, 6));


        String path = ClassLoader.getSystemResource("").getPath() + "plugin.json";
        String str = FileUtil.readByLines(path, null, "utf-8");

        Bean bb = JsonUtil.get(str);
        String s = XmlUtil.turnElement(bb);
        debug(s);

    }
    private static void debug(Object...objects) {
        Tools.out(objects);
    }
}