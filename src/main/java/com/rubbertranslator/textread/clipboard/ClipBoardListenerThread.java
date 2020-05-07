package com.rubbertranslator.textread.clipboard;

import com.rubbertranslator.mediator.Mediator;
import com.rubbertranslator.mediator.MessageEntity;
import com.rubbertranslator.mediator.Module;
import com.rubbertranslator.mediator.ThreadMediator;
import com.rubbertranslator.mediator.UnRegisterException;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClipBoardListenerThread extends Thread implements ClipboardOwner, MessageEntity<String> {
    // 系统剪切板
    private final Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
    // fence用于线程保活
    private final Fence fence = new Fence();
    // 动态等待时间
    private long waitTime = 50;
    // 中介依赖
    private Mediator mediator = ThreadMediator.getInstance();

    public ClipBoardListenerThread(){
        // 注册中介
        mediator.register(Module.TEXT_READ_MODEL_CLIPBOARD_LISTENER,this);
        // 开启剪切板监听
        Transferable trans = sysClip.getContents(this);
        sysClip.setContents(trans, this);
    }

    @Override
    public void run() {
        fence.keepAlive();
    }

    @Override
    public void lostOwnership(Clipboard c, Transferable t) {
        Transferable contents;
        // 循环require clipboard owner
        boolean required = false;
        final long minWaitTime = 50;
        final long maxWaitTime = 3000;
        // 动态等待时间
        while(!required){
            try {
                //waiting e.g for loading huge elements like word's etc.
                // TODO:浪费CPU时间
                Thread.sleep(waitTime);
                contents = sysClip.getContents(null);
                sysClip.setContents(t, this);
                required = true;
                processClipboard(contents, c);
            } catch (InterruptedException | IllegalStateException e) {
                if(waitTime < maxWaitTime){
                    waitTime+=100;  // 增加100ms等待时间
                }
                e.printStackTrace();
            }finally {
                if(waitTime > minWaitTime){
                    waitTime-=10;   // 减少10ms等待时间
                }
            }
        }

    }

    public void processClipboard(Transferable t, Clipboard c) { //your implementation
        try {
            if (t != null && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                String tempText = (String) t.getTransferData(DataFlavor.stringFlavor);
                sendMsg(tempText);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void receiveMsg(String msg) {
        // empty
    }

    @Override
    public void sendMsg(String msg) {
        try {
            mediator.passMessage(Module.TEXT_READ_MODEL_CLIPBOARD_LISTENER,Module.FILTER_MODULE_ACTIVE_WINDOW_LISTENER,msg);
        } catch (UnRegisterException e) {
            e.printStackTrace();
        }
    }


    /**
     * 阻塞器，用于线程保活
     */
    private static class Fence{
        public synchronized void keepAlive(){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public synchronized void dead(){
            this.notify();
        }
    }
}