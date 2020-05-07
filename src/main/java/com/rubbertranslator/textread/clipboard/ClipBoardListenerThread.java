package com.rubbertranslator.textread.clipboard;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClipBoardListenerThread extends Thread {

    // 程序终止
    private volatile boolean exit = false;

    // 程序暂停
    private volatile boolean running = true;
    private Object blocker = new Object();

    @Override
    public void run() {
        String initial = "";
        while (!exit) {
            try {
                // 怎么避免浪费CPU时间？
                Thread.sleep(200);
                if (!running) blocker.wait();
                Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                String paste = c.getContents(null).getTransferData(DataFlavor.stringFlavor).toString();
                if (!paste.equals(initial)) {
                    System.out.println(paste);
                    initial = paste;
                }
            } catch (InterruptedException | UnsupportedFlavorException | IllegalStateException | IOException ex) {
                Logger.getLogger(ClipBoardListenerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void pause() {
        running = false;
    }

    public void resumeRun() {
        running = true;
        blocker.notify();
    }

    public void exit() {
        exit = true;
    }

}