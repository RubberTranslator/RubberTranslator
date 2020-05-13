package com.rubbertranslator.modules.log;


import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/8 10:49
 */
public class LoggerManager {
    public static void configLog() {
        try {
            InputStream input = LoggerManager.class
                    .getResourceAsStream("/log/log.properties");
            LogManager.getLogManager().readConfiguration(input);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(LoggerManager.class.getName()).log(Level.SEVERE, "日志属性加载出错", e);
        }
    }

    public static void setLogLevel(String name, Level level) {
        Logger.getLogger(name).setLevel(level);
    }

}
