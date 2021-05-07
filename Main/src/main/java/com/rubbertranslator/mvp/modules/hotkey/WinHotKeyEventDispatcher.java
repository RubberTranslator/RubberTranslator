package com.rubbertranslator.mvp.modules.hotkey;

import com.rubbertranslator.event.HotKeyEvent;
import org.greenrobot.eventbus.EventBus;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class WinHotKeyEventDispatcher extends AbstractHotKeyEventDispatcher implements NativeKeyListener {

    private HotKeyEvent hotKeyEvent = new HotKeyEvent();

    @Override
    public void startListen() {
        new Thread(
                () -> {
                    synchronized (GlobalScreen.class){
                        if (!GlobalScreen.isNativeHookRegistered()) {
                            try {
                                GlobalScreen.registerNativeHook();
                            } catch (NativeHookException e) {
                                e.printStackTrace();
                            }
                        }
                        GlobalScreen.addNativeKeyListener(this);
                    }
                }
        ).start();
    }

    @Override
    public synchronized void stopListen() {
        if(GlobalScreen.isNativeHookRegistered()){
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        hotKeyEvent.hotKeyValue = nativeKeyEvent.getRawCode();
        EventBus.getDefault().post(hotKeyEvent);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }
}
