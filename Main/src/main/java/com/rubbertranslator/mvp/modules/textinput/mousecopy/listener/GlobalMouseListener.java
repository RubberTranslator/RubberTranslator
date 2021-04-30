package com.rubbertranslator.mvp.modules.textinput.mousecopy.listener;

import com.rubbertranslator.mvp.modules.textinput.mousecopy.copymethods.AbstractMouseEventDispatcher;
import com.rubbertranslator.mvp.modules.textinput.mousecopy.copymethods.LinuxMouseEventDispatcher;
import com.rubbertranslator.mvp.modules.textinput.mousecopy.copymethods.MacMouseEventDispatcher;
import com.rubbertranslator.mvp.modules.textinput.mousecopy.copymethods.WinMouseEventDispatcher;
import com.rubbertranslator.utils.OSTypeUtil;

/**
 * @author Raven
 * @version 1.0
 * date 2020/4/27 15:43
 * 全局mouse event监听
 * 鼠标点击事件分发，采用责任链分发
 */
public class GlobalMouseListener {

    private AbstractMouseEventDispatcher mouseEventDispatcher;

    public GlobalMouseListener() {
    }

    public void init(){
        if (OSTypeUtil.isMac()) {
            mouseEventDispatcher = new MacMouseEventDispatcher();
        } else if (OSTypeUtil.isLinux()) {
            mouseEventDispatcher = new LinuxMouseEventDispatcher();
        } else if(OSTypeUtil.isWin()){
            mouseEventDispatcher = new WinMouseEventDispatcher();
        }
    }

    public void stopDispatch(){
        if(mouseEventDispatcher != null){
            mouseEventDispatcher.releaseResources();
        }
    }

    public boolean isNeedDispatch() {
        return mouseEventDispatcher != null && mouseEventDispatcher.isNeedDispatch();
    }

    public void setNeedDispatch(boolean needDispatch) {
        if(mouseEventDispatcher != null){
            mouseEventDispatcher.setNeedDispatch(needDispatch);
        }
    }

}
