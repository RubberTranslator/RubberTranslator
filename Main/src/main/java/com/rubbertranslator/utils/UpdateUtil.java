package com.rubbertranslator.utils;

import com.rubbertranslator.listener.GenericCallback;

import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date  2020/12/22 22:50
 * 检查更新工具类
 */
public class UpdateUtil {

    public static void checkUpdate(String localVersion, String remoteVersionUrl, GenericCallback<Boolean> callback) {
        // get local version
        if (localVersion == null) {
            callback.callBack(false);
            return;
        }

        // get remote version
        String remoteVersion = null;
        remoteVersion = OkHttpUtil.get(remoteVersionUrl, null);
        if (remoteVersion == null) {
            callback.callBack(false);
            return;
        }
        remoteVersion = remoteVersion.split("\n")[0];
        Logger.getLogger(UpdateUtil.class.getName()).info("remote version: " + remoteVersion);
        if(remoteVersion.endsWith("beta")){
            callback.callBack(false);
        }else{
            // compare
            callback.callBack(localVersion.compareTo(remoteVersion) < 0);
        }
    }
}
