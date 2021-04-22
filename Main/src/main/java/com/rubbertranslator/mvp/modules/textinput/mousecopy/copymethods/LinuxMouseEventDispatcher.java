package com.rubbertranslator.mvp.modules.textinput.mousecopy.copymethods;

import com.rubbertranslator.entity.MouseEvent;
import org.simplenativehooks.NativeHookInitializer;
import org.simplenativehooks.NativeMouseHook;
import org.simplenativehooks.events.NativeMouseEvent;
import org.simplenativehooks.staticResources.BootStrapResources;
import org.simplenativehooks.utilities.Function;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LinuxMouseEventDispatcher extends AbstractMouseEventDispatcher {

    @Override
    protected void initHookLibResources() {
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
                MouseEvent event = new MouseEvent();
                event.setClickPoint(new Point(nativeMouseEvent.getX(),nativeMouseEvent.getY()));
                LinuxMouseEventDispatcher.this.pressEventDispatch(event);
                return true;
            }
        });
        mouse.setMouseReleased(new Function<NativeMouseEvent, Boolean>() {
            @Override
            public Boolean apply(NativeMouseEvent nativeMouseEvent) {
                MouseEvent event = new MouseEvent();
                event.setClickPoint(new Point(nativeMouseEvent.getX(),nativeMouseEvent.getY()));
                LinuxMouseEventDispatcher.this.releaseEventDispatch(event);
                return true;
            }
        });
        mouse.startListening();
        Logger.getLogger(this.getClass().getName()).info("nativehook 注册成功");
        Logger.getLogger(NativeHookInitializer.class.getPackage().getName()).setLevel(Level.OFF);
    }

    @Override
    public void releaseResources() {
        NativeHookInitializer.of().stop();
    }
}
