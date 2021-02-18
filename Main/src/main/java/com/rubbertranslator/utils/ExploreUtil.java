package com.rubbertranslator.utils;

import com.rubbertranslator.listener.GenericCallback;
import com.rubbertranslator.mvp.view.controller.impl.RecordModeController;
import com.rubbertranslator.system.SystemResourceManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date  2021/2/18 21:10
 */
public class ExploreUtil {
    public static void openExplore(String path, GenericCallback<String> callback) {
        if (path == null) return;
        if (!new File(path).isDirectory()) {
            Logger.getLogger(ExploreUtil.class.getName()).severe(path + "不是一个有效目录");
            return;
        }
        SystemResourceManager.getExecutor().execute(() -> {
            try {
                Desktop.getDesktop().browse(Paths.get(path).toUri());
            } catch (IOException e) {
                callback.callBack("当前操作系统不支持本操作");
                Logger.getLogger(RecordModeController.class.getName()).severe(e.getLocalizedMessage());
            }
        });
    }
}
