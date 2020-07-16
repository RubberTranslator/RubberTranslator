package com.rubbertranslator.modules.config;

import com.rubbertranslator.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.modules.textprocessor.post.WordsPair;
import com.rubbertranslator.modules.translate.Language;
import com.rubbertranslator.modules.translate.TranslatorType;
import com.rubbertranslator.system.SystemResourceManager;

import java.util.List;
import java.util.Set;

public class SystemConfigurationStaticProxy extends SystemConfiguration{
    private SystemConfiguration configuration;

    public SystemConfigurationStaticProxy(SystemConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SystemConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * history
     * @return
     */
    @Override
    public Integer getHistoryNum() {
        return configuration.getHistoryNum();
    }
    
    @Override
    public void setHistoryNum(Integer historyNum) {
        configuration.setHistoryNum(historyNum);
        SystemResourceManager.getFacade().getHistory().setHistoryCapacity(historyNum);
    }


    /**
     * Translator
     * @return
     */
    @Override
    public TranslatorType getCurrentTranslator() {
        return configuration.getCurrentTranslator();
    }

    @Override
    public Language getSourceLanguage() {
        return configuration.getSourceLanguage();
    }

    @Override
    public Language getDestLanguage() {
        return configuration.getDestLanguage();
    }

    @Override
    public String getBaiduTranslatorApiKey() {
        return configuration.getBaiduTranslatorApiKey();
    }

    @Override
    public String getBaiduTranslatorSecretKey() {
        return configuration.getBaiduTranslatorSecretKey();
    }

    @Override
    public String getYouDaoTranslatorApiKey() {
        return configuration.getYouDaoTranslatorApiKey();
    }

    @Override
    public String getYouDaoTranslatorSecretKey() {
        return configuration.getYouDaoTranslatorSecretKey();
    }

  
    @Override
    public void setCurrentTranslator(TranslatorType currentTranslator) {
        configuration.setCurrentTranslator(currentTranslator);
        SystemResourceManager.getFacade().getTranslatorFactory().setEngineType(currentTranslator);
    }


    @Override
    public void setSourceLanguage(Language sourceLanguage) {
        configuration.setSourceLanguage(sourceLanguage);
        SystemResourceManager.getFacade().getTranslatorFactory().setSourceLanguage(sourceLanguage);
    }

    @Override
    public void setDestLanguage(Language destLanguage) {
        configuration.setDestLanguage(destLanguage);
        SystemResourceManager.getFacade().getTranslatorFactory().setDestLanguage(destLanguage);
    }

    @Override
    public void setBaiduTranslatorApiKey(String baiduTranslatorApiKey) {
        configuration.setBaiduTranslatorApiKey(baiduTranslatorApiKey);
        SystemResourceManager.getFacade().getTranslatorFactory().getTranslator(TranslatorType.BAIDU).setAppKey(baiduTranslatorApiKey);
    }

    @Override
    public void setBaiduTranslatorSecretKey(String baiduTranslatorSecretKey) {
        configuration.setBaiduTranslatorSecretKey(baiduTranslatorSecretKey);
        SystemResourceManager.getFacade().getTranslatorFactory().getTranslator(TranslatorType.BAIDU).setSecretKey(baiduTranslatorSecretKey);
    }

    @Override
    public void setYouDaoTranslatorApiKey(String youDaoTranslatorApiKey) {
        configuration.setYouDaoTranslatorApiKey(youDaoTranslatorApiKey);
        SystemResourceManager.getFacade().getTranslatorFactory().getTranslator(TranslatorType.YOUDAO).setAppKey(youDaoTranslatorApiKey);
    }

    @Override
    public void setYouDaoTranslatorSecretKey(String youDaoTranslatorSecretKey) {
        configuration.setYouDaoTranslatorSecretKey(youDaoTranslatorSecretKey);
        SystemResourceManager.getFacade().getTranslatorFactory().getTranslator(TranslatorType.YOUDAO).setSecretKey(youDaoTranslatorSecretKey);
    }

    /**
     * 前置文本处理
     * @return
     */
    @Override
    public Boolean isTryToFormat() {
        return configuration.isTryToFormat();
    }

    @Override
    public Boolean isIncrementalCopy() {
        return configuration.isIncrementalCopy();
    }
    
    @Override
    public void setTryToFormat(Boolean tryToFormat) {
        configuration.setTryToFormat(tryToFormat);
        SystemResourceManager.getFacade().getTextPreProcessor().setTryToFormat(tryToFormat);
    }

    
    @Override
    public void setIncrementalCopy(Boolean incrementalCopy) {
        configuration.setIncrementalCopy(incrementalCopy);
    }

    /**
     * 后置处理
     * @return
     */
    @Override
    public Boolean isAutoCopy() {
        return configuration.isAutoCopy();
    }

    @Override
    public void setAutoCopy(Boolean autoCopy) {
        configuration.setAutoCopy(autoCopy);
        SystemResourceManager.getFacade().getAfterProcessor().setAutoCopy(autoCopy);
    }

    @Override
    public Boolean isAutoPaste() {
        return configuration.isAutoPaste();
    }

    @Override
    public void setAutoPaste(Boolean autoPaste) {
        configuration.setAutoPaste(autoPaste);
        SystemResourceManager.getFacade().getAfterProcessor().setAutoPaste(autoPaste);
    }

    /**
     * ui
     * @return
     */
    @Override
    public String getStyleCssPath() {
        return configuration.getStyleCssPath();
    }

    @Override
    public void setStyleCssPath(String styleCssPath) {
        configuration.setStyleCssPath(styleCssPath);
    }

    @Override
    public Boolean isKeepTop() {
        return configuration.isKeepTop();
    }

    @Override
    public void setKeepTop(Boolean keepTop) {
        configuration.setKeepTop(keepTop);
    }

    /**
     * 文本输入
     * @return
     */
    @Override
    public Boolean isOpenClipboardListener() {
        return configuration.isOpenClipboardListener();
    }

    @Override
    public Boolean isDragCopy() {
        return configuration.isDragCopy();
    }

    @Override
    public String getBaiduOcrApiKey() {
        return configuration.getBaiduOcrApiKey();
    }

    @Override
    public String getBaiduOcrSecretKey() {
        return configuration.getBaiduOcrSecretKey();
    }
    
    @Override
    public void setOpenClipboardListener(Boolean openClipboardListener) {
        configuration.setOpenClipboardListener(openClipboardListener);
        SystemResourceManager.getClipboardListenerThread().setRun(openClipboardListener);
    }
    
    @Override
    public void setDragCopy(Boolean dragCopy) {
        configuration.setDragCopy(dragCopy);
        SystemResourceManager.getDragCopyThread().setRun(dragCopy);
    }
    
    @Override
    public void setBaiduOcrApiKey(String baiduOcrApiKey) {
        configuration.setBaiduOcrApiKey(baiduOcrApiKey);
        OCRUtils.setApiKey(baiduOcrApiKey);
    }
    
    @Override
    public void setBaiduOcrSecretKey(String baiduOcrSecretKey) {
        configuration.setBaiduOcrSecretKey(baiduOcrSecretKey);
        OCRUtils.setSecretKey(baiduOcrSecretKey);
    }

    /**
     * 进程过滤
     * @return
     */
    @Override
    public Boolean isOpenProcessFilter() {
        return configuration.isOpenProcessFilter();
    }


    @Override
    public List<String> getProcessList() {
        return configuration.getProcessList();
    }

    @Override
    public void setOpenProcessFilter(Boolean openProcessFilter) {
        configuration.setOpenProcessFilter(openProcessFilter);
        SystemResourceManager.getClipboardListenerThread().getProcessFilter().setOpen(openProcessFilter);
    }

    @Override
    public void setProcessList(List<String> processList) {
        configuration.setProcessList(processList);
        SystemResourceManager.getClipboardListenerThread().getProcessFilter().setFilterList(processList);
    }


    /**
     * 文本后置处理
     * @return
     */
    @Override
    public Boolean isCaseInsensitive() {
        return configuration.isCaseInsensitive();
    }

    @Override
    public void setCaseInsensitive(Boolean caseInsensitive) {
        configuration.setCaseInsensitive(caseInsensitive);
        SystemResourceManager.getFacade().getTextPostProcessor().getReplacer().setCaseInsensitive(caseInsensitive);
    }

    @Override
    public Boolean isOpenWordsReplacer() {
        return configuration.isOpenWordsReplacer();
    }

    @Override
    public void setOpenWordsReplacer(Boolean openWordsReplacer) {
        configuration.setOpenWordsReplacer(openWordsReplacer);
        SystemResourceManager.getFacade().getTextPostProcessor().getReplacer().setOpenWordsReplacer(openWordsReplacer);
    }

    @Override
    public Set<WordsPair> getWordsPairs() {
        return configuration.getWordsPairs();
    }

    @Override
    public void setWordsPairs(Set<WordsPair> wordsPairs) {
        configuration.setWordsPairs(wordsPairs);
        SystemResourceManager.getFacade().getTextPostProcessor().getReplacer().setWordsPairs(wordsPairs);
    }
}
