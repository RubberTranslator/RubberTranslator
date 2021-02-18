package com.rubbertranslator.system;

import com.google.gson.Gson;
import com.rubbertranslator.utils.FileUtil;
import com.rubbertranslator.utils.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemConfigurationManager {
    // 新配置文件路径 更改为用户home目录
    public String configJsonDir;
    public String configJsonPath = "";
    private SystemConfiguration systemConfiguration;

    {
        configJsonDir = System.getProperty("user.home") + "/RubberTranslator/config";
    }


    public SystemConfiguration getSystemConfiguration() {
        return systemConfiguration;
    }

    /**
     * 持久化配置文件到json
     * 需要 systemConfiguration 和  configJsonPath 不为空
     */
    public void saveConfigFile() {
        if (systemConfiguration == null || configJsonPath == null) {
            Logger.getLogger(SystemResourceManager.class.getName()).log(Level.SEVERE, "更新设置时出错，" +
                    "配置类或配置文件路径为空");
            return;
        }
        // 静态代理还原
        String json = JsonUtil.serialize(systemConfiguration.getConfiguration());
        // 使用ui线程来写入
        try {
            FileUtil.writeStringToFile(new File(configJsonPath), json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Logger.getLogger(SystemResourceManager.class.getName()).log(Level.SEVERE, "更新设置时出错", e);
        }
    }

    /**
     * 加载配置文件
     */
    public boolean init() {
        String curVersion = getCurrentVersion();
        if (null == curVersion) return false;

        // 初始化configJson文件路径
        configJsonPath = getConfigJsonPath(curVersion);

        if (isCorrectConfigFileExist(configJsonPath)) {       // 如果当前版本配置文件已经存在
            systemConfiguration = generateConfigFromExistFile(configJsonPath);
        } else {      // 不存在
            SystemConfiguration defaultConfig = generateDefaultConfig();
            // 读取配置文件路径下的最大版本号文件
            File dir = new File(configJsonPath).getParentFile();
            // 不存在则创建
            if (!dir.exists()) dir.mkdirs();
            String maxOldVersion = getMaxVersionFromOldConfigFiles(dir);
            if (maxOldVersion == null) {   // 不存在旧的config文件。
                // 直接序列化默认config即可
                systemConfiguration = defaultConfig;
            } else {      // 存在旧的config文件
                String maxVersionConfigFilePath = getConfigJsonPath(maxOldVersion);
                SystemConfiguration oldConfig = generateConfigFromExistFile(maxVersionConfigFilePath);
                systemConfiguration = mergeConfig(defaultConfig, oldConfig);
                Logger.getLogger(this.getClass().getName()).info("merge with " + maxOldVersion);
            }
            saveConfigFile();
        }

        return systemConfiguration != null;
    }


    /**
     * 取得当前软件版本
     *
     * @return 版本号。 如 v2.0.0
     */
    public static String getCurrentVersion() {
        String version = null;
        try {
            Properties props = new Properties();
            props.load(SystemConfiguration.class.getResourceAsStream("/config/misc.properties"));
            version = (String) props.get("local-version");
        } catch (IOException e) {
            Logger.getLogger(SystemConfigurationManager.class.getName()).log(Level.SEVERE, e.getLocalizedMessage());
        }
        return version;
    }


    /**
     * 根据软件版本，得到配置json文件路径
     * 对于旧版本的configuration文件， 文件名为 configuration.json, 所以以空字符串来区别新旧版本
     * 新版本为 configuration-vx.x.x.json
     *
     * @param version
     * @return 当前配置json文件路径
     */
    private String getConfigJsonPath(String version) {
        String tmpPath;
        if ("".equals(version)) {
            // v2.0.0-beta3 之前
            tmpPath = configJsonDir + "/configuration.json";
        } else {
            // v2.0.0-beta3 之后（包含）
            tmpPath = configJsonDir + "/configuration-" + version + ".json";
        }
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "当前配置文件路径" + tmpPath);
        return tmpPath;
    }

    /**
     * 当前版本对应配置文件是否存在
     *
     * @param configPath
     * @return
     */
    private boolean isCorrectConfigFileExist(String configPath) {
        File file = new File(configPath);
        return file.exists();
    }


    /**
     * 从已存在的对应版本的配置文件中生成配置
     *
     * @param configJsonPath
     */
    private SystemConfiguration generateConfigFromExistFile(String configJsonPath) {
        // 加载本地配置
        File file = new File(configJsonPath);
        String configJson = null;
        SystemConfiguration configuration = null;
        try {
            configJson = FileUtil.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Logger.getLogger(SystemResourceManager.class.getName()).severe(e.getLocalizedMessage());
            return null;
        }
        // json --> object
        Gson gson = new Gson();
        // 原始配置记录
        configuration = gson.fromJson(configJson, SystemConfiguration.class);
        Logger.getLogger(SystemResourceManager.class.getName()).info("加载配置" + configJson);
        return configuration;
    }

    /**
     * 生成默认配置类
     *
     * @return 成功 默认配置类
     * 失败 null
     */
    private SystemConfiguration generateDefaultConfig() {
        // 加载本地配置
        String configJson = null;
        SystemConfiguration configuration;
        try {
            InputStream resourceAsStream = SystemResourceManager.class.getResourceAsStream("/config/default_configuration.json");
            configJson = FileUtil.readInputStreamToString(resourceAsStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Logger.getLogger(SystemResourceManager.class.getName()).severe(e.getLocalizedMessage());
            return null;
        }
        // json --> object
        Gson gson = new Gson();
        // 原始配置记录
        configuration = gson.fromJson(configJson, SystemConfiguration.class);
        Logger.getLogger(SystemResourceManager.class.getName()).info("加载配置" + configJson);
        return configuration;
    }

    /**
     * 取得配置目录目录下的最大版本号
     *
     * @return 成功，返回最大版本号， 若为旧版本(v2.0.0-beta3之前），版本号统一为空 ""
     * 失败， 返回null
     */
    private String getMaxVersionFromOldConfigFiles(File dir) {
        File[] files = dir.listFiles(File::isFile);
        if (files.length == 0) return null;

        String[] fileNames = new String[files.length];
        for (int i = 0; i < fileNames.length; i++) {
            String tmpName = files[i].getName();
            int startOffset = tmpName.indexOf("-");
            if (startOffset == -1) {
                // 旧版本RubberTranslator v2.0.0-beta3之前
                fileNames[i] = "";
            } else {
                fileNames[i] = tmpName.substring(startOffset + 1);
            }
        }
        // 排序
        Arrays.sort(fileNames);
        // 返回最大的版本号
        return fileNames[fileNames.length - 1].replace(".json", "");
    }

    /**
     * 升级程序，核心方法，在baseConfig的基础上，合并来自oldConfig中已经有的字段
     *
     * @param baseConfig
     * @param oldConfig
     * @return 最终配置类
     */
    private SystemConfiguration mergeConfig(SystemConfiguration baseConfig, SystemConfiguration oldConfig) {
        if(baseConfig == null || oldConfig == null) return baseConfig;
        Class<?> clz = SystemConfiguration.class;
        // 枚举成员函数
        Method[] methods = clz.getMethods();
        for (Method m : methods) {
            int startOffset = 0;
            Class<?> classType = m.getReturnType();

            // 确定偏移量,方便后续将get方法转为set方法
            String methodName = m.getName();
            if (!isIncludeMethod(methodName)) continue;
            if (methodName.startsWith("is")) {
                startOffset = 2;
            } else if (methodName.startsWith("get")) {
                startOffset = 3;
            }
            try {
                // 调用对应的get方法
                Object result = m.invoke(oldConfig);
                if (result != null) {
                    String setMethodName = "set" + methodName.substring(startOffset);
                    Logger.getLogger(this.getClass().getName()).fine("合并：" + setMethodName);
                    Method setMethod = clz.getMethod(setMethodName, classType);
                    // 调用对应的set方法
                    setMethod.invoke(baseConfig, result);
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "合并配置失败");
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getLocalizedMessage());
            }

        }
        Logger.getLogger(this.getClass().getName()).info("合并配置成功");
        return baseConfig;
    }

    /**
     * 是否是包含在内的method
     *
     * @param methodName
     * @return
     */
    private boolean isIncludeMethod(String methodName) {
        return methodName.startsWith("is") ||
                (
                        methodName.startsWith("get") &&
                                !methodName.equals("getClass") &&
                                !methodName.equals("getConfiguration")
                );
    }
}
