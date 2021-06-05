package com.rubbertranslator.system;

import com.rubbertranslator.enumtype.TranslatorType;
import com.rubbertranslator.mvp.modules.TranslatorFacade;
import com.rubbertranslator.mvp.modules.afterprocess.AfterProcessor;
import com.rubbertranslator.mvp.modules.filter.ProcessFilter;
import com.rubbertranslator.mvp.modules.filter.WindowsPlatformActiveWindowListenerThread;
import com.rubbertranslator.mvp.modules.history.TranslationHistory;
import com.rubbertranslator.mvp.modules.hotkey.GlobalHotKeyListener;
import com.rubbertranslator.mvp.modules.log.LoggerManager;
import com.rubbertranslator.mvp.modules.textinput.clipboard.ClipboardListenerThread;
import com.rubbertranslator.mvp.modules.textinput.clipboard.LinuxCpListenerThread;
import com.rubbertranslator.mvp.modules.textinput.clipboard.MacCpListenerThread;
import com.rubbertranslator.mvp.modules.textinput.clipboard.WinCpListenerThread;
import com.rubbertranslator.mvp.modules.textinput.mousecopy.DragCopyThread;
import com.rubbertranslator.mvp.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.mvp.modules.textprocessor.post.TextPostProcessor;
import com.rubbertranslator.mvp.modules.textprocessor.pre.TextPreProcessor;
import com.rubbertranslator.mvp.modules.translate.TranslatorFactory;
import com.rubbertranslator.mvp.modules.translate.baidu.BaiduTranslator;
import com.rubbertranslator.mvp.modules.translate.youdao.YoudaoTranslator;
import com.rubbertranslator.mvp.modules.update.UpdateTask;
import com.rubbertranslator.mvp.presenter.BasePresenter;
import com.rubbertranslator.mvp.presenter.ModelPresenter;
import com.rubbertranslator.mvp.view.IView;
import com.rubbertranslator.utils.OSTypeUtil;
import it.sauronsoftware.junique.JUnique;
import org.jetbrains.annotations.NotNull;

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
    private static ClipboardListenerThread clipboardListenerThread;
    private static DragCopyThread dragCopyThread;
    private static WindowsPlatformActiveWindowListenerThread activeWindowListenerThread;
    private static SystemConfigurationManager configManager;
    private static TranslatorFacade facade;

    // cache线程池，还是single线程池好一点？
    private static final ExecutorService executor = Executors.newCachedThreadPool();


    // 只能通过静态方法调用此类
    private SystemResourceManager() {

    }

    public static ExecutorService getExecutor() {
        return executor;
    }


    public static void setDragCopyAndCpListenState(boolean run){
        if(clipboardListenerThread != null){
            clipboardListenerThread.setRun(run);
        }
        if(dragCopyThread != null){
            dragCopyThread.setRun(run);
        }
    }


    /**
     * 初始化系统资源, 并返回系统配置类
     *
     * @return true 初始化成功
     * false 初始化失败
     */
    public static SystemConfiguration init() {
        // log 模块
        logModuleInit();
        // 热更新模块
        updateModuleInit();
        // hotKey
        hotKeyModuleInit();
        // 其余
        facade = TranslatorFacade.getInstance();
        configManager = new SystemConfigurationManager();
        if (!configManager.init()) return null;

        SystemConfiguration configuration = configManager.getSystemConfiguration();
        textInputInit(configuration);
        filterInit(configuration);
        preTextProcessInit(configuration);
        postTextProcessInit(configuration);
        translatorInit(configuration);
        historyInit(configuration);
        afterProcessorInit(configuration);
        return configuration;
    }

    private static void logModuleInit() {
        LoggerManager.configLog();
    }

    private static void updateModuleInit() {
        executor.execute(new UpdateTask());
    }

    private static void hotKeyModuleInit(){
        GlobalHotKeyListener.initHotKeyDispatcher();
    }


    /**
     * 释放资源
     * xxx:考虑加个CountDownLatch确定线程都结束
     */
    public static void destroy() {
        Logger.getLogger(SystemResourceManager.class.getName()).info("资源销毁中");
        // 1. 保存配置文件
        configManager.saveConfigFile();
        // 2. 释放资源
        try {
            executor.shutdownNow();
            executor.awaitTermination(1, TimeUnit.SECONDS);
            JUnique.releaseLock("RubberTranslator");
        } catch (InterruptedException e) {
            Logger.getLogger(SystemResourceManager.class.getName()).log(Level.SEVERE, "释放线程池资源失败");
        }
        GlobalHotKeyListener.destroy();
        textInputDestroy();
        processFilterDestroy();
        // TODO: 检查资源释放情况
        // 其余模块没有资源需要手动释放
        System.runFinalization();
        //        System.exit(0);
    }

    // TODO: Presenter继承关系混乱，这样写不太好，有机会再重构吧
    public static <T extends IView> void initPresenter(BasePresenter<T> presenter) {
        if (presenter instanceof ModelPresenter) {
            // 注入facade
            ((ModelPresenter) presenter).setTranslatorFacade(facade);
            // 注入其它模块
            ((ModelPresenter) presenter).setActiveWindowListenerThread(activeWindowListenerThread);
            ((ModelPresenter) presenter).setClipboardListenerThread(clipboardListenerThread);
            ((ModelPresenter) presenter).setDragCopyThread(dragCopyThread);
        }
        presenter.setConfigManger(configManager);
    }


    private static void textInputInit(@NotNull SystemConfiguration configuration) {
        // 启动相应线程
        if(OSTypeUtil.isMac()){
            clipboardListenerThread = new MacCpListenerThread();
        }else if(OSTypeUtil.isLinux()){
           clipboardListenerThread = new LinuxCpListenerThread();
        }else if(OSTypeUtil.isWin()){
           clipboardListenerThread = new WinCpListenerThread();
        }
        dragCopyThread = new DragCopyThread();
        clipboardListenerThread.start();
        dragCopyThread.start();

        // 配置线程
        clipboardListenerThread.setRun(configuration.isOpenClipboardListener());
        dragCopyThread.setRun(configuration.isDragCopy());

        // 配置OCR
        OCRUtils.setApiKey(configuration.getBaiduOcrApiKey());
        OCRUtils.setSecretKey(configuration.getBaiduOcrSecretKey());

    }

    private static void textInputDestroy() {
        clipboardListenerThread.exit();
        dragCopyThread.exit();
    }

    private static void processFilterDestroy() {
        activeWindowListenerThread.exit();
    }

    private static void filterInit(SystemConfiguration configuration) {
        ProcessFilter processFilter = new ProcessFilter();
        processFilter.setOpen(configuration.isOpenProcessFilter());
        processFilter.addFilterList(configuration.getProcessList());
        // 装载过滤器到剪切板监听线程
        clipboardListenerThread.setProcessFilter(processFilter);
        activeWindowListenerThread = new WindowsPlatformActiveWindowListenerThread();
        activeWindowListenerThread.start();
    }

    private static void preTextProcessInit(SystemConfiguration preProcessConfig) {
        TextPreProcessor textPreProcessor = new TextPreProcessor();
        textPreProcessor.setTryToFormat(preProcessConfig.isTryFormat());
        textPreProcessor.setIncrementalCopy(preProcessConfig.isIncrementalCopy());
        facade.setTextPreProcessor(textPreProcessor);
    }

    private static void postTextProcessInit(SystemConfiguration postProcessConfig) {
        TextPostProcessor textPostProcessor = new TextPostProcessor();
        textPostProcessor.getReplacer().setCaseInsensitive(postProcessConfig.isCaseInsensitive());
        textPostProcessor.getReplacer().addWords(postProcessConfig.getWordsPairs());
        textPostProcessor.getIndentProcessor().setAutoIndent(postProcessConfig.isTryFormat());
        facade.setTextPostProcessor(textPostProcessor);
    }

    private static void translatorInit(SystemConfiguration configuration) {
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
    }

    private static void historyInit(SystemConfiguration configuration) {
        TranslationHistory history = new TranslationHistory();
        facade.setHistory(history);
    }

    private static void afterProcessorInit(SystemConfiguration configuration) {
        AfterProcessor afterProcessor = new AfterProcessor();
        afterProcessor.setAutoCopy(configuration.isAutoCopy());
        afterProcessor.setAutoPaste(configuration.isAutoPaste());
        facade.setAfterProcessor(afterProcessor);
    }


}
