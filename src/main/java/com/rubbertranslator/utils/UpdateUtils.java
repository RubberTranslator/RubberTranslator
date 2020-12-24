package com.rubbertranslator.utils;

import com.rubbertranslator.listener.GenericCallback;
import com.rubbertranslator.system.SystemConfigurationManager;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date  2020/12/22 22:50
 * 检查更新工具类
 */
public class UpdateUtils {
    private static final String versionUrl = "https://cdn.jsdelivr.net/gh/ravenxrz/RubberTranslator@latest/misc/version.txt";
    public static void checkUpdate(GenericCallback<Boolean> callback){
        // get local version
        String localVersion = SystemConfigurationManager.getCurrentVersion();
        Logger.getLogger(UpdateUtils.class.getName()).info("current version:" + localVersion);
        if(localVersion == null){
            callback.callBack(false);
            return;
        }

        // get remote version
        String remoteVersion = null;
        remoteVersion = OkHttpUtil.get(versionUrl,null);
        if(remoteVersion == null){
            callback.callBack(false);
            return;
        }

        Logger.getLogger(UpdateUtils.class.getName()).info("remote version:" + remoteVersion);
        // compare
        if(localVersion.compareTo(remoteVersion) < 0){
            Logger.getLogger(UpdateUtils.class.getName()).info("new version released");
            callback.callBack(true);
        }else{
            callback.callBack(false);
        }
    }
}
