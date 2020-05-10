package com.rubbertranslator.modules.system;

import com.google.gson.annotations.SerializedName;
import com.rubbertranslator.modules.translate.Language;
import com.rubbertranslator.modules.translate.TranslatorType;

import java.util.List;
import java.util.Map;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/10 21:02
 * 系统配置类
 */
public class SystemConfiguration {
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

    @Override
    public String toString() {
        return "SystemConfiguration{" +
                "textInputConfig=" + textInputConfig +
                ", processFilterConfig=" + processFilterConfig +
                ", textProcessConfig=" + textProcessConfig +
                ", translatorConfig=" + translatorConfig +
                '}';
    }
    public ProcessFilterConfig getProcessFilterConfig() {
        return processFilterConfig;
    }

    public void setProcessFilterConfig(ProcessFilterConfig processFilterConfig) {
        this.processFilterConfig = processFilterConfig;
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
    public static class TextInputConfig {
        // 是否开启监听剪切板
        @SerializedName("open_clipboard_listener")
        private boolean openClipboardListener;
        // 是否开启拖拽复制
        @SerializedName("drag_copy")
        private boolean dragCopy;
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

        public boolean isOpenClipboardListener() {
            return openClipboardListener;
        }

        public void setOpenClipboardListener(boolean openClipboardListener) {
            this.openClipboardListener = openClipboardListener;
        }

        public boolean isDragCopy() {
            return dragCopy;
        }

        public void setDragCopy(boolean dragCopy) {
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

    public static class ProcessFilterConfig {
        // 是否打开进程过滤器
        @SerializedName("open_process_filter")
        private boolean openProcessFilter;
        // TODO: 考虑加入黑白名单模式
        // 过滤进程集合 TODO:如果有黑白名单，则用两个进程list来表示，如whiteProcessList
        @SerializedName("process_list")
        private List<String> processList;

        public boolean isOpenProcessFilter() {
            return openProcessFilter;
        }

        public void setOpenProcessFilter(boolean openProcessFilter) {
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
    public static class TextProcessConfig{
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
        public static class TextPreProcessConfig{
            @SerializedName("keep_paragraph_format")
            private boolean tryKeepParagraphFormat;

            public boolean isTryKeepParagraphFormat() {
                return tryKeepParagraphFormat;
            }

            public void setTryKeepParagraphFormat(boolean tryKeepParagraphFormat) {
                this.tryKeepParagraphFormat = tryKeepParagraphFormat;
            }

            @Override
            public String toString() {
                return "TextPreProcessConfig{" +
                        "tryKeepParagraphFormat=" + tryKeepParagraphFormat +
                        '}';
            }
        }

        /**
         * 后置处理
         */
        public static class TextPostProcessConfig{
            @SerializedName("words_replacer")
            private WordsReplacerConfig wordsReplacerConfig;

            @Override
            public String toString() {
                return "TextPostProcessConfig{" +
                        "wordsReplacerConfig=" + wordsReplacerConfig +
                        '}';
            }

            public WordsReplacerConfig getWordsReplacerConfig() {
                return wordsReplacerConfig;
            }

            public void setWordsReplacerConfig(WordsReplacerConfig wordsReplacerConfig) {
                this.wordsReplacerConfig = wordsReplacerConfig;
            }

            public static class WordsReplacerConfig{
                // 是否打开词组替换
                @SerializedName("open_words_replacer")
                private boolean openWordsReplacer;
                // 词组替换集合
                @SerializedName("words_map")
                private Map<String, String> wordsMap;

                public boolean isOpenWordsReplacer() {
                    return openWordsReplacer;
                }

                public void setOpenWordsReplacer(boolean openWordsReplacer) {
                    this.openWordsReplacer = openWordsReplacer;
                }

                public Map<String, String> getWordsMap() {
                    return wordsMap;
                }

                public void setWordsMap(Map<String, String> wordsMap) {
                    this.wordsMap = wordsMap;
                }
            }
        }

    }


    /**
     * 翻译模块配置
     */
    public static class TranslatorConfig{

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
        @SerializedName("you_dao_transltor_api_key")
        private String youdaoTranslatorSecretKey;

        @Override
        public String toString() {
            return "TranslatorConfig{" +
                    "currentTranslator=" + currentTranslator +
                    ", sourceLanguage=" + sourceLanguage +
                    ", destLanguage=" + destLanguage +
                    ", baiduTranslatorApiKey='" + baiduTranslatorApiKey + '\'' +
                    ", baiduTranslatorSecretKey='" + baiduTranslatorSecretKey + '\'' +
                    ", youDaoTranslatorApiKey='" + youDaoTranslatorApiKey + '\'' +
                    ", youdaoTranslatorSecretKey='" + youdaoTranslatorSecretKey + '\'' +
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

        public String getYoudaoTranslatorSecretKey() {
            return youdaoTranslatorSecretKey;
        }

        public void setYoudaoTranslatorSecretKey(String youdaoTranslatorSecretKey) {
            this.youdaoTranslatorSecretKey = youdaoTranslatorSecretKey;
        }
    }

}
