package com.rubbertranslator.modules;

import com.rubbertranslator.entity.Pair;
import com.rubbertranslator.listener.GenericCallback;
import com.rubbertranslator.modules.afterprocess.AfterProcessor;
import com.rubbertranslator.modules.history.TranslationHistory;
import com.rubbertranslator.modules.textprocessor.post.TextPostProcessor;
import com.rubbertranslator.modules.textprocessor.pre.TextPreProcessor;
import com.rubbertranslator.modules.translate.TranslatorFactory;
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
     * 处理整个翻译过程
     * 最终处理结果会通过ranslatorFacadeListener#onComplete(java.lang.String, java.lang.String)回调
     * 成功：回调原文+译文
     * 失败：回调原文+null
     *
     * @param text 原文
     *             失败 null
     */
    public void process(String text, GenericCallback<Pair<String>> callback) {
        Callable<String> callable = new FacadeCallable(text);
        FutureTask<String> task = new FacadeFutureTask(callable,callback);
        executor.execute(task);
    }

    private class FacadeFutureTask extends FutureTask<String> {
        private final FacadeCallable callable;
        private GenericCallback<Pair<String>> callback;

        public FacadeFutureTask(Callable<String> callable,GenericCallback<Pair<String>> callback) {
            super(callable);
            this.callable = (FacadeCallable) callable;
            this.callback = callback;
        }

        @Override
        protected void done() {
            try {
                String result = get();  // 译文（如果有的话）
                String processedOrigin = callable.getProcessedOrigin(); // 原文
                callback.callBack(new Pair<>(processedOrigin,result));
            } catch (InterruptedException | ExecutionException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getLocalizedMessage(), e);
            }

        }
    }

    private class FacadeCallable implements Callable<String> {
        // 原文
        private String origin = "";
        // 格式处理后的原文
        private String processedOrigin = "";

        public String getProcessedOrigin() {
            return processedOrigin;
        }

        public String getOrigin() {
            return origin;
        }

        public FacadeCallable(String text) {
            this.origin = text;
        }

        @Override
        public String call() {
            if (origin == null || "".equals(origin)) return null;
            // facade处理开始
            String translation = null;
            try {
                // text保存处理后的文本
                processedOrigin = textPreProcessor.process(origin);
                // 重新初始化lastOrigin
                translation = translatorFactory.translate(processedOrigin);
                // 后置处理
                translation = textPostProcessor.process(processedOrigin, translation);
                // 记录翻译历史
                history.addHistory(processedOrigin, translation);
                translation = afterProcessor.process(translation);
            } catch (NullPointerException e) {
                Logger.getLogger(this.getClass().getName()).warning(e.getLocalizedMessage());
            }
            return translation;
        }
    }
}
