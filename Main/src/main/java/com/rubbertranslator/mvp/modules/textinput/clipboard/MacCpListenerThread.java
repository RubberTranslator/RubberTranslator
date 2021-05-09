package com.rubbertranslator.mvp.modules.textinput.clipboard;

import com.rubbertranslator.event.ClipboardContentInputEvent;
import com.rubbertranslator.mvp.modules.filter.ProcessFilter;
import com.rubbertranslator.thread.CondVar;
import com.rubbertranslator.utils.OSTypeUtil;
import org.greenrobot.eventbus.EventBus;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MacCpListenerThread extends ClipboardListenerThread{
    // running CondVar
    private final CondVar runningCond = new CondVar();
    // 程序暂停
    private volatile boolean running = true;
    // 退出状态flag
    private volatile boolean exitFlag = false;
    // 跳过本次变化监听
    private volatile AtomicBoolean ignoreThisTime = new AtomicBoolean(false);

    public MacCpListenerThread() {
        setName("MacClipboard Thread");
    }

    @Override
    protected void startProcess() {
        String recentTextContent = null;
        Image recentImageContent = null;
        boolean firstIsText = false;
        boolean firstImageIsSet = false;

        // continuously perform read from clipboard
        while (!exitFlag) {      // mac os 没有lostOwnership自动唤醒机制（除非获取focus)， 所以只能采用轮询方式，有点浪费cpu
            try {
                Thread.sleep(200);
                synchronized (runningCond) {        // 性能损失应该很大
                    while (!running) {
                        runningCond.wait();
                    }
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
                        if (ignoreThisTime.get()) {
                            ignoreThisTime.set(false);
                            continue;
                        }
                        if (processFilter != null && !processFilter.check()) {
                            textInputEvent.text = paste;
                            textInputEvent.isTextType = true;
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
                            if (ignoreThisTime.get()) {
                                ignoreThisTime.set(false);
                                continue;
                            }
                            textInputEvent.image = paste;
                            textInputEvent.isTextType = false;
                            Logger.getLogger(this.getClass().getName()).info("剪切板有新内容:" + "图片输入");
                            EventBus.getDefault().post(textInputEvent);
                        }
                    }
                }
            } catch (HeadlessException | UnsupportedFlavorException | IOException | InterruptedException | IllegalStateException e) {
                e.printStackTrace();
                Logger.getLogger(this.getClass().getName()).info(e.getLocalizedMessage());
            }
        }
    }

    @Override
    protected void resumeProcess() {
        synchronized (runningCond){
            running = true;
            runningCond.notifyAll();
        }
    }

    @Override
    protected void pauseProcess() {
        synchronized (runningCond){
            running = false;
            runningCond.notifyAll();
        }
    }

    public void ignoreThisTime() {
        if(!ignoreThisTime.get()){
           ignoreThisTime.set(true);
        }
    }

    public void exit() {
        // 保证 process thread is runnning
        resumeProcess();
        exitFlag = true;
    }

}
