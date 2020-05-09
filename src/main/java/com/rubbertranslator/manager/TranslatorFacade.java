package com.rubbertranslator.manager;

import com.rubbertranslator.modules.filter.ProcessFilter;
import com.rubbertranslator.modules.textprocessor.post.TextPostProcessor;
import com.rubbertranslator.modules.textprocessor.pre.TextPreProcessor;
import com.rubbertranslator.modules.translate.Language;
import com.rubbertranslator.modules.translate.TranslatorFactory;
import com.rubbertranslator.modules.translate.TranslatorType;
import org.jetbrains.annotations.NotNull;

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
    private TranslatorFactory translator;
    // 后置文本处理器
    private TextPostProcessor textPostProcessor;

    public TranslatorFacade() {
        textPreProcessor = new TextPreProcessor();
        translator = new TranslatorFactory();
        textPostProcessor = new TextPostProcessor();

        textPreProcessor.setTryToKeepParagraph(true);
        translator.setEngineType(TranslatorType.BAIDU);
        textPostProcessor.setOpen(true);

        textPostProcessor.getReplacer().setCaseInsensitive(false);
    }

    public void setProcessFilter(@NotNull ProcessFilter processFilter) {
        this.processFilter = processFilter;
    }


    public void process(String text) {
        if (text == null || "".equals(text)) return;

        String temp = text;
        String translated;
        // 做一个判断检验
        // do translate works
        if (processFilter.check()) return;
        temp = textPreProcessor.process(text);
        temp = translator.translate(Language.ENGLISH, Language.CHINESE_SIMPLIFIED, temp);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, temp);
        // 后置处理
        translated = textPostProcessor.process(temp);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, translated);
    }
}
