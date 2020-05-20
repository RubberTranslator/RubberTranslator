package com.rubbertranslator.system;

import com.google.gson.Gson;
import com.rubbertranslator.modules.TranslatorFacade;
import com.rubbertranslator.modules.afterprocess.AfterProcessor;
import com.rubbertranslator.modules.filter.ProcessFilter;
import com.rubbertranslator.modules.filter.WindowsPlatformActiveWindowListenerThread;
import com.rubbertranslator.modules.history.TranslationHistory;
import com.rubbertranslator.modules.log.LoggerManager;
import com.rubbertranslator.system.proxy.*;
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
import net.sf.cglib.proxy.Enhancer;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
        return textInputInit(configurationProxy.getTextInputConfig()) &&
                filterInit(configurationProxy.getProcessFilterConfig()) &&
                preTextProcessInit(configurationProxy.getTextProcessConfig().getTextPreProcessConfig()) &&
                postTextProcessInit(configurationProxy.getTextProcessConfig().getTextPostProcessConfig()) &&
                translatorInit(configurationProxy.getTranslatorConfig()) &&
                historyInit(configurationProxy.getHistoryConfig()) &&
                afterProcessorInit(configurationProxy.getAfterProcessorConfig());
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
        // 文本输入配置
        TextInputConfigStaticProxy textInputConfigStaticProxy = new TextInputConfigStaticProxy(configuration.getTextInputConfig());
        SystemConfiguration.TextInputConfig textInputConfigProxy = (SystemConfiguration.TextInputConfig)
                Enhancer.create(SystemConfiguration.TextInputConfig.class, new ConfigProxy(textInputConfigStaticProxy));

        // 过滤器
        ProcessFilterConfigStaticProxy processFilterStaticConfig = new ProcessFilterConfigStaticProxy(configuration.getProcessFilterConfig());
        SystemConfiguration.ProcessFilterConfig processFilterConfigProxy = (SystemConfiguration.ProcessFilterConfig)
                Enhancer.create(SystemConfiguration.ProcessFilterConfig.class, new ConfigProxy(processFilterStaticConfig));

        // 前置处理
        TextPreProcessConfigStaticProxy textPreProcessStaticConfig = new TextPreProcessConfigStaticProxy(configuration.getTextProcessConfig().getTextPreProcessConfig());
        SystemConfiguration.TextProcessConfig.TextPreProcessConfig textPreProcessConfigProxy = (SystemConfiguration.TextProcessConfig.TextPreProcessConfig)
                Enhancer.create(SystemConfiguration.TextProcessConfig.TextPreProcessConfig.class, new ConfigProxy(textPreProcessStaticConfig));
        // 后置文本处理
        /**
         * FIXME: 降低文本处理的嵌套层级！！！！当前：总->TextProcess->TextPostProcess->WordReplacer
         * FIXME：改为 总->WordReplacer
         */
        // 文本替换处理
        WordsReplacerConfigStaticProxy wordsReplacerStaticConfig =
                new WordsReplacerConfigStaticProxy(configuration.getTextProcessConfig().getTextPostProcessConfig().getWordsReplacerConfig());
        SystemConfiguration.TextProcessConfig.TextPostProcessConfig.WordsReplacerConfig wordsReplacerConfig = (SystemConfiguration.TextProcessConfig.TextPostProcessConfig.WordsReplacerConfig) Enhancer.create(SystemConfiguration.TextProcessConfig.TextPostProcessConfig.WordsReplacerConfig.class,new ConfigProxy(wordsReplacerStaticConfig));

        configuration.getTextProcessConfig().getTextPostProcessConfig().setWordsReplacerConfig(wordsReplacerConfig);
        TextPostProcessConfigStaticProxy textPostProcessStaticConfig = new TextPostProcessConfigStaticProxy(configuration.getTextProcessConfig().getTextPostProcessConfig());
        SystemConfiguration.TextProcessConfig.TextPostProcessConfig textPostProcessConfig = (SystemConfiguration.TextProcessConfig.TextPostProcessConfig)
                Enhancer.create(SystemConfiguration.TextProcessConfig.TextPostProcessConfig.class, new ConfigProxy(textPostProcessStaticConfig));

        // 翻译配置
        TranslatorConfigStaticProxy translatorStaticConfig = new TranslatorConfigStaticProxy(configuration.getTranslatorConfig());
        SystemConfiguration.TranslatorConfig translatorConfigProxy = (SystemConfiguration.TranslatorConfig)
                Enhancer.create(SystemConfiguration.TranslatorConfig.class, new ConfigProxy(translatorStaticConfig));

        // 历史配置
        HistoryConfigStaticProxy historyStaticConfig = new HistoryConfigStaticProxy(configuration.getHistoryConfig());
        SystemConfiguration.HistoryConfig historyConfigProxy = (SystemConfiguration.HistoryConfig)
                Enhancer.create(SystemConfiguration.HistoryConfig.class, new ConfigProxy(historyStaticConfig));

        // 后置处理配置
        AfterProcessorConfigStaticProxy afterProcessorStaticConfig = new AfterProcessorConfigStaticProxy(configuration.getAfterProcessorConfig());
        SystemConfiguration.AfterProcessorConfig afterProcessorConfig = (SystemConfiguration.AfterProcessorConfig) Enhancer.create(SystemConfiguration.AfterProcessorConfig.class, new ConfigProxy(afterProcessorStaticConfig));

        // ui配置
        SystemConfiguration.UIConfig uiConfigProxy = (SystemConfiguration.UIConfig)
                Enhancer.create(SystemConfiguration.UIConfig.class,new ConfigProxy(configuration.getUiConfig()));

        // 注入
        configuration.setTextInputConfig(textInputConfigProxy);
        configuration.setProcessFilterConfig(processFilterConfigProxy);
        configuration.getTextProcessConfig().setTextPreProcessConfig(textPreProcessConfigProxy);
        configuration.getTextProcessConfig().setTextPostProcessConfig(textPostProcessConfig);
        configuration.setTranslatorConfig(translatorConfigProxy);
        configuration.setHistoryConfig(historyConfigProxy);
        configuration.setAfterProcessorConfig(afterProcessorConfig);
        configuration.setUiConfig(uiConfigProxy);
        Logger.getLogger(SystemConfiguration.class.getName()).info("wrap代理完成");
        return configuration;
    }


    private static boolean textInputInit(SystemConfiguration.TextInputConfig configuration) {
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

    private static boolean filterInit(SystemConfiguration.ProcessFilterConfig configuration) {
        ProcessFilter processFilter = new ProcessFilter();
        processFilter.setOpen(configuration.isOpenProcessFilter());
        processFilter.addFilterList(configuration.getProcessList());
        // 装载过滤器到剪切板监听线程
        clipboardListenerThread.setProcessFilter(processFilter);
        activeWindowListenerThread = new WindowsPlatformActiveWindowListenerThread();
        activeWindowListenerThread.start();
        return true;
    }

    private static boolean preTextProcessInit(SystemConfiguration.TextProcessConfig.TextPreProcessConfig preProcessConfig) {
        TextPreProcessor textPreProcessor = new TextPreProcessor();
        textPreProcessor.setTryToFormat(preProcessConfig.isTryToFormat());
        textPreProcessor.setIncrementalCopy(preProcessConfig.isIncrementalCopy());
        facade.setTextPreProcessor(textPreProcessor);
        return true;
    }

    private static boolean postTextProcessInit(SystemConfiguration.TextProcessConfig.TextPostProcessConfig postProcessConfig) {
        TextPostProcessor textPostProcessor = new TextPostProcessor();
        textPostProcessor.setOpen(postProcessConfig.isOpenPostProcess());
        textPostProcessor.getReplacer().setCaseInsensitive(postProcessConfig.getWordsReplacerConfig().isCaseInsensitive());
        textPostProcessor.getReplacer().addWords(postProcessConfig.getWordsReplacerConfig().getWordsPairs());
        facade.setTextPostProcessor(textPostProcessor);
        return true;
    }

    private static boolean translatorInit(SystemConfiguration.TranslatorConfig configuration) {
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

    private static boolean historyInit(SystemConfiguration.HistoryConfig configuration){
        TranslationHistory history = new TranslationHistory();
        history.setHistoryCapacity(configuration.getHistoryNum());
        return true;
    }

    private static boolean afterProcessorInit(SystemConfiguration.AfterProcessorConfig configuration) {
        AfterProcessor afterProcessor = new AfterProcessor();
        afterProcessor.setAutoCopy(configuration.isAutoCopy());
        afterProcessor.setAutoPaste(configuration.isAutoPaste());
        facade.setAfterProcessor(afterProcessor);
        return  true;
    }



}
