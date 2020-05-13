package com.rubbertranslator.modules.textinput.mousecopy.copymethods;


import com.rubbertranslator.modules.textinput.mousecopy.initiator.SystemTrayInitiator;
import org.jnativehook.mouse.NativeMouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Raven
 * @version 1.0
 * date 2020/4/28 11:51
 * 鼠标事件分发器
 */
public class MouseEventDispatcher {
    private List<CopyMethod> copyMethods = new ArrayList<>();

    public MouseEventDispatcher() {
        // 初始化处理链
        initializeCopyMethodsChain();
    }

    /**
     * 初始化copy method chain
     */
    private void initializeCopyMethodsChain() {
        // first add double click method
        copyMethods.add(new DoubleClickCopyMethod());
        // add mouse move method
        copyMethods.add(new MouseMoveCopyMethod());
    }


    public void pressEventDispatch(NativeMouseEvent event) {
        for (CopyMethod copyMethod : copyMethods) {
            copyMethod.onPressed(event);
        }
    }

    public void releaseEventDispatch(NativeMouseEvent event) {
        for (CopyMethod copyMethod : copyMethods) {
            copyMethod.onRelease(event);
            if (copyMethod.isProcessed()) {
                // 处理链终止
                return;
            }
        }
    }

}
