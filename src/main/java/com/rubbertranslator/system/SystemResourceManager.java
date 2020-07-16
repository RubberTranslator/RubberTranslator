package com.rubbertranslator.system;

import com.google.gson.Gson;
import com.rubbertranslator.modules.TranslatorFacade;
import com.rubbertranslator.modules.afterprocess.AfterProcessor;
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
import com.rubbertranslator.utils.JsonUtil;
import it.sauronsoftware.junique.JUnique;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
        Logger.getLogger(SystemResourceManager.class.getName()).info("资源销毁中");
        // 1. 保存配置文件
        saveConfigFile();
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

    /**
     * 保存配置文件
     */
    public static void saveConfigFile(){
        // 静态代理还原
        String json = JsonUtil.serialize(configurationProxy.getConfiguration());
        // 使用ui线程来写入
        try {
            FileUtil.writeStringToFile(new File(configJsonPath), json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Logger.getLogger(SystemResourceManager.class.getName()).log(Level.SEVERE,"更新设置时出错",e);
        }
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
        // 静态代理
        return new SystemConfigurationStaticProxy(configuration);
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
