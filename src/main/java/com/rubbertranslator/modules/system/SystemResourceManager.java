package com.rubbertranslator.modules.system;

import com.google.gson.Gson;
import com.rubbertranslator.modules.TranslatorFacade;
import com.rubbertranslator.modules.filter.ProcessFilter;
import com.rubbertranslator.modules.textinput.clipboard.ClipBoardListenerThread;
import com.rubbertranslator.modules.textinput.mousecopy.DragCopyThread;
import com.rubbertranslator.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.modules.textprocessor.post.TextPostProcessor;
import com.rubbertranslator.modules.textprocessor.pre.TextPreProcessor;
import com.rubbertranslator.modules.translate.TranslatorFactory;
import com.rubbertranslator.modules.translate.TranslatorType;
import com.rubbertranslator.modules.translate.baidu.BaiduTranslator;
import com.rubbertranslator.modules.translate.youdao.YoudaoTranslator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/10 22:25
 * 系统资源管理
 */
public class SystemResourceManager {

    private static ClipBoardListenerThread clipBoardListenerThread;

    private static DragCopyThread dragCopyThread;

    private static TranslatorFacade facade;


    // 只能通过静态方法调用此类
    private SystemResourceManager() {
    }

    public static ClipBoardListenerThread getClipBoardListenerThread() {
        return clipBoardListenerThread;
    }

    public static DragCopyThread getDragCopyThread() {
        return dragCopyThread;
    }

    public static TranslatorFacade getFacade() {
        return facade;
    }

    /**
     * 初始化系统资源
     *
     * @return true 初始化成功
     * false 初始化失败
     */
    public static boolean init() {
        // 1. 加载配置文件
        SystemConfiguration configuration = loadSystemConfig();
        if(configuration == null) return false;
        // 2.初始化facade
         facade = new TranslatorFacade();
        // 3. 启动各组件
        return  textInputInit(configuration.getTextInputConfig()) &&
                filterInit(configuration.getProcessFilterConfig()) &&
                preTextProcessInit(configuration.getTextProcessConfig().getTextPreProcessConfig()) &&
                postTextProcessInit(configuration.getTextProcessConfig().getTextPostProcessConfig()) &&
                translatorInit(configuration.getTranslatorConfig());
    }

    /**
     * 释放资源
     */
    public static void destroy(){
        textInputDestroy();
        // 其余模块没有资源需要手动释放
        System.runFinalization();
        System.exit(0);
    }


    /**
     * 加载配置文件
     * @return  null 加载失败
     *           配置类 加载成功
     */
    private static SystemConfiguration loadSystemConfig() {
        // 加载本地目录
        String configJsonPath = "./configuration.json";
        File file = new File(configJsonPath);
        Path path;
        String configJson;
        try {
            if (file.exists()) {
                path = Paths.get(file.toURI());
            } else {
                // 不存在加载默认配置文件
                path = Paths.get(SystemConfiguration.class.getResource("/config/default_configuration.json").toURI());

            }
            configJson = Files.readString(path);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
            return null;
        }
        // json --> object
        Gson gson = new Gson();
        SystemConfiguration configuration = gson.fromJson(configJson, SystemConfiguration.class);
        if(configJson == null){
            return null;
        }else{
            return configuration;
        }
    }

    private static boolean textInputInit(SystemConfiguration.TextInputConfig configuration){
        clipBoardListenerThread = new ClipBoardListenerThread();
        clipBoardListenerThread.setRun(configuration.isOpenClipboardListener());
        dragCopyThread = new DragCopyThread();
        dragCopyThread.setRun(configuration.isDragCopy());
        OCRUtils.setApiKey(configuration.getBaiduOcrApiKey());
        OCRUtils.setSecretKey(configuration.getBaiduOcrSecretKey());

        clipBoardListenerThread.start();
        dragCopyThread.start();
        return true;
    }

    private static void textInputDestroy(){
        clipBoardListenerThread.exit();
        dragCopyThread.exit();
    }

    private static boolean filterInit(SystemConfiguration.ProcessFilterConfig configuration){
        ProcessFilter processFilter = new ProcessFilter();
        processFilter.setOpen(configuration.isOpenProcessFilter());
        processFilter.addFilterList(configuration.getProcessList());
        facade.setProcessFilter(processFilter);
        return true;
    }

    private static boolean preTextProcessInit(SystemConfiguration.TextProcessConfig.TextPreProcessConfig preProcessConfig){
        TextPreProcessor textPreProcessor = new TextPreProcessor();
        textPreProcessor.setTryToKeepParagraph(preProcessConfig.isTryKeepParagraphFormat());
        facade.setTextPreProcessor(textPreProcessor);
        return true;
    }

    private static boolean postTextProcessInit(SystemConfiguration.TextProcessConfig.TextPostProcessConfig postProcessConfig){
        TextPostProcessor textPostProcessor = new TextPostProcessor();
        textPostProcessor.setOpen(postProcessConfig.isOpenPostProcess());
        textPostProcessor.getReplacer().setCaseInsensitive(postProcessConfig.getWordsReplacerConfig().isCaseInsensitive());
        textPostProcessor.getReplacer().addWords(postProcessConfig.getWordsReplacerConfig().getWordsMap());
        facade.setTextPostProcessor(textPostProcessor);
        return true;
    }

    private static boolean translatorInit(SystemConfiguration.TranslatorConfig configuration){
        TranslatorFactory translatorFactory = new TranslatorFactory();
        translatorFactory.setEngineType(configuration.getCurrentTranslator());
        translatorFactory.setSourceLanguage(configuration.getSourceLanguage());
        translatorFactory.setDestLanguage(configuration.getDestLanguage());
        if(configuration.getBaiduTranslatorApiKey() != null && configuration.getBaiduTranslatorSecretKey() !=null){
            BaiduTranslator baiduTranslator = new BaiduTranslator();
            baiduTranslator.setAPP_KEY(configuration.getBaiduTranslatorApiKey());
            baiduTranslator.setSECRET_KEY(configuration.getBaiduTranslatorSecretKey());
            translatorFactory.addTranslator(TranslatorType.BAIDU,baiduTranslator);
        }
        if(configuration.getYouDaoTranslatorApiKey()!=null && configuration.getYouDaoTranslatorSecretKey() != null){
            YoudaoTranslator youdaoTranslator = new YoudaoTranslator();
            youdaoTranslator.setAPP_KEY(configuration.getYouDaoTranslatorApiKey());
            youdaoTranslator.setSECRET_KEY(configuration.getYouDaoTranslatorSecretKey());
            translatorFactory.addTranslator(TranslatorType.YOUDAO,youdaoTranslator);
        }
        facade.setTranslatorFactory(translatorFactory);
        return true;
    }



}
