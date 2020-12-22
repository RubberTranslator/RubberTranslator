package com.rubbertranslator.utils;

import com.rubbertranslator.listener.GenericCallback;
import com.rubbertranslator.system.SystemConfigurationManager;
import okhttp3.Request;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date  2020/12/22 22:50
 * 检查更新工具类
 */
public class UpdateUtils {
    private static final String versionUrl = "https://raw.githubusercontent.com/ravenxrz/RubberTranslator/version_control/latest_stable_version.txt";
    public static void checkUpdate(GenericCallback<Boolean> callback){
        // get local version
        String localVersion = SystemConfigurationManager.getCurrentVersion();
        Logger.getLogger(UpdateUtils.class.getName()).info("current version:" + localVersion);
        if(localVersion == null){
            callback.callBack(false);
            return;
        }

        // get remote version
        Request request = new Request.Builder()
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36")
                .url(versionUrl)
                .build();

        String remoteVersion = null;
        try {
            remoteVersion = OkHttpUtil.syncRequest(request);
            if(remoteVersion == null){
                callback.callBack(false);
                return;
            }
            Logger.getLogger(UpdateUtils.class.getName()).info("remote version:" + remoteVersion);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // compare
        if(localVersion.compareTo(remoteVersion) < 0){
            Logger.getLogger(UpdateUtils.class.getName()).info("new version:" + remoteVersion);
            callback.callBack(true);
        }else{
            callback.callBack(false);
        }
    }
}
