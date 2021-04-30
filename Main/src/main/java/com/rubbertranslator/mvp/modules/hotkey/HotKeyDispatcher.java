package com.rubbertranslator.mvp.modules.hotkey;

import com.rubbertranslator.event.HotKeyEvent;
import org.greenrobot.eventbus.EventBus;
import org.simplenativehooks.NativeHookInitializer;
import org.simplenativehooks.NativeKeyHook;
import org.simplenativehooks.events.NativeKeyEvent;
import org.simplenativehooks.staticResources.BootStrapResources;
import org.simplenativehooks.utilities.Function;

import java.io.IOException;

/**
 * 热键管理
 */
public class HotKeyDispatcher {

    private static HotKeyDispatcher dispatcher;

    private HotKeyEvent hotKeyEvent = new HotKeyEvent();

    private HotKeyDispatcher() {

    }

    public static void initHotKeyDispatcher() {
        if (dispatcher == null) {
            synchronized (HotKeyDispatcher.class) {
                if (dispatcher == null) {
                    dispatcher = new HotKeyDispatcher();
                    dispatcher.register();
                }
            }
        }
    }

    public static void destroy(){
        NativeHookInitializer.of().stop();
    }

    // TODO 这里采用了simplenatike
    // 本想换为jnative，但是jnative和simplenative的
    // KeyCode 编码不一样，需要额外实现一个转换层
    // 目前只采用simplenatike, 看是否有平台会无法使用
    private void register() {
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
                System.out.println(d.getKey());
                hotKeyEvent.hotKeyValue = d.getKey();
                EventBus.getDefault().post(hotKeyEvent);
                return true;
            }
        });
        key.startListening();
        // TODO: When to stop? because there is a mouse listener in another class as well
    }


}
