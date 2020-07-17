package com.rubbertranslator.system;

import com.google.gson.annotations.SerializedName;
import com.rubbertranslator.entity.WordsPair;
import com.rubbertranslator.enumtype.Language;
import com.rubbertranslator.enumtype.TranslatorType;

import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/10 21:02
 * 系统配置类
 */
public class SystemConfiguration implements Serializable {
    /**
     * ui
     */
    @SerializedName("keep_top")
    private Boolean isKeepTop;
    @SerializedName("custom_css_path")
    private String styleCssPath;
    @SerializedName("last_position")
    private Point lastPos;
    @SerializedName("last_size")
    private Point lastSize;
    @SerializedName("last_fxml_path")
    private String lastFxmlPath;
    @SerializedName("auto_hide")
    private Boolean isAutoHide;

    /**
     * 文本输入
     */
    // 是否开启监听剪切板
    @SerializedName("open_clipboard_listener")
    private Boolean isOpenClipboardListener;
    // 是否开启拖拽复制
    @SerializedName("drag_copy")
    private Boolean isDragCopy;
    // ocr
    @SerializedName("baidu_ocr_api_key")
    private String baiduOcrApiKey;
    @SerializedName("baidu_ocr_secret_key")
    private String baiduOcrSecretKey;

    /**
     * 进程过滤
     */
    // 是否打开进程过滤器
    @SerializedName("open_process_filter")
    private Boolean isOpenProcessFilter;
    // TODO: 考虑加入黑白名单模式
    // 过滤进程集合 TODO:如果有黑白名单，则用两个进程list来表示，如whiteProcessList
    @SerializedName("process_list")
    private List<String> processList;

    /**
     * 后置处理
     */
    @SerializedName("auto_copy")
    private Boolean isAutoCopy;
    @SerializedName("auto_paste")
    private Boolean isAutoPaste;

    /**
     * 前置文本处理
     */
    @SerializedName("try_format")
    private Boolean isTryFormat;
    @SerializedName("incremental_copy")
    private Boolean isIncrementalCopy;

    /**
     * 后置文本处理
     */
    // 是否打开词组替换
    @SerializedName("open_words_replacer")
    private Boolean isOpenWordsReplacer;
    // 是否大小写敏感
    @SerializedName("case_insensitive")
    private Boolean isCaseInsensitive;
    // 词组替换集合
    @SerializedName("words_pairs")
    private Set<WordsPair> wordsPairs;

    /**
     * 翻译模块
     */
    // 当前翻译类型
    @SerializedName("current_translator")
    private TranslatorType currentTranslator;
    // 翻译源语言
    @SerializedName("source_language")
    private Language sourceLanguage;
    // 翻译目标语言
    @SerializedName("dest_language")
    private Language destLanguage;
    // 百度翻译key&value
    @SerializedName("baidu_translator_api_key")
    private String baiduTranslatorApiKey;
    @SerializedName("baidu_translator_secret_key")
    private String baiduTranslatorSecretKey;
    // 有道key&value
    @SerializedName("you_dao_translator_api_key")
    private String youDaoTranslatorApiKey;
    @SerializedName("you_dao_translator_secret_key")
    private String youDaoTranslatorSecretKey;

    /**
     * 翻译历史
     */
    // 翻译历史记录条数
    @SerializedName("history_num")
    private Integer historyNum;


    public SystemConfiguration getConfiguration(){
        return this;
    }

    public Boolean isKeepTop() {
        return isKeepTop;
    }

    public void setKeepTop(Boolean keepTop) {
        isKeepTop = keepTop;
    }

    public String getStyleCssPath() {
        return styleCssPath;
    }

    public void setStyleCssPath(String styleCssPath) {
        this.styleCssPath = styleCssPath;
    }

    public Point getLastPos() {
        return lastPos;
    }

    public void setLastPos(Point lastPos) {
        this.lastPos = lastPos;
    }

    public Point getLastSize() {
        return lastSize;
    }

    public void setLastSize(Point lastSize) {
        this.lastSize = lastSize;
    }

    public String getLastFxmlPath() {
        return lastFxmlPath;
    }

    public void setLastFxmlPath(String lastFxmlPath) {
        this.lastFxmlPath = lastFxmlPath;
    }

    public Boolean isAutoHide() {
        return isAutoHide;
    }

    public void setAutoHide(Boolean autoHide) {
        isAutoHide = autoHide;
    }

