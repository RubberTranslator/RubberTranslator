package com.rubbertranslator.modules.system.proxy;

import com.rubbertranslator.modules.system.SystemConfiguration;
import com.rubbertranslator.modules.system.SystemResourceManager;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/11 16:08
 */
public class TextPostProcessStaticConfig extends SystemConfiguration.TextProcessConfig.TextPostProcessConfig {
    private SystemConfiguration.TextProcessConfig.TextPostProcessConfig textPostProcessConfig;

    @Override
    public Boolean isOpenPostProcess() {
        return textPostProcessConfig.isOpenPostProcess();
    }

    public SystemConfiguration.TextProcessConfig.TextPostProcessConfig getTextPostProcessConfig() {
        return textPostProcessConfig;
    }

    @Override
    public WordsReplacerConfig getWordsReplacerConfig() {
        return textPostProcessConfig.getWordsReplacerConfig();
    }

    public TextPostProcessStaticConfig(SystemConfiguration.TextProcessConfig.TextPostProcessConfig textPostProcessConfig) {
        this.textPostProcessConfig = textPostProcessConfig;
    }

    public void setOpenPostProcess(Boolean openPostProcess) {
        textPostProcessConfig.setOpenPostProcess(openPostProcess);
        SystemResourceManager.getFacade().getTextPostProcessor().setOpen(openPostProcess);
    }
    public void setWordsReplacerConfig(WordsReplacerConfig wordsReplacerConfig) {
        textPostProcessConfig.setWordsReplacerConfig(wordsReplacerConfig);
        SystemResourceManager.getFacade().getTextPostProcessor().getReplacer()
                .setCaseInsensitive(wordsReplacerConfig.isCaseInsensitive());
        SystemResourceManager.getFacade().getTextPostProcessor().getReplacer()
                .setOpenWordsReplacer(wordsReplacerConfig.isOpenWordsReplacer());
        SystemResourceManager.getFacade().getTextPostProcessor().getReplacer()
                .setMap(wordsReplacerConfig.getWordsMap());
    }
}
