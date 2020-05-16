package com.rubbertranslator.modules.system.proxy;

import com.rubbertranslator.modules.system.SystemConfiguration;
import com.rubbertranslator.modules.system.SystemResourceManager;
import com.rubbertranslator.modules.translate.Language;
import com.rubbertranslator.modules.translate.TranslatorType;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/11 16:16
 */
public class TranslatorConfigStaticProxy extends SystemConfiguration.TranslatorConfig {
    private SystemConfiguration.TranslatorConfig translatorConfig;


    @Override
    public TranslatorType getCurrentTranslator() {
        return translatorConfig.getCurrentTranslator();
    }

    @Override
    public Language getSourceLanguage() {
        return translatorConfig.getSourceLanguage();
    }

    @Override
    public Language getDestLanguage() {
        return translatorConfig.getDestLanguage();
    }

    @Override
    public String getBaiduTranslatorApiKey() {
        return translatorConfig.getBaiduTranslatorApiKey();
    }

    @Override
    public String getBaiduTranslatorSecretKey() {
        return translatorConfig.getBaiduTranslatorSecretKey();
    }

    @Override
    public String getYouDaoTranslatorApiKey() {
        return translatorConfig.getYouDaoTranslatorApiKey();
    }

    @Override
    public String getYouDaoTranslatorSecretKey() {
        return translatorConfig.getYouDaoTranslatorSecretKey();
    }

    public TranslatorConfigStaticProxy(SystemConfiguration.TranslatorConfig translatorConfig) {
        this.translatorConfig = translatorConfig;
    }

    @Override
    public void setCurrentTranslator(TranslatorType currentTranslator) {
        translatorConfig.setCurrentTranslator(currentTranslator);
        SystemResourceManager.getFacade().getTranslatorFactory().setEngineType(currentTranslator);
    }


    public SystemConfiguration.TranslatorConfig getTranslatorConfig() {
        return translatorConfig;
    }

    @Override
    public void setSourceLanguage(Language sourceLanguage) {
        translatorConfig.setSourceLanguage(sourceLanguage);
        SystemResourceManager.getFacade().getTranslatorFactory().setSourceLanguage(sourceLanguage);
    }

    @Override
    public void setDestLanguage(Language destLanguage) {
        translatorConfig.setDestLanguage(destLanguage);
        SystemResourceManager.getFacade().getTranslatorFactory().setDestLanguage(destLanguage);
    }

    @Override
    public void setBaiduTranslatorApiKey(String baiduTranslatorApiKey) {
        translatorConfig.setBaiduTranslatorApiKey(baiduTranslatorApiKey);
        SystemResourceManager.getFacade().getTranslatorFactory().getTranslator(TranslatorType.BAIDU).setAppKey(baiduTranslatorApiKey);
    }

    @Override
    public void setBaiduTranslatorSecretKey(String baiduTranslatorSecretKey) {
        translatorConfig.setBaiduTranslatorSecretKey(baiduTranslatorSecretKey);
        SystemResourceManager.getFacade().getTranslatorFactory().getTranslator(TranslatorType.BAIDU).setSecretKey(baiduTranslatorSecretKey);
    }

    @Override
    public void setYouDaoTranslatorApiKey(String youDaoTranslatorApiKey) {
        translatorConfig.setYouDaoTranslatorApiKey(youDaoTranslatorApiKey);
        SystemResourceManager.getFacade().getTranslatorFactory().getTranslator(TranslatorType.YOUDAO).setAppKey(youDaoTranslatorApiKey);
    }

    @Override
    public void setYouDaoTranslatorSecretKey(String youDaoTranslatorSecretKey) {
        translatorConfig.setYouDaoTranslatorSecretKey(youDaoTranslatorSecretKey);
        SystemResourceManager.getFacade().getTranslatorFactory().getTranslator(TranslatorType.YOUDAO).setSecretKey(youDaoTranslatorSecretKey);
    }
}
