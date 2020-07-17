package com.rubbertranslator.system;

import com.rubbertranslator.utils.FileUtil;
import com.rubbertranslator.utils.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemConfigurationManager {
    private SystemConfiguration systemConfiguration;

    public SystemConfigurationManager(SystemConfiguration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
    }

    public SystemConfiguration getSystemConfiguration() {
        return systemConfiguration;
    }

    public void saveConfigFile(String path){
        // 静态代理还原
        String json = JsonUtil.serialize(systemConfiguration.getConfiguration());
        // 使用ui线程来写入
        try {
            FileUtil.writeStringToFile(new File(path), json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Logger.getLogger(SystemResourceManager.class.getName()).log(Level.SEVERE,"更新设置时出错",e);
        }
    }
}
