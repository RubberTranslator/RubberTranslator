package com.rubbertranslator.mvp.modules.hotkey;

import com.rubbertranslator.utils.OSTypeUtil;

import java.util.logging.Logger;

/**
 * 热键管理
 */
public class GlobalHotKeyListener {

    private static volatile boolean isInitialized = false;

    private static AbstractHotKeyEventDispatcher hotKeyEventDispatcher;


    private GlobalHotKeyListener() {

    }

    public synchronized static void initHotKeyDispatcher() {
        if(!isInitialized){
           init();
           isInitialized = true;
        }
    }

    public static void destroy(){
        hotKeyEventDispatcher.stopListen();
    }

    // TODO 这里采用了simplenatike
    // 本想换为jnative，但是jnative和simplenative的
    // KeyCode 编码不一样，需要额外实现一个转换层
    // 目前只采用simplenatike, 看是否有平台会无法使用
    private static void init() {
        if(OSTypeUtil.isWin()){
            hotKeyEventDispatcher = new WinHotKeyEventDispatcher();
        }else if(OSTypeUtil.isMac()){
            hotKeyEventDispatcher = new MacHotKeyEventDispatcher();
        }else if(OSTypeUtil.isLinux()){
            hotKeyEventDispatcher = new LinuxHotKeyEventDispatcher();
        }else{
            Logger.getLogger(GlobalHotKeyListener.class.getName()).severe("Unsupported os type");
        }

        hotKeyEventDispatcher.startListen();
    }


}
