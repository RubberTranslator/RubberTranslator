package com.rubbertranslator.mvp.modules.textinput.mousecopy.copymethods;

import com.rubbertranslator.entity.MouseEvent;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WinMouseEventDispatcher extends AbstractMouseEventDispatcher {

    @Override
    protected void initHookLibResources() {
        /* turn off the console output */
        // Get the logger for "org.jnativehook" and set the level to off.
        new Thread(() -> {
            try {
                GlobalScreen.registerNativeHook();
            } catch (NativeHookException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "There was a problem registering the native hook.", ex);
            }
            // Add the appropriate listeners.
            Logger.getLogger(this.getClass().getName()).info("mouse hook register");
            GlobalScreen.addNativeMouseListener(new NativeMouseListener() {
                @Override
                public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {

                }

                @Override
                public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
                    MouseEvent event = new MouseEvent();
                    event.setClickPoint(nativeMouseEvent.getPoint());
                    WinMouseEventDispatcher.this.pressEventDispatch(event);
                }

                @Override
                public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
                    MouseEvent event = new MouseEvent();
                    event.setClickPoint(nativeMouseEvent.getPoint());
                    WinMouseEventDispatcher.this.releaseEventDispatch(event);
                }
            });

            Logger.getLogger(this.getClass().getName()).info("jnativehook 注册成功");

            // 关闭log
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
//            Don 't forget to disable the parent handlers.
            logger.setUseParentHandlers(false);
        }).start();

    }


    @Override
    public void releaseResources() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
    }
}
