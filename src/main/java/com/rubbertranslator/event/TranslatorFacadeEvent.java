package com.rubbertranslator.event;

/**
 * @author Raven
 * @version 1.0
 * date  2020/5/20 21:29
 *
 */
public class TranslatorFacadeEvent {
    // 处理开始与否
    private final boolean processStart;

    public TranslatorFacadeEvent(boolean start) {
        this.processStart = start;
    }

    public boolean isProcessStart() {
        return processStart;
    }

}
