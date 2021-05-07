package com.rubbertranslator.mvp.modules.hotkey;

import com.rubbertranslator.event.HotKeyEvent;
import org.greenrobot.eventbus.EventBus;
import org.simplenativehooks.NativeHookInitializer;
import org.simplenativehooks.NativeKeyHook;
import org.simplenativehooks.events.NativeKeyEvent;
import org.simplenativehooks.staticResources.BootStrapResources;
import org.simplenativehooks.utilities.Function;

import java.io.IOException;

public class LinuxHotKeyEventDispatcher extends AbstractHotKeyEventDispatcher{

    private final HotKeyEvent hotKeyEvent = new HotKeyEvent();

    @Override
    public void startListen() {
        new Thread(
                ()->{
                    synchronized (NativeHookInitializer.class){
                        /* Extracting resources */
                        try {
                            BootStrapResources.extractResources();
                        } catch (IOException e) {
                            System.out.println("Cannot extract bootstrap resources.");
                            e.printStackTrace();
                        }

                        /* Initializing global hooks */
                        NativeHookInitializer.of().start();

                        /* Set up callbacks */
                        NativeKeyHook key = NativeKeyHook.of();
                        key.setKeyReleased(new Function<NativeKeyEvent, Boolean>() {
                            @Override
                            public Boolean apply(NativeKeyEvent d) {
                                hotKeyEvent.hotKeyValue = d.getKey();
                                EventBus.getDefault().post(hotKeyEvent);
                                return true;
                            }
                        });
                        key.startListening();
                    }
                }
        ).start();
    }

    @Override
    public void stopListen() {
        NativeKeyHook.of().stopListening();
    }
}
