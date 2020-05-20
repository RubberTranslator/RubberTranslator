package com.rubbertranslator.event;

/**
 * @author Raven
 * @version 1.0
 * date  2020/5/20 22:07
 * 翻译完成事件
 */
public class TranslateCompleteEvent {
    private String origin;
    private String translation;

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getOrigin() {
        return origin;
    }

    public String getTranslation() {
        return translation;
    }
}
