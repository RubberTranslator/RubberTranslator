package com.rubbertranslator.modules.translate;

/**
 * 由于每种翻译引擎采用的语言映名称不同，所以每个语言单独维护语言表
 * 然后翻译模块向外提供一个公共的语言类型枚举即可
 */
public enum Language {
    AUTO,    // 自动
    CHINESE_SIMPLIFIED,    // 简体中文
    CHINESE_TRADITIONAL,    // 繁体中文
    ENGLISH, // 英文
    FRENCH,     // 法语
    JAPANESE // 日文
}
