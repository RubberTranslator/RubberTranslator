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


public class ClipBoardListener implements ClipboardOwner, MessageEntity<String> {
    // 系统剪切板
    private final Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
    // 动态等待时间
    private long waitTime = 50;
    // 中介依赖
    private final Mediator mediator = ThreadMediator.getInstance();
    // 剪切板内容处理
    private ClipboardContentProcessor cp = new ClipboardContentProcessor();

    public ClipBoardListener(){
        // 注册中介
        mediator.register(Module.TEXT_READ_MODEL_CLIPBOARD_LISTENER,this);
        // 注册ocr模块

        // 关闭log
//        Logger.getLogger(ClipBoardListenerThread.class.getName()).setLevel(Level.OFF);
        // 开启剪切板监听
        Transferable trans = sysClip.getContents(this);
        sysClip.setContents(trans, this);
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
                sendMsg(cp.process(contents));
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


    @Override
    public void receiveMsg(String msg) {
        // empty
    }

    @Override
    public void sendMsg(String msg) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,msg);
        /*if(msg == null) return;
        try {
            mediator.passMessage(Module.TEXT_READ_MODEL_CLIPBOARD_LISTENER,Module.FILTER_MODULE_ACTIVE_WINDOW_LISTENER,msg);
        } catch (UnRegisterException e) {
            e.printStackTrace();
        }*/
    }
}