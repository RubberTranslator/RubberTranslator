package com.rubbertranslator.mvp.modules.textinput.mousecopy;

import com.rubbertranslator.mvp.modules.textinput.mousecopy.listener.GlobalMouseListener;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/7 15:01
 */
public class DragCopyThread extends Thread {
    // Construct the example object.
    private final GlobalMouseListener mouseListener = new GlobalMouseListener();

    public DragCopyThread() {
        setName("DragCopy Thread");
    }

    @Override
    public void run() {
        // 初始化后就退出，无需做保活
        /* turn off the console output */
        // Get the logger for "org.jnativehook" and set the level to off.
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "There was a problem registering the native hook.", ex);
        }
        // Add the appropriate listeners.
        GlobalScreen.addNativeMouseListener(mouseListener);
        Logger.getLogger(this.getClass().getName()).info("jnativehook 注册成功");

        // 关闭log
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        // Don't forget to disable the parent handlers.
        logger.setUseParentHandlers(false);
    }

    public void exit() {
        try {
            GlobalScreen.unregisterNativeHook();
            Logger.getLogger(this.getClass().getName()).info("DragCopy exit");
        } catch (NativeHookException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "There was a problem unregistering the native hook.");
        }
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
