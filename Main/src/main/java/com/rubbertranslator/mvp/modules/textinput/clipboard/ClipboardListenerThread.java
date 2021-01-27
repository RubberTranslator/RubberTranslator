package com.rubbertranslator.mvp.modules.textinput.clipboard;


import com.rubbertranslator.event.ClipboardContentInputEvent;
import com.rubbertranslator.mvp.modules.filter.ProcessFilter;
import com.rubbertranslator.utils.OSTypeUtil;
import org.greenrobot.eventbus.EventBus;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.Objects;
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
        if (OSTypeUtil.isMac()) {     // mac 单独处理
            macOSProcess();
        } else {          // win, linux 处理
            Transferable trans = clipboard.getContents(this);
            clipboard.setContents(trans, this);
        }
    }

    private void macOSProcess() {
        // mac os 采用 javafx clipboard专用接口来处理
        javafx.scene.input.Clipboard cb = javafx.scene.input.Clipboard.getSystemClipboard();

        String recentTextContent = null;
        Image recentImageContent = null;
        boolean firstIsText = false;
        boolean firstImageIsSet = false;

        // continuously perform read from clipboard
        while (true) {      // mac os 没有lostOwnership自动唤醒机制（除非获取focus)， 所以只能采用轮询方式，有点浪费cpu
            try {
                Thread.sleep(200);
                if (!running) {
                    break;
                }
                Transferable t = clipboard.getContents(null);
                if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    firstIsText = true;     // 为了避免第一次app程序启动时，图片直接翻译的bug

                    String paste = (String) t.getTransferData(DataFlavor.stringFlavor);
                    if (recentTextContent == null) {      // 为了避免第一次启动，文本直接翻译的bug
                        recentTextContent = paste;
                        continue;
                    }
                    if (!Objects.equals(paste, recentTextContent)) {
                        recentTextContent = paste;
                        if (ignoreThisTime) {
                            ignoreThisTime = false;
                            continue;
                        }
                        if (processFilter != null && !processFilter.check()) {
                            textInputEvent.setText(paste);
                            Logger.getLogger(this.getClass().getName()).info("剪切板有新内容:" + paste);
                            EventBus.getDefault().post(textInputEvent);
                        }
                    }
                } else if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                    Image paste = (Image) t.getTransferData(DataFlavor.imageFlavor);
                    if (!firstIsText && !firstImageIsSet) {   // 为了避免第一次app程序启动时，图片直接翻译的bug
                        recentImageContent = paste;
                        firstImageIsSet = true;
                        continue;
                    }
                    if (recentImageContent == null ||        // 检测图片是否更新，如果现在的clipboard中的图片和之前的图片的宽高是一样的，就认为是相同的图片
                            recentImageContent.getWidth(null) != paste.getWidth(null) ||
                            recentImageContent.getHeight(null) != paste.getHeight(null)) {
                        recentImageContent = paste;
                        if (processFilter != null && !processFilter.check()) {
                            if (ignoreThisTime) {
                                ignoreThisTime = false;
                                continue;
                            }
                            textInputEvent.setImage(paste);
                            Logger.getLogger(this.getClass().getName()).info("剪切板有新内容:" + "图片输入");
                            EventBus.getDefault().post(textInputEvent);
                        }
                    }
                }
            } catch (HeadlessException | UnsupportedFlavorException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void lostOwnership(Clipboard c, Transferable t) {
        if (OSTypeUtil.isMac()) {        // MAC 只能采用轮训
            return;
        }
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
        if (ignoreThisTime) {
            Logger.getLogger(this.getClass().getName()).info("本次已忽略");
            ignoreThisTime = false;
            return;
        }

        Logger.getLogger(this.getClass().getName()).info("本次正常处理");
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