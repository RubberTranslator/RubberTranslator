package com.rubbertranslator.textread.clipboard;

import com.sun.javafx.iio.ImageStorage;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;


public class ClipBoardListenerThread extends Thread {

    // 程序终止
    private volatile boolean exit = false;

    // 程序暂停
    private volatile boolean running = true;
    private Object blocker = new Object();

    @Override
    public void run() {
        String initialText = "";
        Image initialImage = new BufferedImage(1,1,TYPE_INT_RGB);
        while (!exit) {
            try {
                // 怎么避免浪费CPU时间？
                Thread.sleep(200);
                if (!running) blocker.wait();
                Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable t = c.getContents(null);
                if(t.isDataFlavorSupported(DataFlavor.stringFlavor)){
                    String paste = (String) t.getTransferData(DataFlavor.stringFlavor);
                    if (!Objects.equals(paste,initialText)) {
                        System.out.println(paste);
                        initialText = paste;
                    }
                }else if(t.isDataFlavorSupported(DataFlavor.imageFlavor)){
                    Image paste = (Image) t.getTransferData(DataFlavor.imageFlavor);
                    if(paste.getWidth(null) != initialImage.getWidth(null)){
                        System.out.println(paste);
                        initialImage = paste;
                    }
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