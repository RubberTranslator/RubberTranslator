package com.rubbertranslator.utils;

public class OSTypeUtil {
    public static boolean isWin(){
        return System.getProperty("os.name").toLowerCase().startsWith("win");
    }


    public static boolean isLinux(){
        return System.getProperty("os.name").toLowerCase().startsWith("linux");
    }


    public static boolean isMac(){
        return System.getProperty("os.name").toLowerCase().startsWith("mac");
    }

}
