package com.rubbertranslator.mvp.modules.hotkey;

import com.rubbertranslator.event.HotKeyEvent;
import org.greenrobot.eventbus.EventBus;
import org.simplenativehooks.NativeHookInitializer;
import org.simplenativehooks.NativeKeyHook;
import org.simplenativehooks.events.NativeKeyEvent;
import org.simplenativehooks.utilities.Function;

/**
 * 热键管理
 */
public class HotKeyDispatcher {

    private static HotKeyDispatcher dispatcher;
    private HotKeyEvent hotKeyEvent = new HotKeyEvent();

    private HotKeyDispatcher(){

    }

    public static void initHotKeyDispatcher(){
       if(dispatcher == null) {
           synchronized (HotKeyDispatcher.class){
               if(dispatcher == null){
                   dispatcher = new HotKeyDispatcher();
                   dispatcher.register();
               }
           }
       }
    }

    private void register(){
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
        // TODO: When to stop? because there is a mouse listener in another class as well
    }
}
