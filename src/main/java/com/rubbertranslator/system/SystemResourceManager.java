package com.rubbertranslator.system;

import com.google.gson.Gson;
import com.rubbertranslator.mvp.modules.TranslatorFacade;
import com.rubbertranslator.mvp.modules.afterprocess.AfterProcessor;
import com.rubbertranslator.mvp.modules.filter.ProcessFilter;
import com.rubbertranslator.mvp.modules.filter.WindowsPlatformActiveWindowListenerThread;
import com.rubbertranslator.mvp.modules.history.TranslationHistory;
import com.rubbertranslator.mvp.modules.log.LoggerManager;
import com.rubbertranslator.mvp.modules.textinput.clipboard.ClipboardListenerThread;
import com.rubbertranslator.mvp.modules.textinput.mousecopy.DragCopyThread;
import com.rubbertranslator.mvp.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.mvp.modules.textprocessor.post.TextPostProcessor;
import com.rubbertranslator.mvp.modules.textprocessor.pre.TextPreProcessor;
import com.rubbertranslator.mvp.modules.translate.TranslatorFactory;
import com.rubbertranslator.enumtype.TranslatorType;
import com.rubbertranslator.mvp.modules.translate.baidu.BaiduTranslator;
import com.rubbertranslator.mvp.modules.translate.youdao.YoudaoTranslator;
import com.rubbertranslator.mvp.presenter.ModelPresenter;
import com.rubbertranslator.mvp.presenter.ConfigPresenter;
import com.rubbertranslator.utils.FileUtil;
import it.sauronsoftware.junique.JUnique;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/10 22:25
 * 系统资源管理
 */
public class SystemResourceManager {
    // 新配置文件路径 更改为用户home目录
    public static String configJsonPath = System.getProperty("user.home") + "/RubberTranslator/config/configuration.json";
    private static ClipboardListenerThread clipboardListenerThread;
    private static DragCopyThread dragCopyThread;
    private static WindowsPlatformActiveWindowListenerThread activeWindowListenerThread;
    private static SystemConfigurationManager configManager;
    private static SystemConfiguration configuration;
    private static TranslatorFacade facade;

    // cache线程池，还是single线程池好一点？
    private static final ExecutorService executor = Executors.newCachedThreadPool();


    // 只能通过静态方法调用此类
    private SystemResourceManager() {

    }

    public static ExecutorService getExecutor() {
        return executor;
    }


    /**
     * 初始化系统资源, 并返回系统配置类
     *
     * @return true 初始化成功
     * false 初始化失败
     */
    public static SystemConfiguration init() {
        LoggerManager.configLog();
        facade = new TranslatorFacade();
        initSystemConfig();
        textInputInit(configuration);
        filterInit(configuration);
        preTextProcessInit(configuration);
        postTextProcessInit(configuration);
        translatorInit(configuration);
        historyInit(configuration);
        afterProcessorInit(configuration);
        return configuration;
    }


    /**
     * 释放资源
     * xxx:考虑加个CountDownLatch确定线程都结束
     */
    public static void destroy() {
        Logger.getLogger(SystemResourceManager.class.getName()).info("资源销毁中");
        // 1. 保存配置文件
        configManager.saveConfigFile(configJsonPath);
        // 2. 释放资源
        try {
            executor.shutdownNow();
            executor.awaitTermination(1, TimeUnit.SECONDS);
            JUnique.releaseLock("RubberTranslator");
            textInputDestroy();
            processFilterDestroy();
            // TODO: 检查资源释放情况
            // 其余模块没有资源需要手动释放
            System.runFinalization();
            //        System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void initPresenter(ConfigPresenter presenter) {
        if (presenter instanceof ModelPresenter) {
            // 注入facade
            ((ModelPresenter) presenter).setTranslatorFacade(facade);
            // 注入其它模块
            ((ModelPresenter) presenter).setActiveWindowListenerThread(activeWindowListenerThread);
            ((ModelPresenter) presenter).setClipboardListenerThread(clipboardListenerThread);
            ((ModelPresenter) presenter).setDragCopyThread(dragCopyThread);
        }
        // 注入config manager
        configManager = new SystemConfigurationManager(configuration);
        presenter.setConfigManger(configManager);

    }

    /**
     * 加载配置文件
     */
    private static boolean initSystemConfig() {
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
        configuration = gson.fromJson(configJson, SystemConfiguration.class);
        Logger.getLogger(SystemResourceManager.class.getName()).info("加载配置" + configJson);
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
        textPreProcessor.setTryToFormat(preProcessConfig.isTryFormat());
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

    private static boolean historyInit(SystemConfiguration configuration) {
        TranslationHistory history = new TranslationHistory();
        history.setHistoryCapacity(configuration.getHistoryNum());
        return true;
    }

    private static boolean afterProcessorInit(SystemConfiguration configuration) {
        AfterProcessor afterProcessor = new AfterProcessor();
        afterProcessor.setAutoCopy(configuration.isAutoCopy());
        afterProcessor.setAutoPaste(configuration.isAutoPaste());
        facade.setAfterProcessor(afterProcessor);
        return true;
    }


}
