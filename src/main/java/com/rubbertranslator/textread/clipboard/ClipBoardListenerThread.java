package com.rubbertranslator.textread.clipboard;

import com.rubbertranslator.mediator.*;
import com.rubbertranslator.mediator.Module;
import com.sun.javafx.iio.ImageStorage;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;


public class ClipBoardListenerThread extends Thread implements MessageEntity<String> {
    // 系统剪切板
    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    // 中介依赖
    private final Mediator mediator = ThreadMediator.getInstance();
    // 剪切板内容处理
    private final ClipboardContentProcessor cp = new ClipboardContentProcessor();

    // 程序终止
    private volatile boolean exit = false;
    // 程序暂停
    private volatile boolean running = true;
    private final Object blocker = new Object();

    public ClipBoardListenerThread() {
        // 注册中介
        mediator.register(Module.TEXT_READ_MODEL_CLIPBOARD_LISTENER,this);
        // 注册ocr模块

        // 关闭log
//        Logger.getLogger(ClipBoardListenerThread.class.getName()).setLevel(Level.OFF);
    }

    @Override
    public void run() {
        final long minWaitTime = 100;
        final long maxWaitTime = 3000;
        // 动态等待时间
        long waitTime = minWaitTime;

        String initialText = "";
        Image initialImage = new BufferedImage(1,1,TYPE_INT_RGB);
        // 剪切板状态是否改变
        boolean stateChange = false;
        while (!exit) {
            try {
                // TODO:怎么避免浪费CPU时间？
                Thread.sleep(waitTime);
                if (!running) blocker.wait();

                Transferable t = clipboard.getContents(null);
                // TODO: 下面的代码判断重复而多余，但是尚未找到好的方法来区别不同的Transferable
                if(t.isDataFlavorSupported(DataFlavor.stringFlavor)){
                    String paste = (String) t.getTransferData(DataFlavor.stringFlavor);
                    if (!Objects.equals(paste,initialText)) {
                        initialText = paste;
                        stateChange = true;
                    }
                }else if(t.isDataFlavorSupported(DataFlavor.imageFlavor)){
                    Image paste = (Image) t.getTransferData(DataFlavor.imageFlavor);
                    if(paste.getWidth(null) != initialImage.getWidth(null) || paste.getHeight(null) != initialImage.getHeight(null)){
                        initialImage = paste;
                        stateChange = true;
                    }
                }
                // 状态改变，进行处理
                if(stateChange){
                    sendMsg(cp.process(t));
                    stateChange = false;
                }
            } catch (InterruptedException | IllegalStateException  | UnsupportedFlavorException | IOException e) {
                Logger.getLogger(ClipBoardListenerThread.class.getName()).log(Level.SEVERE, e.getMessage(), e);
                if(waitTime < maxWaitTime){
                    waitTime+=100;  // 增加100ms等待时间
                }
            } finally {
                if(waitTime > minWaitTime){
                    waitTime-=10;   // 减少10ms等待时间
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


    @Override
    public void receiveMsg(String msg) {

    }

    @Override
    public void sendMsg(String msg) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,msg);
        if(msg == null) return;
        try {
            mediator.passMessage(Module.TEXT_READ_MODEL_CLIPBOARD_LISTENER,Module.FILTER_MODULE_ACTIVE_WINDOW_LISTENER,msg);
        } catch (UnRegisterException e) {
            e.printStackTrace();
        }
    }
}