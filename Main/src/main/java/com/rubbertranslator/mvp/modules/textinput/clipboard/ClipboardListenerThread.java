package com.rubbertranslator.mvp.modules.textinput.clipboard;


import com.rubbertranslator.event.ClipboardContentInputEvent;
import com.rubbertranslator.mvp.modules.filter.ProcessFilter;
import com.rubbertranslator.utils.OSTypeUtil;
import org.greenrobot.eventbus.EventBus;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class ClipboardListenerThread extends Thread  {
    // 系统剪切板
    protected final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    // 消息通知事件
    protected final ClipboardContentInputEvent textInputEvent = new ClipboardContentInputEvent();
    // 过滤器
    protected ProcessFilter processFilter;

    public ClipboardListenerThread() {
        setName("Clipboard Thread");
    }

    @Override
    public void run() {
        startProcess();
    }

    protected abstract void startProcess();

    public void setProcessFilter(ProcessFilter processFilter) {
        this.processFilter = processFilter;
    }

    public ProcessFilter getProcessFilter() {
        return processFilter;
    }

    public void setRun(boolean run) {
        if (run) {
            resumeProcess();
        } else {
            pauseProcess();
        }
    }


    protected abstract void resumeProcess();

    protected abstract void pauseProcess() ;

    public abstract void ignoreThisTime();

    public abstract void exit();
}