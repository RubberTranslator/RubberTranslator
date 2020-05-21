package com.rubbertranslator.event;

/**
 * @author Raven
 * @version 1.0
 * date  2020/5/20 21:29
 * 翻译过程事件，表明整个翻译开始和结束
 */
public class TranslatorProcessEvent {
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
