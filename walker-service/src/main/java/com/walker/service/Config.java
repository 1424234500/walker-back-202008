package com.walker.service;

import com.walker.common.util.Page;
import com.walker.common.util.Tools;

public class Config {

    private static int count = 0;
    public static String PRE = "init--------";
    public static String getPre(){
        return "#" + Tools.fillStringBy(count++ + ".", " ", 4, 1) + PRE ;
    }


    public static final String TRUE = "1";
    public static final String FALSE = "0";


    public static String getUploadDir() {
        return "/home/walker/files";
    }
    public static String getDownloadDir() {
        return "/home";
    }

    public static int getDbsize(){
        return 500;
    }


    public static String makePrefix(String prefix, String ID) {
        if(ID != null && ID.length() > 0 && !ID.startsWith(prefix)){
            ID = prefix + ID;
        }
        return ID;
    }


}
