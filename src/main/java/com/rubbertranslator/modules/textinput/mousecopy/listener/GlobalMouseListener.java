package com.rubbertranslator.modules.textinput.mousecopy.listener;

import com.rubbertranslator.event.MouseClickPositionEvent;
import com.rubbertranslator.modules.textinput.mousecopy.copymethods.MouseCopyEventDispatcher;
import org.greenrobot.eventbus.EventBus;
import org.jnativehook.mouse.NativeMouseAdapter;
import org.jnativehook.mouse.NativeMouseEvent;

/**
 * @author Raven
 * @version 1.0
 * date 2020/4/27 15:43
 * 全局mouse event监听
 * 鼠标复制事件分发，采用责任链分发
 */
public class GlobalMouseListener extends NativeMouseAdapter {

    private final MouseCopyEventDispatcher mouseCopyEventDispatcher = new MouseCopyEventDispatcher();
    private boolean needDispatch = true;

    public boolean isNeedDispatch() {
        return needDispatch;
    }

    public void setNeedDispatch(boolean needDispatch) {
        this.needDispatch = needDispatch;
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeEvent) {
        if(needDispatch){
            mouseCopyEventDispatcher.pressEventDispatch(nativeEvent);
        }
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
        if(needDispatch){
            mouseCopyEventDispatcher.releaseEventDispatch(nativeEvent);
        }
    }
}
