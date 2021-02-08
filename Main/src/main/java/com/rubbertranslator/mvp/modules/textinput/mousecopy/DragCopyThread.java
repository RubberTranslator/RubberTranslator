package com.rubbertranslator.mvp.modules.textinput.mousecopy;

import com.rubbertranslator.mvp.modules.textinput.mousecopy.listener.GlobalMouseListener;
import org.simplenativehooks.NativeHookInitializer;
import org.simplenativehooks.NativeMouseHook;
import org.simplenativehooks.events.NativeMouseEvent;
import staticResources.BootStrapResources;
import utilities.Function;

import java.io.IOException;
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

        /* Extracting resources */
        try {
            BootStrapResources.extractResources();
        } catch (IOException e) {
            System.out.println("Cannot extract bootstrap resources.");
            e.printStackTrace();
        }

        /* Initializing global hooks */
        NativeHookInitializer.of().start();
        NativeMouseHook mouse = NativeMouseHook.of();
        mouse.setMousePressed(new Function<NativeMouseEvent, Boolean>() {
            @Override
            public Boolean apply(NativeMouseEvent nativeMouseEvent) {
                mouseListener.nativeMousePressed(nativeMouseEvent);
                return true;
            }
        });
        mouse.setMouseReleased(new Function<NativeMouseEvent, Boolean>() {
            @Override
            public Boolean apply(NativeMouseEvent nativeMouseEvent) {
                mouseListener.nativeMouseReleased(nativeMouseEvent);
                return true;
            }
        });
        mouse.startListening();
        Logger.getLogger(this.getClass().getName()).info("jnativehook 注册成功");
        Logger.getLogger(NativeHookInitializer.class.getPackage().getName()).setLevel(Level.OFF);
    }

    public void exit() {
        /* Clean up */
        NativeHookInitializer.of().stop();
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
