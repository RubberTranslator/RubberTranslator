package com.rubbertranslator.modules;

import com.rubbertranslator.modules.afterprocess.AfterProcessor;
import com.rubbertranslator.modules.filter.ProcessFilter;
import com.rubbertranslator.modules.history.TranslationHistory;
import com.rubbertranslator.modules.textprocessor.post.TextPostProcessor;
import com.rubbertranslator.modules.textprocessor.pre.TextPreProcessor;
import com.rubbertranslator.modules.translate.TranslatorFactory;

import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/8 10:00
 */
public class TranslatorFacade {

    // 进程过滤器
    private ProcessFilter processFilter;
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
    // facade回调
    private TranslatorFacadeListener facadeListener;

    // lastOrigin
    private String lastOrigin = "";
    // lastTranlsation
    private String lastTranslation = "";

    public void setFacadeListener(TranslatorFacadeListener facadeListener) {
        this.facadeListener = facadeListener;
    }

    public TranslatorFacade() {
        history = new TranslationHistory();
        executor = Executors.newSingleThreadExecutor();
        ;
    }


    /**
     * 后置所有模块清理
     */
    public void clear() {
        this.textPreProcessor.cleanBuffer();
    }

    public AfterProcessor getAfterProcessor() {
        return afterProcessor;
    }

    public void setAfterProcessor(AfterProcessor afterProcessor) {
        this.afterProcessor = afterProcessor;
    }

    public ProcessFilter getProcessFilter() {
        return processFilter;
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


    public void setProcessFilter(ProcessFilter processFilter) {
        this.processFilter = processFilter;
    }


    /**
     * 处理整个翻译过程
     *
     * @param text 原文
     * @return 成功 译文
     * 失败 null
     */
    public void process(String text) {
        if ((lastOrigin != null && lastOrigin.equals(text)) ||
                (lastTranslation != null && lastTranslation.equals(text))) {
            return;
        }
        Callable<String> callable = new FacadeCallable(text);
        FutureTask<String> task = new FacadeFutureTask(callable);
        executor.execute(task);
    }


    public interface TranslatorFacadeListener {
        void onComplete(String origin, String translation);
    }

    private class FacadeFutureTask extends FutureTask<String> {
        private final FacadeCallable callable;

        public FacadeFutureTask(Callable<String> callable) {
            super(callable);
            this.callable = (FacadeCallable) callable;
        }

        @Override
        protected void done() {
            if (facadeListener != null) {
                try {
                    String result = get();
                    String origin = callable.getText();
                    //XXX: 失败回调，硬编码为中文
                    if(result != null && facadeListener != null){
                        facadeListener.onComplete(origin, result);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class FacadeCallable implements Callable<String> {
        // text保存textProProcessor处理后的文本
        private String text;

        public String getText() {
            return text;
        }

        public FacadeCallable(String text) {
            this.text = text;
        }

        @Override
        public String call() throws Exception {
            if (text == null || "".equals(text)) return null;

            // lastOrigin 重新初始化
            lastOrigin = text;

            String origin = text;
            String translation = null;
            try {
                // 过滤
                if (processFilter.check()) return null;
                // text保存处理后的文本
                text = textPreProcessor.process(text);
                translation = translatorFactory.translate(text);
                // 后置处理
                translation = textPostProcessor.process(translation);
                // lastTranslation 重新初始化
                lastTranslation = translation;
                // 记录翻译历史
                history.addHistory(origin, translation);
                translation = afterProcessor.process(translation);
            } catch (NullPointerException e) {
                Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
            }
            return translation;
        }
    }
}
