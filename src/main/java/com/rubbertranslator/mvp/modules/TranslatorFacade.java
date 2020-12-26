package com.rubbertranslator.mvp.modules;

import com.rubbertranslator.entity.Pair;
import com.rubbertranslator.enumtype.TranslatorType;
import com.rubbertranslator.listener.GenericCallback;
import com.rubbertranslator.mvp.modules.afterprocess.AfterProcessor;
import com.rubbertranslator.mvp.modules.history.TranslationHistory;
import com.rubbertranslator.mvp.modules.textprocessor.post.TextPostProcessor;
import com.rubbertranslator.mvp.modules.textprocessor.pre.TextPreProcessor;
import com.rubbertranslator.mvp.modules.translate.TranslatorFactory;
import com.rubbertranslator.system.SystemResourceManager;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/8 10:00
 */
public class TranslatorFacade {

    // 文本预处理器
    private TextPreProcessor textPreProcessor;
    // 翻译模块
    private TranslatorFactory translatorFactory;
    // 后置文本处理器
    private TextPostProcessor textPostProcessor;
    // 翻译历史记录
    private final TranslationHistory history;
    // 后置处理器
    private AfterProcessor afterProcessor;
    // 创建线程池（使用了预定义的配置）
    private final ExecutorService executor;

    public TranslatorFacade() {
        history = new TranslationHistory();
        executor = SystemResourceManager.getExecutor();
    }

    public AfterProcessor getAfterProcessor() {
        return afterProcessor;
    }

    public void setAfterProcessor(AfterProcessor afterProcessor) {
        this.afterProcessor = afterProcessor;
    }

    public TextPreProcessor getTextPreProcessor() {
        return textPreProcessor;
    }

    public TranslatorFactory getTranslatorFactory() {
        return translatorFactory;
    }

    public TextPostProcessor getTextPostProcessor() {
        return textPostProcessor;
    }

    public TranslationHistory getHistory() {
        return history;
    }

    public void setTextPreProcessor(TextPreProcessor textPreProcessor) {
        this.textPreProcessor = textPreProcessor;
    }

    public void setTranslatorFactory(TranslatorFactory translatorFactory) {
        this.translatorFactory = translatorFactory;
    }

    public void setTextPostProcessor(TextPostProcessor textPostProcessor) {
        this.textPostProcessor = textPostProcessor;
    }


    /**
     * 处理整个翻译过程 -- 单翻译
     * @param text 原文
     * @param callback 回调接口
     */
    public void singleTranslate(String text, GenericCallback<Pair<String,String>> callback) {
        Callable<Pair<String,String>> callable = new RelayTranslateCall(text);
        FutureTask<Pair<String,String>> task = new FacadeTask(callable,callback);
        executor.execute(task);
    }


    /**
     * 处理整个翻译 -- 多翻译
     * @param text 原文
     * @param callback 回调接口
     */
    public void multiTranslate(String text, GenericCallback<Pair<TranslatorType,String>> callback){
        Callable<Pair<TranslatorType,String>> googleCall = new SingleTranslateCall(text,TranslatorType.GOOGLE);
        Callable<Pair<TranslatorType,String>> baiduCall = new SingleTranslateCall(text,TranslatorType.BAIDU);
        Callable<Pair<TranslatorType,String>> youdaoCall = new SingleTranslateCall(text,TranslatorType.YOUDAO);

        FutureTask<Pair<TranslatorType,String>> googleTask = new FacadeTask(googleCall,callback);
        FutureTask<Pair<TranslatorType,String>> baiduTask = new FacadeTask(baiduCall,callback);
        FutureTask<Pair<TranslatorType,String>> youdaoTask = new FacadeTask(youdaoCall,callback);

        // exec
        executor.execute(googleTask);
        executor.execute(baiduTask);
        executor.execute(youdaoTask);
    }



    private class FacadeTask<T> extends FutureTask<T> {

        private GenericCallback<T> callback;

        public FacadeTask(Callable<T> callable, GenericCallback<T> callback) {
            super(callable);
            this.callback = callback;
        }

        @Override
        protected void done() {
            try {
                T result = get();  // 获取结果
                callback.callBack(result);
            } catch (InterruptedException | ExecutionException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getLocalizedMessage(), e);
            }

        }
    }

    /**
     * 接力翻译 -- 即 若google翻译失败，自动使用百度翻译
     * 如果都失败，返回 “” 空字符串
     */
    private class RelayTranslateCall implements Callable<Pair<String,String>> {
        // 原文
        private String origin = "";

        public RelayTranslateCall(String text) {
            this.origin = text;
        }

        @Override
        public Pair<String,String> call() {
            if (origin == null || "".equals(origin)) return null;
            // facade处理开始
            String translation = null;
            try {
                // text保存处理后的文本
                origin = textPreProcessor.process(origin);
                // 重新初始化lastOrigin
                translation = translatorFactory.translate(origin);
                // 后置处理
                translation = textPostProcessor.process(origin, translation);
                // 记录翻译历史
                history.addHistory(origin, translation);
                afterProcessor.process(translation);
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).warning(e.getLocalizedMessage());
            }
            return new Pair<>(origin, translation == null ? "" : translation);
        }
    }


    /**
     *  单翻译 --按照指定类型翻译
     *  如果翻译不成功，直接返回 "" 空字符串
     */
    private class SingleTranslateCall implements Callable<Pair<TranslatorType,String>>{

        private String origin = ""; // 原文

        TranslatorType type;    // 当前类型

        public SingleTranslateCall(String origin, TranslatorType type) {
            this.origin = origin;
            this.type = type;
        }

        @Override
        public Pair<TranslatorType, String> call() throws Exception {
            if (origin == null || "".equals(origin)) return null;
            // facade处理开始
            String translation = null;
            try {
                // text保存处理后的文本
                origin = textPreProcessor.process(origin);
                // 重新初始化lastOrigin
                translation = translatorFactory.translateByType(type,origin);
                // 后置处理
                translation = textPostProcessor.process(origin, translation);
                // 记录翻译历史
                history.addHistory(origin, translation);
                afterProcessor.process(translation);
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).warning(e.getLocalizedMessage());
            }
            return new Pair<>(type,  translation == null ? "" : translation);
        }
    }
}
