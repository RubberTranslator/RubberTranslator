package com.rubbertranslator.system;

import com.rubbertranslator.utils.OSTypeUtil;

/**
 * @author Raven
 * @version 1.0
 * date  2021/2/18 20:20
 * 存放各种路径
 */
public class ProgramPaths {

    // log 目录存放路径
    public static final String logFileDir;

    // export 目录
    public static final String exportDir;

    // 配置文件目录
    public static final String configFileDir;

    static {
        if (OSTypeUtil.isWin()) {
//            logFileDir = System.getenv("APPDATA") + "/RubberTranslator/log";
//            exportDir = System.getenv("APPDATA") + "/RubberTranslator/export";
//            configFileDir = System.getenv("APPDATA")+ "/RubberTranslator/config";
            logFileDir = System.getProperty("user.dir") + "/RubberTranslator/log";
            exportDir = System.getProperty("user.dir") + "/RubberTranslator/export";
            configFileDir = System.getProperty("user.dir") + "/RubberTranslator/config";
        } else if (OSTypeUtil.isLinux()) {
            logFileDir = System.getProperty("user.dir") + "/RubberTranslator/log";
            exportDir = System.getProperty("user.dir") + "/RubberTranslator/export";
            configFileDir = System.getProperty("user.dir") + "/RubberTranslator/config";
        } else {        // is mac
            logFileDir = System.getProperty("user.home") + "/RubberTranslator/log";
            exportDir = System.getProperty("user.home") + "/RubberTranslator/export";
            configFileDir = System.getProperty("user.home") + "/RubberTranslator/config";
        }
    }

    private ProgramPaths() {
    }

}
