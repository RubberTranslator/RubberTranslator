package com.rubbertranslator.mvp.modules.afterprocess;

import com.rubbertranslator.mvp.modules.textinput.mousecopy.copymethods.CopyRobot;

import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/12 10:57
 */
public class AfterProcessor {

    // 自动复制
    private volatile boolean autoCopy = false;
    // hack:自动粘贴(依赖于自动复制）
    private volatile boolean autoPaste = false;

    @Override
    public String toString() {
        return "AfterProcessor{" +
                "autoCopy=" + autoCopy +
                ", autoPaste=" + autoPaste +
                '}';
    }

    public boolean isAutoCopy() {
        return autoCopy;
    }

    public void setAutoCopy(boolean autoCopy) {
        this.autoCopy = autoCopy;
    }

    public boolean isAutoPaste() {
        return autoPaste;
    }

    public void setAutoPaste(boolean autoPaste) {
        this.autoPaste = autoPaste;
    }

    public String process(String text){
        if(autoCopy){
            Logger.getLogger(this.getClass().getName()).info("set clipboard:"+ text);
            CopyRobot.getInstance().copyText(text);
            if(autoPaste){  // 粘贴依赖自动复制
                CopyRobot.getInstance().triggerPaste();
            }
        }
        return text;
    }
}
