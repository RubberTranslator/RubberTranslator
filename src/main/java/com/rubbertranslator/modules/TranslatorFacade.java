package com.rubbertranslator.modules;

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
    // facade回调
    private TranslatorFacadeListener facadeListener;

    // lastTranslation 保存最后的译文
    private String lastTranslation = "";


    public TranslatorFacade() {
        history = new TranslationHistory();
        executor = SystemResourceManager.getExecutor();
    }

    public void setFacadeListener(TranslatorFacadeListener facadeListener) {
        this.facadeListener = facadeListener;
    }


    /**
     * 后置所有模块清理
     */
    public void clear() {
        // 前置模块 --增量复制功能 buffer清理
        this.textPreProcessor.cleanBuffer();
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
     *
     * @param text 原文
     * @return 成功 译文
     * 失败 null
     */
    public void process(String text) {
        if (/*(lastOrigin != null && lastOrigin.equals(text)) ||*/
                // 当前输入text是否和上一次的译文相同
                //此condition用于排除“自动复制”所带来的二次翻译问题，但是对用户点击的二次翻译“原文”开放
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
                    String processedOrigin = callable.getProcessedOrigin();
                    if(result != null && facadeListener != null){
                        facadeListener.onComplete(processedOrigin, result);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,e.getLocalizedMessage(),e);
                }
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

            String translation = null;
            try {
                // text保存处理后的文本
                processedOrigin = textPreProcessor.process(origin);
                // 重新初始化lastOrigin
                // lastOrigin 保存最后格式化后的原文
                // lastOrigin = processedOrigin;
                translation = translatorFactory.translate(processedOrigin);
                // 后置处理
                translation = textPostProcessor.process(processedOrigin,translation);
                // lastTranslation 重新初始化
                lastTranslation = translation;
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
