package com.rubbertranslator.mvp.modules.textinput.mousecopy.listener;

import com.rubbertranslator.mvp.modules.textinput.mousecopy.copymethods.MouseEventDispatcher;
import org.simplenativehooks.events.NativeMouseEvent;

/**
 * @author Raven
 * @version 1.0
 * date 2020/4/27 15:43
 * 全局mouse event监听
 * 鼠标复制事件分发，采用责任链分发
 */
public class GlobalMouseListener {

    private final MouseEventDispatcher mouseEventDispatcher = new MouseEventDispatcher();
    private boolean needDispatch = true;

    public boolean isNeedDispatch() {
        return needDispatch;
    }

    public void setNeedDispatch(boolean needDispatch) {
        this.needDispatch = needDispatch;
    }


    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
        if (needDispatch) {
            mouseEventDispatcher.pressEventDispatch(nativeMouseEvent);
        }
    }

    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
        if (needDispatch) {
            mouseEventDispatcher.releaseEventDispatch(nativeMouseEvent);
        }
    }
}
