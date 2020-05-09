package com.rubbertranslator.modules.history;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/9 10:44
 */
public class HistoryEntry {
    private String origin;
    private String translation;

    public HistoryEntry(String origin, String translation) {
        this.origin = origin;
        this.translation = translation;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return "HistoryEntry{" +
                "origin='" + origin + '\'' +
                ", translation='" + translation + '\'' +
                '}';
    }
}
