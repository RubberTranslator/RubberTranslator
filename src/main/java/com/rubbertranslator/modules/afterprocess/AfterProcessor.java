package com.rubbertranslator.modules.afterprocess;

import com.rubbertranslator.modules.textinput.mousecopy.copymethods.CopyRobot;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/12 10:57
 */
public class AfterProcessor {
    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    // 自动复制
    private volatile boolean autoCopy = false;
    // hack:自动粘贴(依赖于自动复制）
    private volatile boolean autoPaste = false;

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
            Logger.getLogger(this.getClass().getName()).info("autoCopy: "+autoCopy);
            Logger.getLogger(this.getClass().getName()).info("set clipboard:"+text);
            boolean required = false;
            while(!required){
                try {
                    //waiting e.g for loading huge elements like word's etc.
                    Thread.sleep(100);
                    clipboard.setContents(new StringSelection(text),null);
                    required = true;
                } catch (InterruptedException | IllegalStateException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING,e.getMessage(),e);
                }
            }
            if(autoPaste){  // 粘贴依赖自动复制
                CopyRobot.getInstance().triggerPaste();
                Logger.getLogger(this.getClass().getName()).info("paste");
            }
        }
        return text;
    }
}
