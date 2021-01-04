package com.rubbertranslator.mvp.modules.textinput.mousecopy.copymethods;

import org.jnativehook.mouse.NativeMouseEvent;

/**
 * @author Raven
 * @version 1.0
 * date 2020/4/27 17:31
 */
public abstract class CopyMethod {
    protected CopyRobot copyerRobot = CopyRobot.getInstance();
    // indicate whether the copy action has been processed
    protected boolean isProcessed = false;

    public boolean isProcessed() {
        return isProcessed;
    }

    /**
     * 鼠标按下事件
     * @param event
     */
    public abstract void onPressed(NativeMouseEvent event);

    /**
     * 鼠标释放事件
     * @param event
     */
    public abstract void onRelease(NativeMouseEvent event);


}
