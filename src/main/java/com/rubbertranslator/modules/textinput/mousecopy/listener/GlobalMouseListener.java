package com.rubbertranslator.modules.textinput.mousecopy.listener;

import com.rubbertranslator.modules.textinput.mousecopy.copymethods.MouseEventDispatcher;
import org.jnativehook.mouse.NativeMouseAdapter;
import org.jnativehook.mouse.NativeMouseEvent;

/**
 * @author Raven
 * @version 1.0
 * date 2020/4/27 15:43
 */
public class GlobalMouseListener extends NativeMouseAdapter {

    private final MouseEventDispatcher mouseEventDispatcher = new MouseEventDispatcher();
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
            mouseEventDispatcher.pressEventDispatch(nativeEvent);
        }
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
        if(needDispatch){
            mouseEventDispatcher.releaseEventDispatch(nativeEvent);
        }
    }
}
