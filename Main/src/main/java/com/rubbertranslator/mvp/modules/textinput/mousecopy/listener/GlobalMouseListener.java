package com.rubbertranslator.mvp.modules.textinput.mousecopy.listener;

import com.rubbertranslator.mvp.modules.textinput.mousecopy.copymethods.MouseEventDispatcher;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

/**
 * @author Raven
 * @version 1.0
 * date 2020/4/27 15:43
 * 全局mouse event监听
 * 鼠标复制事件分发，采用责任链分发
 */
public class GlobalMouseListener implements NativeMouseInputListener {

    private final MouseEventDispatcher mouseEventDispatcher = new MouseEventDispatcher();
    private boolean needDispatch = true;

    public boolean isNeedDispatch() {
        return needDispatch;
    }

    public void setNeedDispatch(boolean needDispatch) {
        this.needDispatch = needDispatch;
    }


    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
        if (needDispatch) {
            mouseEventDispatcher.pressEventDispatch(nativeMouseEvent);
        }
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
        if (needDispatch) {
            mouseEventDispatcher.releaseEventDispatch(nativeMouseEvent);
        }
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {

    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {

    }
}
