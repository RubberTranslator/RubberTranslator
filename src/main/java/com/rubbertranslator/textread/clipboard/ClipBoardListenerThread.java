package com.rubbertranslator.textread.clipboard;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;


public class ClipBoardListenerThread extends Thread implements ClipboardOwner {
    // 系统剪切板
    private final Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
    // fence用于线程保活
    private final Fence fence = new Fence();
    // 动态等待时间
    private long waitTime = 50;

    public ClipBoardListenerThread(){
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
                System.out.println(tempText);
            }
        } catch (Exception e) {
            System.out.println(e);
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