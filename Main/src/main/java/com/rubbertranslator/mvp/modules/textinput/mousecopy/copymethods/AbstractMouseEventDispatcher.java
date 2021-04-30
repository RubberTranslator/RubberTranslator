package com.rubbertranslator.mvp.modules.textinput.mousecopy.copymethods;

import com.rubbertranslator.entity.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class AbstractMouseEventDispatcher {

    protected List<CopyMethod> copyMethods = new ArrayList<>();

    private boolean needDispatch = true;

    public boolean isNeedDispatch() {
        return needDispatch;
    }

    public void setNeedDispatch(boolean needDispatch) {
        this.needDispatch = needDispatch;
    }


    public AbstractMouseEventDispatcher() {
        initHookLibResources();
        registerCopyMethods();
    }

    protected abstract void initHookLibResources();

    public abstract void releaseResources();

    /**
     * 初始化copy method chain
     */
    protected void registerCopyMethods() {
        // first add double click method
        copyMethods.add(new DoubleClickCopyMethod());
        // add mouse move method
        copyMethods.add(new MouseMoveCopyMethod());
    }


    public void pressEventDispatch(MouseEvent event) {
        if (needDispatch) {
            pressEventPreDispatch(event);
            for (CopyMethod copyMethod : copyMethods) {
                copyMethod.onPressed(event);
            }
            pressEventPostDispatch(event);
        }
    }

    protected void pressEventPreDispatch(MouseEvent event) {

    }

    protected void pressEventPostDispatch(MouseEvent event) {

    }

    public void releaseEventDispatch(MouseEvent event) {
        if (needDispatch) {
            releaseEventPreDispatch(event);
            for (CopyMethod copyMethod : copyMethods) {
                copyMethod.onRelease(event);
                if (copyMethod.isProcessed()) {
                    Logger.getLogger(this.getClass().getName()).info("handle by " + copyMethod.getClass().getSimpleName());
                    // 处理链终止
                    break;
                }
            }
            releaseEventPostDispatch(event);
        }
    }

    protected void releaseEventPreDispatch(MouseEvent event) {

    }

    protected void releaseEventPostDispatch(MouseEvent event) {
    }


}
