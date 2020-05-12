package com.rubbertranslator.modules.system.proxy;

import com.rubbertranslator.modules.system.SystemConfiguration;
import com.rubbertranslator.modules.system.SystemResourceManager;
import com.rubbertranslator.modules.textprocessor.post.WordsPair;

import java.util.Set;

/**
 * WordReplacer静态代理
 */
public class WordsReplacerConfigStaticProxy extends SystemConfiguration.TextProcessConfig.TextPostProcessConfig.WordsReplacerConfig {
    private SystemConfiguration.TextProcessConfig.TextPostProcessConfig.WordsReplacerConfig wordsReplacerConfig;

    public SystemConfiguration.TextProcessConfig.TextPostProcessConfig.WordsReplacerConfig getWordsReplacerConfig() {
        return wordsReplacerConfig;
    }

    public WordsReplacerConfigStaticProxy(SystemConfiguration.TextProcessConfig.TextPostProcessConfig.WordsReplacerConfig wordsReplacerConfig) {
        this.wordsReplacerConfig = wordsReplacerConfig;
    }

    @Override
    public Boolean isCaseInsensitive() {
        return wordsReplacerConfig.isCaseInsensitive();
    }

    @Override
    public void setCaseInsensitive(Boolean caseInsensitive) {
        wordsReplacerConfig.setCaseInsensitive(caseInsensitive);
        SystemResourceManager.getFacade().getTextPostProcessor().getReplacer().setCaseInsensitive(caseInsensitive);
    }

    @Override
    public Boolean isOpenWordsReplacer() {
        return wordsReplacerConfig.isOpenWordsReplacer();
    }

    @Override
    public void setOpenWordsReplacer(Boolean openWordsReplacer) {
        wordsReplacerConfig.setOpenWordsReplacer(openWordsReplacer);
        SystemResourceManager.getFacade().getTextPostProcessor().getReplacer().setOpenWordsReplacer(openWordsReplacer);
    }

    @Override
    public Set<WordsPair> getWordsPairs() {
        return wordsReplacerConfig.getWordsPairs();
    }

    @Override
    public void setWordsPairs(Set<WordsPair> wordsPairs) {
        wordsReplacerConfig.setWordsPairs(wordsPairs);
        SystemResourceManager.getFacade().getTextPostProcessor().getReplacer().setWordsPairs(wordsPairs);
    }
}
