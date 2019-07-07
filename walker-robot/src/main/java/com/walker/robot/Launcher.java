package com.walker.robot;

import org.apache.log4j.PropertyConfigurator;

import java.io.File;

import com.walker.common.util.Context;
import com.walker.common.util.FileUtil;
import com.walker.common.util.Tools;

public class Launcher {

    public static void main(String[] args) {
        new Launcher();
    }


    public Launcher() {
//		web项目需要配置于WEB-INF/classes 	spring.xml	 web.xml寻址classpath:   ?
        System.setProperty("path_conf", "conf");
        PropertyConfigurator.configure(Context.getPathConf("log4j.properties"));
        Tools.out("-----------------launcher-------------------");


        new Gui("Pc");


        Tools.out("-----------------end-------------------");
    }

}
