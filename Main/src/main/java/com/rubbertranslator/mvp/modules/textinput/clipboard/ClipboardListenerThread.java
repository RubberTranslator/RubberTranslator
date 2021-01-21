package com.rubbertranslator.mvp.modules.textinput.clipboard;


import com.rubbertranslator.event.ClipboardContentInputEvent;
import com.rubbertranslator.mvp.modules.filter.ProcessFilter;
import org.greenrobot.eventbus.EventBus;

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
    private volatile boolean ignoreThisTime = false;
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
//        destroy();
        Logger.getLogger(this.getClass().getName()).info("ClipBoard exit");
    }

    private void init() {
        // 注册消息监听
        // 初始化剪切板监听
        Transferable trans = clipboard.getContents(this);
        clipboard.setContents(trans, this);
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
                    Logger.getLogger(this.getClass().getName()).info("本次已忽略");
                    ignoreThisTime = false;
                    break;
                } else {
                    Logger.getLogger(this.getClass().getName()).info("本次正常处理");
                }
                processClipboard(contents);
            } catch (Exception e) {
                if (waitTime < maxWaitTime) {
                    waitTime += 100;  // 增加100ms等待时间
                }
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "剪切板被抢占，尝试延时后重试");
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
                Logger.getLogger(this.getClass().getName()).info("剪切板有新内容:" + paste);
                EventBus.getDefault().post(textInputEvent);
            }
        } else if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            Image paste = (Image) t.getTransferData(DataFlavor.imageFlavor);
            if (processFilter != null && !processFilter.check()) {
                textInputEvent.setImage(paste);
                Logger.getLogger(this.getClass().getName()).info("剪切板有新内容:" + "图片输入");
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

    public void ignoreThisTime() {
        synchronized (this) {
            if (!ignoreThisTime) ignoreThisTime = true;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void exit() {
        blocker.dead();
    }


    /**
     * 阻塞器，用于线程保活
     */
    private static class Blocker {
        public synchronized void keepAlive() {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "ClipboardThread保活失败");
            }
        }

        public synchronized void dead() {
            this.notify();
        }
    }
}