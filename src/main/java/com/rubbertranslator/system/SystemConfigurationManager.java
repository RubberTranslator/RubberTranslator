package com.rubbertranslator.system;

import com.google.gson.Gson;
import com.rubbertranslator.utils.FileUtil;
import com.rubbertranslator.utils.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemConfigurationManager {
    // 新配置文件路径 更改为用户home目录
    public static String configJsonPath = System.getProperty("user.dir") + "/RubberTranslator/config/configuration.json";

    private SystemConfiguration systemConfiguration;

    public SystemConfigurationManager() {
    }

    /**
     * 加载配置文件
     */
    public boolean init() {
        // 加载本地配置
        File file = new File(configJsonPath);
        String configJson;
        try {
            if (!file.exists()) {
                InputStream resourceAsStream = SystemResourceManager.class.getResourceAsStream("/config/default_configuration.json");
                FileUtil.copyInputStreamToFile(resourceAsStream, file);
            }
            configJson = FileUtil.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Logger.getLogger(SystemResourceManager.class.getName()).severe(e.getLocalizedMessage());
            return false;
        }
        // json --> object
        Gson gson = new Gson();
        // 原始配置记录
        systemConfiguration = gson.fromJson(configJson, SystemConfiguration.class);
        Logger.getLogger(SystemResourceManager.class.getName()).info("加载配置" + configJson);
        return true;
    }


    public SystemConfiguration getSystemConfiguration() {
        return systemConfiguration;
    }

    public void saveConfigFile(){
        // 静态代理还原
        String json = JsonUtil.serialize(systemConfiguration.getConfiguration());
        // 使用ui线程来写入
        try {
            FileUtil.writeStringToFile(new File(configJsonPath), json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Logger.getLogger(SystemResourceManager.class.getName()).log(Level.SEVERE,"更新设置时出错",e);
        }
    }
}
