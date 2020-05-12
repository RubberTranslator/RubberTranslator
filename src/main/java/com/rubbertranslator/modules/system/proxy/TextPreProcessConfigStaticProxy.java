package com.rubbertranslator.modules.system.proxy;

import com.rubbertranslator.modules.system.SystemConfiguration;
import com.rubbertranslator.modules.system.SystemResourceManager;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/11 16:06
 */
public class TextPreProcessConfigStaticProxy extends SystemConfiguration.TextProcessConfig.TextPreProcessConfig {
    private SystemConfiguration.TextProcessConfig.TextPreProcessConfig preProcessConfig;


    public SystemConfiguration.TextProcessConfig.TextPreProcessConfig getPreProcessConfig() {
        return preProcessConfig;
    }

    @Override
    public Boolean isTryKeepParagraphFormat() {
        return preProcessConfig.isTryKeepParagraphFormat();
    }

    @Override
    public Boolean isIncrementalCopy() {
        return preProcessConfig.isIncrementalCopy();
    }

    @Override
    public void setIncrementalCopy(Boolean incrementalCopy) {
        preProcessConfig.setIncrementalCopy(incrementalCopy);
        SystemResourceManager.getFacade().getTextPreProcessor().setIncrementalCopy(incrementalCopy);
    }

    public TextPreProcessConfigStaticProxy(SystemConfiguration.TextProcessConfig.TextPreProcessConfig preProcessConfig) {
        this.preProcessConfig = preProcessConfig;
    }

    public void setTryKeepParagraphFormat(Boolean tryKeepParagraphFormat) {
        preProcessConfig.setTryKeepParagraphFormat(tryKeepParagraphFormat);
        SystemResourceManager.getFacade().getTextPreProcessor().setTryToKeepParagraph(tryKeepParagraphFormat);
    }
}
