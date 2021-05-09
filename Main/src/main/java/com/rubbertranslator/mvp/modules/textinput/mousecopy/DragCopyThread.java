package com.rubbertranslator.mvp.modules.textinput.mousecopy;

import com.rubbertranslator.mvp.modules.textinput.mousecopy.listener.GlobalMouseListener;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/7 15:01
 * DragCopyThread 已经不再是线程!!
 */
public class DragCopyThread {
    // Construct the example object.
    private final GlobalMouseListener mouseListener =  new GlobalMouseListener();

    public DragCopyThread() {
    }

    public void start() {
        mouseListener.init();
    }

    public void exit() {
        /* Clean up */
        mouseListener.stopDispatch();
    }

    public void setRun(boolean run) {
        if (run) {
            resumeRun();
        } else {
            pause();
        }
    }

    private void pause() {
        mouseListener.setNeedDispatch(false);
    }

    private void resumeRun() {
        mouseListener.setNeedDispatch(true);
    }


}
