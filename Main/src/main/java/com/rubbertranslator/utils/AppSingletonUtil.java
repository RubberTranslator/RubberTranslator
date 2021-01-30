package com.rubbertranslator.utils;

import com.rubbertranslator.App;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * @author Raven
 * @version 1.0
 * date  2020/12/4 10:57
 */
public class AppSingletonUtil {
    public static boolean isAppRunning() {
        try {
            RandomAccessFile randomFile =
                    new RandomAccessFile(App.class.getSimpleName() + ".class", "rw");

            FileChannel channel = randomFile.getChannel();

            if (channel.tryLock() == null)
                return true;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return false;
    }
}
