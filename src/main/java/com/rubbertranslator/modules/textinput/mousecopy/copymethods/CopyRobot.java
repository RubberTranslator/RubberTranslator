package com.rubbertranslator.modules.textinput.mousecopy.copymethods;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 复制器
 */
public class CopyRobot {
    private static CopyRobot copyRobot;
    private Robot robot;
    private Clipboard clipboard;

    private CopyRobot(){
        try {
            robot = new Robot();
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        } catch (AWTException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getLocalizedMessage(), e);
        }
    }

    public static CopyRobot getInstance(){
        // double check singleton
        if(copyRobot == null){
            synchronized (CopyRobot.class){
                if(copyRobot == null){
                    copyRobot = new CopyRobot();
                }
            }
        }
        return copyRobot;
    }


    public synchronized void copyText(String text){
        if(text == null || "".equals(text)) return;
        boolean required = false;
        while(!required){
            try {
                //waiting e.g for loading huge elements like word's etc.
                Thread.sleep(100);
                clipboard.setContents(new StringSelection(text),null);
                required = true;
            } catch (InterruptedException | IllegalStateException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING,e.getLocalizedMessage(),e);
            }
        }
    }


    /**
     * 触发copy action
     */
    public synchronized void triggerCopy(){
        // do copy action
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_C);
        robot.delay(robot.getAutoDelay());
        robot.keyRelease(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    /**
     * 触发粘贴
     */
    public synchronized void triggerPaste(){
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.delay(robot.getAutoDelay());
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }
}