    public Boolean isOpenClipboardListener() {
        return isOpenClipboardListener;
    }

    public void setOpenClipboardListener(Boolean openClipboardListener) {
        isOpenClipboardListener = openClipboardListener;
    }

    public Boolean isDragCopy() {
        return isDragCopy;
    }

    public void setDragCopy(Boolean dragCopy) {
        isDragCopy = dragCopy;
    }

    public String getBaiduOcrApiKey() {
        return baiduOcrApiKey;
    }

    public void setBaiduOcrApiKey(String baiduOcrApiKey) {
        this.baiduOcrApiKey = baiduOcrApiKey;
    }

    public String getBaiduOcrSecretKey() {
        return baiduOcrSecretKey;
    }

    public void setBaiduOcrSecretKey(String baiduOcrSecretKey) {
        this.baiduOcrSecretKey = baiduOcrSecretKey;
    }

    public Boolean isOpenProcessFilter() {
        return isOpenProcessFilter;
    }

    public void setOpenProcessFilter(Boolean openProcessFilter) {
        isOpenProcessFilter = openProcessFilter;
    }

    public List<String> getProcessList() {
        return processList;
    }

    public void setProcessList(List<String> processList) {
        this.processList = processList;
    }

    public Boolean isAutoCopy() {
        return isAutoCopy;
    }

    public void setAutoCopy(Boolean autoCopy) {
        isAutoCopy = autoCopy;
    }

    public Boolean isAutoPaste() {
        return isAutoPaste;
    }

    public void setAutoPaste(Boolean autoPaste) {
        isAutoPaste = autoPaste;
    }

    public Boolean isTryFormat() {
        return isTryFormat;
    }

    public void setTryFormat(Boolean tryFormat) {
        isTryFormat = tryFormat;
    }

    public Boolean isIncrementalCopy() {
        return isIncrementalCopy;
    }

    public void setIncrementalCopy(Boolean incrementalCopy) {
        isIncrementalCopy = incrementalCopy;
    }

    public Boolean isOpenWordsReplacer() {
        return isOpenWordsReplacer;
    }

    public void setOpenWordsReplacer(Boolean openWordsReplacer) {
        isOpenWordsReplacer = openWordsReplacer;
    }

    public Boolean isCaseInsensitive() {
        return isCaseInsensitive;
    }

    public void setCaseInsensitive(Boolean caseInsensitive) {
        isCaseInsensitive = caseInsensitive;
    }

    public Set<WordsPair> getWordsPairs() {
        return wordsPairs;
    }

    public void setWordsPairs(Set<WordsPair> wordsPairs) {
        this.wordsPairs = wordsPairs;
    }

    public TranslatorType getCurrentTranslator() {
        return currentTranslator;
    }

    public void setCurrentTranslator(TranslatorType currentTranslator) {
        this.currentTranslator = currentTranslator;
    }

    public Language getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(Language sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public Language getDestLanguage() {
        return destLanguage;
    }

    public void setDestLanguage(Language destLanguage) {
        this.destLanguage = destLanguage;
    }

    public String getBaiduTranslatorApiKey() {
        return baiduTranslatorApiKey;
    }

    public void setBaiduTranslatorApiKey(String baiduTranslatorApiKey) {
        this.baiduTranslatorApiKey = baiduTranslatorApiKey;
    }

    public String getBaiduTranslatorSecretKey() {
        return baiduTranslatorSecretKey;
    }

    public void setBaiduTranslatorSecretKey(String baiduTranslatorSecretKey) {
        this.baiduTranslatorSecretKey = baiduTranslatorSecretKey;
    }

    public String getYouDaoTranslatorApiKey() {
        return youDaoTranslatorApiKey;
    }

    public void setYouDaoTranslatorApiKey(String youDaoTranslatorApiKey) {
        this.youDaoTranslatorApiKey = youDaoTranslatorApiKey;
    }

    public String getYouDaoTranslatorSecretKey() {
        return youDaoTranslatorSecretKey;
    }

    public void setYouDaoTranslatorSecretKey(String youDaoTranslatorSecretKey) {
        this.youDaoTranslatorSecretKey = youDaoTranslatorSecretKey;
    }

    public Integer getHistoryNum() {
        return historyNum;
    }

    public void setHistoryNum(Integer historyNum) {
        this.historyNum = historyNum;
    }
}
