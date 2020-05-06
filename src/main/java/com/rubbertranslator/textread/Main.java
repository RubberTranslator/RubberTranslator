package com.rubbertranslator.textread;

import com.rubbertranslator.textread.clipboard.ClipBoardListenerThread;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/6 22:44
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        ClipBoardListenerThread thread = new ClipBoardListenerThread();
        thread.start();
        thread.join();
    }
}
