package com.rubbertranslator.mvp.modules.textinput.clipboard;

import com.rubbertranslator.thread.CondVar;
import com.rubbertranslator.utils.OSTypeUtil;
import org.greenrobot.eventbus.EventBus;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WinCpListenerThread extends ClipboardListenerThread implements ClipboardOwner {
    // 程序暂停
    private volatile boolean running = true;
    // 动态等待时间
    private long waitTime = 50;
    // 跳过本次变化监听
    private final AtomicBoolean ignoreThisTime = new AtomicBoolean();
    // Thread Blocker
    private final CondVar blocker = new CondVar();

    public WinCpListenerThread() {
        setName("WinClipboard Thread");
    }

    @Override
    protected void startProcess() {
        Transferable trans = clipboard.getContents(this);
        clipboard.setContents(trans, this);
        try {
            // To prevent this thread dead
            blocker.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lostOwnership(Clipboard c, Transferable t) {
        assert (!OSTypeUtil.isMac());
        Transferable contents;
        // 循环require clipboard owner
        boolean required = false;
        final long minWaitTime = 50;
        final long maxWaitTime = 3000;

        while (!required) {
            try {
                //waiting e.g for loading huge elements like word's etc.
                Thread.sleep(waitTime);
                if (!running) {
                    break;
                }
                contents = clipboard.getContents(null);
                clipboard.setContents(contents, this);
                required = true;
                dispatchCpContent(contents);
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

    private void dispatchCpContent(Transferable t) throws IOException, UnsupportedFlavorException {
        if (ignoreThisTime.get()) {
            Logger.getLogger(this.getClass().getName()).info("本次已忽略");
            ignoreThisTime.set(false);
            return;
        }

        Logger.getLogger(this.getClass().getName()).info("本次正常处理");
        if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String paste = (String) t.getTransferData(DataFlavor.stringFlavor);
            if (processFilter != null && !processFilter.check()) {
                textInputEvent.text = paste;
                textInputEvent.isTextType = true;
                Logger.getLogger(this.getClass().getName()).info("剪切板有新内容:" + paste);
                EventBus.getDefault().post(textInputEvent);
            }
        } else if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            Image paste = (Image) t.getTransferData(DataFlavor.imageFlavor);
            if (processFilter != null && !processFilter.check()) {
                textInputEvent.image = paste;
                textInputEvent.isTextType = false;
                Logger.getLogger(this.getClass().getName()).info("剪切板有新内容:" + "图片输入");
                EventBus.getDefault().post(textInputEvent);
            }
        }
    }

    @Override
    protected void resumeProcess() {
        running = true;
    }

    @Override
    protected void pauseProcess() {
        running = false;
    }

    public void ignoreThisTime() {
        if (!ignoreThisTime.get()) {
            ignoreThisTime.set(true);
        }
    }

    public void exit() {
        synchronized (blocker){
            blocker.notifyAll();
        }
    }
}
