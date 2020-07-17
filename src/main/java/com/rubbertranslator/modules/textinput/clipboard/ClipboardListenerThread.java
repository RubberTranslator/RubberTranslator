package com.rubbertranslator.modules.textinput.clipboard;


import com.rubbertranslator.event.ClipboardContentInputEvent;
import com.rubbertranslator.event.CopyOriginOrTranslationEvent;
import com.rubbertranslator.modules.filter.ProcessFilter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClipboardListenerThread extends Thread implements ClipboardOwner {
    // 系统剪切板
    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    // blocker用于线程保活
    private final Blocker blocker = new Blocker();
    // 程序暂停
    private volatile boolean running = true;
    // 动态等待时间
    private long waitTime = 50;
    // 跳过本次变化监听
    private boolean ignoreThisTime = false;
    // 消息通知事件
    private final ClipboardContentInputEvent textInputEvent = new ClipboardContentInputEvent();
    // 过滤器
    private ProcessFilter processFilter;


    public ClipboardListenerThread() {
        setName("Clipboard Thread");
    }

    @Override
    public void run() {
        init();
        blocker.keepAlive();
        destroy();
        Logger.getLogger(this.getClass().getName()).info("ClipBoard exit");
    }

    private void init() {
        // 注册消息监听
        EventBus.getDefault().register(this);
        // 初始化剪切板监听
        Transferable trans = clipboard.getContents(this);
        clipboard.setContents(trans, this);
    }

    private void destroy() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void lostOwnership(Clipboard c, Transferable t) {
        Transferable contents;
        // 循环require clipboard owner
        boolean required = false;
        final long minWaitTime = 50;
        final long maxWaitTime = 3000;
        while (!required) {
            try {
                //waiting e.g for loading huge elements like word's etc.
                Thread.sleep(waitTime);
                contents = clipboard.getContents(null);
                clipboard.setContents(contents, this);
                required = true;
                // 忽略条件只能在成为剪切板owner后判定，否则无法继续监听剪切板
                if (!running) {
                    break;
                }
                if (ignoreThisTime) {
                    ignoreThisTime = false;
                    break;
                }
                processClipboard(contents);
            } catch (Exception e) {
                if (waitTime < maxWaitTime) {
                    waitTime += 100;  // 增加100ms等待时间
                }
                e.printStackTrace();
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getLocalizedMessage(), e);
            } finally {
                if (waitTime > minWaitTime) {
                    waitTime -= 10;   // 减少10ms等待时间
                }
            }
        }
    }

    private void processClipboard(Transferable t) throws IOException, UnsupportedFlavorException {
        if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String paste = (String) t.getTransferData(DataFlavor.stringFlavor);
            if (processFilter != null && !processFilter.check()) {
                textInputEvent.setText(paste);
                EventBus.getDefault().post(textInputEvent);
            }
        } else if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            Image paste = (Image) t.getTransferData(DataFlavor.imageFlavor);
            if (processFilter != null && !processFilter.check()) {
                textInputEvent.setImage(paste);
                EventBus.getDefault().post(textInputEvent);
            }
        }
    }

    public void setProcessFilter(ProcessFilter processFilter) {
        this.processFilter = processFilter;
    }

    public ProcessFilter getProcessFilter() {
        return processFilter;
    }


    public void setRun(boolean run) {
        running = run;
    }

    public void exit() {
        blocker.dead();
    }


    /**
     * 用户复制原文或译文时，为了避免重复翻译，监听线程需要忽略本次剪切板变化
     *
     * @param event 复制原文或译文事件
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void triggerIgnoreThisTime(CopyOriginOrTranslationEvent event) {
        if (event == null) return;
        ignoreThisTime = true;
    }


    /**
     * 阻塞器，用于线程保活
     */
    private static class Blocker {
        public synchronized void keepAlive() {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public synchronized void dead() {
            this.notify();
        }
    }
}