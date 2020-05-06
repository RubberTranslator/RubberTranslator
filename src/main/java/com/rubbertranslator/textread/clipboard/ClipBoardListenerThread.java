package com.rubbertranslator.textread.clipboard;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;


public class ClipBoardListenerThread extends Thread implements ClipboardOwner {
    private final Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
    private final Fence fence = new Fence();

    public ClipBoardListenerThread(){
        Transferable trans = sysClip.getContents(this);
        sysClip.setContents(trans, this);
    }

    @Override
    public void run() {
        fence.block();
    }

    @Override
    public void lostOwnership(Clipboard c, Transferable t) {
        System.out.println(Thread.currentThread());
        Transferable contents = null;
        try {
            Thread.sleep(200);  //waiting e.g for loading huge elements like word's etc.
            contents = sysClip.getContents(this);
            processClipboard(contents, c);
            sysClip.setContents(t, this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (IllegalStateException e){
            // 剪贴板监听线程出现异常，重新启动一个
            System.out.println("异常");
            fence.unblock();
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
        public synchronized void block(){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public synchronized void unblock(){
            this.notify();
        }
    }
}