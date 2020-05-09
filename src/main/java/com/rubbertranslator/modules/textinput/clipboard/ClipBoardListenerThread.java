package com.rubbertranslator.modules.textinput.clipboard;


import com.rubbertranslator.modules.textinput.TextInputListener;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;


public class ClipBoardListenerThread extends Thread {
    // 系统剪切板
    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

    // 程序终止
    private volatile boolean exit = false;
    // 程序暂停
    private volatile boolean running = true;
    private final Object blocker = new Object();

    //
    private TextInputListener textInputListener;

    public void setTextInputListener(TextInputListener textInputListener) {
        this.textInputListener = textInputListener;
    }

    public ClipBoardListenerThread(TextInputListener listener) {
        this.textInputListener = listener;
    }

    @Override
    public void run() {
        final long minWaitTime = 100;
        final long maxWaitTime = 3000;
        // 动态等待时间
        long waitTime = minWaitTime;

        String initialText = "";
        Image initialImage = new BufferedImage(1, 1, TYPE_INT_RGB);
        while (!exit) {
            try {
                // xxx:怎么避免浪费CPU时间？
                Thread.sleep(waitTime);
                if (!running) blocker.wait();

                Transferable t = clipboard.getContents(null);
                // XXX: 下面的代码判断重复多余，但是尚未找到好的方法来区别不同的Transferable
                if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    String paste = (String) t.getTransferData(DataFlavor.stringFlavor);
                    if (!Objects.equals(paste, initialText) && textInputListener != null) {
                        initialText = paste;
                        textInputListener.onTextInput(paste);
                    }
                } else if (t.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                    Image paste = (Image) t.getTransferData(DataFlavor.imageFlavor);
                    if ((paste.getWidth(null) != initialImage.getWidth(null) || paste.getHeight(null) != initialImage.getHeight(null))
                            && textInputListener != null) {
                        initialImage = paste;
                        textInputListener.onImageInput(paste);
                    }
                }

            } catch (InterruptedException | IllegalStateException | UnsupportedFlavorException | IOException e) {
                Logger.getLogger(ClipBoardListenerThread.class.getName()).log(Level.SEVERE, e.getMessage(), e);
                if (waitTime < maxWaitTime) {
                    waitTime += 100;  // 增加100ms等待时间
                }
            } finally {
                if (waitTime > minWaitTime) {
                    waitTime -= 10;   // 减少10ms等待时间
                }
            }
        }
    }

//    public void pause() {
//        running = false;
//    }
//
//    public void resumeRun() {
//        running = true;
//        blocker.notify();
//    }
//
//    public void exit() {
//        exit = true;
//    }
}