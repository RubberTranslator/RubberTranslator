package com.rubbertranslator.misc;

/**
 * 阻塞器，用于线程保活
 */
public class ThreadKeepAlive {
    public synchronized void keepAlive() {
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void dead() {
        this.notify();
    }
}