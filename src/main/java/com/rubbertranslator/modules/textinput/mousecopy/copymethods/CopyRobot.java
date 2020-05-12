package com.rubbertranslator.modules.textinput.mousecopy.copymethods;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * 复制器
 */
public class CopyRobot {
    private Robot robot;
    private static CopyRobot copyerRobot;

    private CopyRobot(){
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static CopyRobot getInstance(){
        // double check singleton
        if(copyerRobot == null){
            synchronized (CopyRobot.class){
                if(copyerRobot == null){
                    copyerRobot = new CopyRobot();
                }
            }
        }
        return copyerRobot;
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
