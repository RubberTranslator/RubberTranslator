package com.rubbertranslator.manager;

import com.rubbertranslator.modules.filter.ProcessFilter;
import com.rubbertranslator.modules.textpreprocessor.TextPreProcessor;
import org.jetbrains.annotations.NotNull;
import org.jnativehook.GlobalScreen;

import java.util.logging.Level;
import java.util.logging.LogManager;
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

    public TranslatorFacade() {
    }


    public void setProcessFilter(@NotNull ProcessFilter processFilter) {
        this.processFilter = processFilter;
    }


    public void setTextPreProcessor(@NotNull TextPreProcessor textPreProcessor) {
        this.textPreProcessor = textPreProcessor;
    }


    public void process(String text){
        // 做一个判断检验
        // do translate works
        if(processFilter.check()) return;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,text);
        text = textPreProcessor.process(text);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,"处理后："+text);
    }
}
