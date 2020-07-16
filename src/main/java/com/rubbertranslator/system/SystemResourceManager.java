package com.rubbertranslator.system;

import com.google.gson.Gson;
import com.rubbertranslator.modules.TranslatorFacade;
import com.rubbertranslator.modules.afterprocess.AfterProcessor;
import com.rubbertranslator.modules.config.ConfigProxy;
import com.rubbertranslator.modules.config.SystemConfiguration;
import com.rubbertranslator.modules.config.SystemConfigurationStaticProxy;
import com.rubbertranslator.modules.filter.ProcessFilter;
import com.rubbertranslator.modules.filter.WindowsPlatformActiveWindowListenerThread;
import com.rubbertranslator.modules.history.TranslationHistory;
import com.rubbertranslator.modules.log.LoggerManager;
import com.rubbertranslator.modules.textinput.clipboard.ClipboardListenerThread;
import com.rubbertranslator.modules.textinput.mousecopy.DragCopyThread;
import com.rubbertranslator.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.modules.textprocessor.post.TextPostProcessor;
import com.rubbertranslator.modules.textprocessor.pre.TextPreProcessor;
import com.rubbertranslator.modules.translate.TranslatorFactory;
import com.rubbertranslator.modules.translate.TranslatorType;
import com.rubbertranslator.modules.translate.baidu.BaiduTranslator;
import com.rubbertranslator.modules.translate.youdao.YoudaoTranslator;
import com.rubbertranslator.utils.FileUtil;
import javafx.stage.Stage;
import net.sf.cglib.proxy.Enhancer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/10 22:25
 * 系统资源管理
 */
public class SystemResourceManager {
    // 新配置文件路径 更改为用户home目录
    public static String configJsonPath = System.getProperty("user.home")+"/RubberTranslator/config/configuration.json";
    private static ClipboardListenerThread clipboardListenerThread;
    private static DragCopyThread dragCopyThread;
    private static WindowsPlatformActiveWindowListenerThread activeWindowListenerThread;
    private static TranslatorFacade facade;
    private static Stage appStage;

    // cache线程池，还是single线程池好一点？
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    // 所有用户得操作，都应该通过configurationProxy来操作
    private static SystemConfiguration configurationProxy;


    // 只能通过静态方法调用此类
    private SystemResourceManager() {

    }
    public static ExecutorService getExecutor() {
        return executor;
    }

    public static ClipboardListenerThread getClipboardListenerThread() {
        assert clipboardListenerThread != null : "请先执行SystemResourceManager.init";
        return clipboardListenerThread;
    }

    public static DragCopyThread getDragCopyThread() {
        assert dragCopyThread != null : "请先执行SystemResourceManager.init";
        return dragCopyThread;
    }

    public static TranslatorFacade getFacade() {
        assert facade != null : "请先执行SystemResourceManager.init";
        return facade;
    }

    public static Stage getStage() {
        return appStage;
    }

    /**
     * 设置stage
     * @param stage
     */
    public static void setStage(Stage stage)
    {
        appStage = stage;
        uiInit(configurationProxy);
    }

    public static SystemConfiguration getConfigurationProxy() {
        return configurationProxy;
    }


    /**
     * 初始化系统资源
     *
     * @return true 初始化成功
     * false 初始化失败
     */
    public static boolean init() {
        // 0. 设置log
        LoggerManager.configLog();
        // 1. 加载配置文件
        configurationProxy = loadSystemConfig();
        if (configurationProxy == null) return false;
        // 2.初始化facade
        facade = new TranslatorFacade();
        // 3. 启动各组件
        // ui配置在controller进行应用
        return  textInputInit(configurationProxy) &&
                filterInit(configurationProxy) &&
                preTextProcessInit(configurationProxy) &&
                postTextProcessInit(configurationProxy) &&
                translatorInit(configurationProxy) &&
                historyInit(configurationProxy) &&
                afterProcessorInit(configurationProxy);
    }


    /**
     * 释放资源
     * xxx:考虑加个CountDownLatch确定线程都结束
     */
    public static void destroy() {
        textInputDestroy();
        processFilterDestroy();
        // 其余模块没有资源需要手动释放
        System.runFinalization();
        System.exit(0);
    }

    /**
     * 加载配置文件
     *
     * @return null 加载失败
     * 配置类 加载成功
     */
    private static SystemConfiguration loadSystemConfig() {
        // 加载本地配置
        File file = new File(configJsonPath);
        String configJson;
        try {
            if (!file.exists()) {
                InputStream resourceAsStream = SystemResourceManager.class.getResourceAsStream("/config/default_configuration.json");
                FileUtil.copyInputStreamToFile(resourceAsStream,file);
            }
            configJson = FileUtil.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Logger.getLogger(SystemResourceManager.class.getName()).severe(e.getLocalizedMessage());
            return null;
        }
        // json --> object
        Gson gson = new Gson();
        // 原始配置记录
        SystemConfiguration configuration = gson.fromJson(configJson, SystemConfiguration.class);
        Logger.getLogger(SystemResourceManager.class.getName()).info("加载配置"+configJson);
        return wrapProxy(configuration);
    }

