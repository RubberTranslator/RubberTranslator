package com.rubbertranslator.mvp.modules.hotkey;

public class MacHotKeyEventDispatcher extends AbstractHotKeyEventDispatcher{

    private final LinuxHotKeyEventDispatcher hotKeyListener = new LinuxHotKeyEventDispatcher();

    @Override
    public void startListen() {
        hotKeyListener.startListen();
    }

    @Override
    public void stopListen() {
        hotKeyListener.stopListen();
    }
}
