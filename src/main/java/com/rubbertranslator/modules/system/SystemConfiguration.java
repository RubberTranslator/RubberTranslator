package com.rubbertranslator.modules.system;

import com.google.gson.annotations.SerializedName;
import com.rubbertranslator.modules.textprocessor.post.WordsPair;
import com.rubbertranslator.modules.translate.Language;
import com.rubbertranslator.modules.translate.TranslatorType;

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
    @SerializedName("ui_config")
    private UIConfig uiConfig;
    // 文本输入
    @SerializedName("text_input")
    private TextInputConfig textInputConfig;
    // 进程过滤
    @SerializedName("process_filter")
    private ProcessFilterConfig processFilterConfig;
    // 文本处理
    @SerializedName("text_process")
    private TextProcessConfig textProcessConfig;
    // 翻译模块
    @SerializedName("translator")
    private TranslatorConfig translatorConfig;
    
    @SerializedName("history")
    private HistoryConfig historyConfig;

    @SerializedName("after_processor_config")
    private AfterProcessorConfig afterProcessorConfig;


    @Override
    public String toString() {
        return "SystemConfiguration{" +
                "uiConfig=" + uiConfig +
                ", textInputConfig=" + textInputConfig +
                ", processFilterConfig=" + processFilterConfig +
                ", textProcessConfig=" + textProcessConfig +
                ", translatorConfig=" + translatorConfig +
                ", historyConfig=" + historyConfig +
                ", afterProcessorConfig=" + afterProcessorConfig +
                '}';
    }

    public AfterProcessorConfig getAfterProcessorConfig() {
        return afterProcessorConfig;
    }

    public void setAfterProcessorConfig(AfterProcessorConfig afterProcessorConfig) {
        this.afterProcessorConfig = afterProcessorConfig;
    }

    public UIConfig getUiConfig() {
        return uiConfig;
    }

    public void setUiConfig(UIConfig uiConfig) {
        this.uiConfig = uiConfig;
    }

    public ProcessFilterConfig getProcessFilterConfig() {
        return processFilterConfig;
    }

    public void setProcessFilterConfig(ProcessFilterConfig processFilterConfig) {
        this.processFilterConfig = processFilterConfig;
    }

    public HistoryConfig getHistoryConfig() {
        return historyConfig;
    }

    public void setHistoryConfig(HistoryConfig historyConfig) {
        this.historyConfig = historyConfig;
    }

    public TextProcessConfig getTextProcessConfig() {
        return textProcessConfig;
    }

    public void setTextProcessConfig(TextProcessConfig textProcessConfig) {
        this.textProcessConfig = textProcessConfig;
    }

    public TextInputConfig getTextInputConfig() {
        return textInputConfig;
    }

    public void setTextInputConfig(TextInputConfig textInputConfig) {
        this.textInputConfig = textInputConfig;
    }

    public TranslatorConfig getTranslatorConfig() {
        return translatorConfig;
    }

    public void setTranslatorConfig(TranslatorConfig translatorConfig) {
        this.translatorConfig = translatorConfig;
    }


    /**
     * 文本输入模块配置
     */
    public static class TextInputConfig implements Serializable{
        // 是否开启监听剪切板
        @SerializedName("open_clipboard_listener")
        private Boolean openClipboardListener;
        // 是否开启拖拽复制
        @SerializedName("drag_copy")
        private Boolean dragCopy;
        // ocr
        @SerializedName("baidu_ocr_api_key")
        private String baiduOcrApiKey;
        @SerializedName("baidu_ocr_secret_key")
        private String baiduOcrSecretKey;

        @Override
        public String toString() {
            return "TextInputConfig{" +
                    "openClipboardListener=" + openClipboardListener +
                    ", dragCopy=" + dragCopy +
                    ", baiduOcrApiKey='" + baiduOcrApiKey + '\'' +
                    ", baiduOcrSecretKey='" + baiduOcrSecretKey + '\'' +
                    '}';
        }

        public Boolean isOpenClipboardListener() {
            return openClipboardListener;
        }

        public void setOpenClipboardListener(Boolean openClipboardListener) {
            this.openClipboardListener = openClipboardListener;
        }

        public Boolean isDragCopy() {
            return dragCopy;
        }

        public void setDragCopy(Boolean dragCopy) {
            this.dragCopy = dragCopy;
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
    }

    public static class ProcessFilterConfig implements Serializable{
        // 是否打开进程过滤器
        @SerializedName("open_process_filter")
        private Boolean openProcessFilter;
        // TODO: 考虑加入黑白名单模式
        // 过滤进程集合 TODO:如果有黑白名单，则用两个进程list来表示，如whiteProcessList
        @SerializedName("process_list")
        private List<String> processList;

        public Boolean isOpenProcessFilter() {
            return openProcessFilter;
        }

        public void setOpenProcessFilter(Boolean openProcessFilter) {
            this.openProcessFilter = openProcessFilter;
        }

        public List<String> getProcessList() {
            return processList;
        }

        public void setProcessList(List<String> processList) {
            this.processList = processList;
        }

        @Override
        public String toString() {
            return "ProcessFilterConfig{" +
                    "openProcessFilter=" + openProcessFilter +
                    ", processList=" + processList +
                    '}';
        }
    }

    /**
     * 文本处理模块配置
     */
    public static class TextProcessConfig implements Serializable{
        // 前置处理
        @SerializedName("text_pre_process")
        private TextPreProcessConfig textPreProcessConfig;
        // 后置处理
        @SerializedName("text_post_process")
        private TextPostProcessConfig textPostProcessConfig;

        public TextPreProcessConfig getTextPreProcessConfig() {
            return textPreProcessConfig;
        }

        public void setTextPreProcessConfig(TextPreProcessConfig textPreProcessConfig) {
            this.textPreProcessConfig = textPreProcessConfig;
        }

        public TextPostProcessConfig getTextPostProcessConfig() {
            return textPostProcessConfig;
        }

        public void setTextPostProcessConfig(TextPostProcessConfig textPostProcessConfig) {
            this.textPostProcessConfig = textPostProcessConfig;
        }

        @Override
        public String toString() {
            return "TextProcessConfig{" +
                    "textPreProcessConfig=" + textPreProcessConfig +
                    ", textPostProcessConfig=" + textPostProcessConfig +
                    '}';
        }

        /**
         * 前置处理
         */
        public static class TextPreProcessConfig implements Serializable{
            @SerializedName("keep_paragraph_format")
            private Boolean tryKeepParagraphFormat;

            private Boolean incrementalCopy;


            public Boolean isTryKeepParagraphFormat() {
                return tryKeepParagraphFormat;
            }

            public void setTryKeepParagraphFormat(Boolean tryKeepParagraphFormat) {
                this.tryKeepParagraphFormat = tryKeepParagraphFormat;
            }

            public Boolean isIncrementalCopy() {
                return incrementalCopy;
            }

            public void setIncrementalCopy(Boolean incrementalCopy) {
                this.incrementalCopy = incrementalCopy;
            }

            @Override
            public String toString() {
                return "TextPreProcessConfig{" +
                        "tryKeepParagraphFormat=" + tryKeepParagraphFormat +
                        ", incrementalCopy=" + incrementalCopy +
                        '}';
            }
        }

        /**
         * 后置处理
         */
        public static class TextPostProcessConfig{
            @SerializedName("open_post_process")
            private Boolean openPostProcess;

            @SerializedName("words_replacer")
            private WordsReplacerConfig wordsReplacerConfig;

            public Boolean isOpenPostProcess() {
                return openPostProcess;
            }

            public void setOpenPostProcess(Boolean openPostProcess) {
                this.openPostProcess = openPostProcess;
            }

            @Override
            public String toString() {
                return "TextPostProcessConfig{" +
                        "openPostProcess=" + openPostProcess +
                        ", wordsReplacerConfig=" + wordsReplacerConfig +
                        '}';
            }

            public WordsReplacerConfig getWordsReplacerConfig() {
                return wordsReplacerConfig;
            }

            public void setWordsReplacerConfig(WordsReplacerConfig wordsReplacerConfig) {
                this.wordsReplacerConfig = wordsReplacerConfig;
            }

            public static class WordsReplacerConfig implements Serializable{
                // 是否打开词组替换
                @SerializedName("open_words_replacer")
                private Boolean openWordsReplacer;
                // 是否大小写敏感
                @SerializedName("case_insensitive")
                private Boolean caseInsensitive;
                // 词组替换集合
                @SerializedName("words_pairs")
                private Set<WordsPair> wordsPairs;

                public Boolean isCaseInsensitive() {
                    return caseInsensitive;
                }

                public void setCaseInsensitive(Boolean caseInsensitive) {
                    this.caseInsensitive = caseInsensitive;
                }

                public Boolean isOpenWordsReplacer() {
                    return openWordsReplacer;
                }

                public void setOpenWordsReplacer(Boolean openWordsReplacer) {
                    this.openWordsReplacer = openWordsReplacer;
                }

                public Set<WordsPair> getWordsPairs() {
                    return wordsPairs;
                }

                public void setWordsPairs(Set<WordsPair> wordsPairs) {
                    this.wordsPairs = wordsPairs;
                }

                @Override
                public String toString() {
                    return "WordsReplacerConfig{" +
                            "openWordsReplacer=" + openWordsReplacer +
                            ", caseInsensitive=" + caseInsensitive +
                            ", wordsPairs=" + wordsPairs +
                            '}';
                }
            }
        }

    }


    /**
     * 翻译模块配置
     */
    public static class TranslatorConfig implements Serializable{

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

        @Override
        public String toString() {
            return "TranslatorConfig{" +
                    "currentTranslator=" + currentTranslator +
                    ", sourceLanguage=" + sourceLanguage +
                    ", destLanguage=" + destLanguage +
                    ", baiduTranslatorApiKey='" + baiduTranslatorApiKey + '\'' +
                    ", baiduTranslatorSecretKey='" + baiduTranslatorSecretKey + '\'' +
                    ", youDaoTranslatorApiKey='" + youDaoTranslatorApiKey + '\'' +
                    ", youdaoTranslatorSecretKey='" + youDaoTranslatorSecretKey + '\'' +
                    '}';
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
    }

    public static class HistoryConfig implements Serializable{
        // 翻译历史记录条数
        @SerializedName("history_num")
        private Integer historyNum;

        public Integer getHistoryNum() {
            return historyNum;
        }

        public void setHistoryNum(Integer historyNum) {
            this.historyNum = historyNum;
        }

        @Override
        public String toString() {
            return "HistoryConfig{" +
                    "historyNum=" + historyNum +
                    '}';
        }
    }
    
    public static class UIConfig implements Serializable{
        @SerializedName("keep_top")
        private Boolean keepTop;
        @SerializedName("custom_css_path")
        private String styleCssPath;

        public String getStyleCssPath() {
            return styleCssPath;
        }

        public void setStyleCssPath(String styleCssPath) {
            this.styleCssPath = styleCssPath;
        }

        public Boolean isKeepTop() {
            return keepTop;
        }

        public void setKeepTop(Boolean keepTop) {
            this.keepTop = keepTop;
        }

        @Override
        public String toString() {
            return "UIConfig{" +
                    "keepTop=" + keepTop +
                    '}';
        }
    }

    public static class AfterProcessorConfig implements Serializable{
        @SerializedName("auto_copy")
        private Boolean autoCopy;
        @SerializedName("auto_paste")
        private Boolean autoPaste;

        public Boolean isAutoCopy() {
            return autoCopy;
        }

        public void setAutoCopy(Boolean autoCopy) {
            this.autoCopy = autoCopy;
        }

        public Boolean isAutoPaste() {
            return autoPaste;
        }

        public void setAutoPaste(Boolean autoPaste) {
            this.autoPaste = autoPaste;
        }

        @Override
        public String toString() {
            return "AfterProcessorConfig{" +
                    "autoCopy=" + autoCopy +
                    ", autoPaste=" + autoPaste +
                    '}';
        }
    }

}
