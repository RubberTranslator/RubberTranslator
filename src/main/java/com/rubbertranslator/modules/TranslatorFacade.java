package com.rubbertranslator.modules;

import com.rubbertranslator.modules.filter.ProcessFilter;
import com.rubbertranslator.modules.history.TranslationHistory;
import com.rubbertranslator.modules.textprocessor.post.TextPostProcessor;
import com.rubbertranslator.modules.textprocessor.pre.TextPreProcessor;
import com.rubbertranslator.modules.translate.TranslatorFactory;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.logging.Level;
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
    // 创建线程池（使用了预定义的配置）
    private final ExecutorService executor;
    // facade回调
    private TranslatorFacadeListener facadeListener;

    public void setFacadeListener(TranslatorFacadeListener facadeListener) {
        this.facadeListener = facadeListener;
    }

    public TranslatorFacade() {
        history = new TranslationHistory();
        executor = Executors.newSingleThreadExecutor();;
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
     * @param text 原文
     * @return 成功 译文
     *          失败 null
     */
    public void process(String text) {
        Callable<String> callable = new FacadeCallable(text);
        FutureTask<String> task = new FacadeFutureTask(callable);
        executor.execute(task);
    }


    public interface TranslatorFacadeListener{
        void onComplete(String text);
    }

    private class FacadeFutureTask extends FutureTask<String>{

        public FacadeFutureTask(Callable<String> callable) {
            super(callable);
        }

        @Override
        protected void done() {
            if(facadeListener != null){
                try {
                    String result = get();
                    //XXX: 失败回调，硬编码为中文
                    Logger.getLogger(this.getClass().getName()).info(result);
                    Logger.getLogger(this.getClass().getName()).info(Thread.currentThread().getName());
                    facadeListener.onComplete(Objects.requireNonNullElse(result, "翻译失败"));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class FacadeCallable implements Callable<String>{

        private final String text;

        public FacadeCallable(String text) {
            this.text = text;
        }

        @Override
        public String call() throws Exception {
            if (text == null || "".equals(text)) return null;
            String temp = null;
            try{
                // 做一个判断检验
                // do translate works
                if (processFilter.check()) return null;
                temp = textPreProcessor.process(text);
                temp = translatorFactory.translate( temp);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, temp);
                // 后置处理
                temp = textPostProcessor.process(temp);
                // 记录翻译历史
                history.addHistory(text,temp);
            }catch (NullPointerException e){
                Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
            }
            return temp;
        }
    }
}