    /**
     * 为所有系统配置包装代理
     * 每当用户修改任何系统配置，都将配置持久化
     *  两层代理：
     *  1. 静态代理： 设置更改后，通知后续处理模块，立马更改
     *  2. 动态代理： 设置更改后，持久化所有设置
     * @param configuration 系统配置
     */
    private static SystemConfiguration wrapProxy(SystemConfiguration configuration) {
        SystemConfiguration systemStaticConfig = new SystemConfigurationStaticProxy(configuration);
        SystemConfiguration systemConfiguration = (SystemConfiguration)
                Enhancer.create(SystemConfiguration.class, new ConfigProxy(systemStaticConfig));
        Logger.getLogger(SystemConfiguration.class.getName()).info("wrap代理完成");
        return systemConfiguration;
    }


    private static boolean uiInit(SystemConfiguration configurationProxy)
    {
        if(appStage == null || configurationProxy == null) return false;
        appStage.setAlwaysOnTop(configurationProxy.isKeepTop());
        try {
            // 回显
            String path = configurationProxy.getStyleCssPath();
            if (path != null) {
                File file = new File(path);
                if (file.exists()) {
                    appStage.getScene().getStylesheets().setAll(file.toURI().toURL().toString());
                }
            }
        } catch (MalformedURLException e) {
            Logger.getLogger(SystemResourceManager.class.getName()).log(Level.WARNING, e.getLocalizedMessage(), e);
        }
        return true;
    }

    private static boolean textInputInit(SystemConfiguration configuration) {
        clipboardListenerThread = new ClipboardListenerThread();
        clipboardListenerThread.setRun(configuration.isOpenClipboardListener());
        dragCopyThread = new DragCopyThread();
        dragCopyThread.setRun(configuration.isDragCopy());
        OCRUtils.setApiKey(configuration.getBaiduOcrApiKey());
        OCRUtils.setSecretKey(configuration.getBaiduOcrSecretKey());

        clipboardListenerThread.start();
        dragCopyThread.start();
        return true;
    }

    private static void textInputDestroy() {
        clipboardListenerThread.exit();
        dragCopyThread.exit();
    }

    private static void processFilterDestroy() {
        activeWindowListenerThread.exit();
    }

    private static boolean filterInit(SystemConfiguration configuration) {
        ProcessFilter processFilter = new ProcessFilter();
        processFilter.setOpen(configuration.isOpenProcessFilter());
        processFilter.addFilterList(configuration.getProcessList());
        // 装载过滤器到剪切板监听线程
        clipboardListenerThread.setProcessFilter(processFilter);
        activeWindowListenerThread = new WindowsPlatformActiveWindowListenerThread();
        activeWindowListenerThread.start();
        return true;
    }

    private static boolean preTextProcessInit(SystemConfiguration preProcessConfig) {
        TextPreProcessor textPreProcessor = new TextPreProcessor();
        textPreProcessor.setTryToFormat(preProcessConfig.isTryToFormat());
        textPreProcessor.setIncrementalCopy(preProcessConfig.isIncrementalCopy());
        facade.setTextPreProcessor(textPreProcessor);
        return true;
    }

    private static boolean postTextProcessInit(SystemConfiguration postProcessConfig) {
        TextPostProcessor textPostProcessor = new TextPostProcessor();
        textPostProcessor.getReplacer().setCaseInsensitive(postProcessConfig.isCaseInsensitive());
        textPostProcessor.getReplacer().addWords(postProcessConfig.getWordsPairs());
        facade.setTextPostProcessor(textPostProcessor);
        return true;
    }

    private static boolean translatorInit(SystemConfiguration configuration) {
        TranslatorFactory translatorFactory = new TranslatorFactory();
        translatorFactory.setEngineType(configuration.getCurrentTranslator());
        translatorFactory.setSourceLanguage(configuration.getSourceLanguage());
        translatorFactory.setDestLanguage(configuration.getDestLanguage());
        if (configuration.getBaiduTranslatorApiKey() != null && configuration.getBaiduTranslatorSecretKey() != null) {
            BaiduTranslator baiduTranslator = new BaiduTranslator();
            baiduTranslator.setAppKey(configuration.getBaiduTranslatorApiKey());
            baiduTranslator.setSecretKey(configuration.getBaiduTranslatorSecretKey());
            translatorFactory.addTranslator(TranslatorType.BAIDU, baiduTranslator);
        }
        if (configuration.getYouDaoTranslatorApiKey() != null && configuration.getYouDaoTranslatorSecretKey() != null) {
            YoudaoTranslator youdaoTranslator = new YoudaoTranslator();
            youdaoTranslator.setAppKey(configuration.getYouDaoTranslatorApiKey());
            youdaoTranslator.setSecretKey(configuration.getYouDaoTranslatorSecretKey());
            translatorFactory.addTranslator(TranslatorType.YOUDAO, youdaoTranslator);
        }
        facade.setTranslatorFactory(translatorFactory);
        return true;
    }

    private static boolean historyInit(SystemConfiguration configuration){
        TranslationHistory history = new TranslationHistory();
        history.setHistoryCapacity(configuration.getHistoryNum());
        return true;
    }

    private static boolean afterProcessorInit(SystemConfiguration configuration) {
        AfterProcessor afterProcessor = new AfterProcessor();
        afterProcessor.setAutoCopy(configuration.isAutoCopy());
        afterProcessor.setAutoPaste(configuration.isAutoPaste());
        facade.setAfterProcessor(afterProcessor);
        return  true;
    }



}
