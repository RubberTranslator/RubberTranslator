package com.rubbertranslator.event;

/**
 * @author Raven
 * @version 1.0
 * date  2020/5/20 21:29
 *
 */
public class TranslatorFacadeEvent {
    // 处理开始与否
    private boolean processStart;

    public void start(){
        setProcessStart(true);
    }

    public void end(){
        setProcessStart(false);
    }

    private void setProcessStart(boolean processStart){
        this.processStart = processStart;
    }

    public boolean isProcessStart() {
        return processStart;
    }

}
