package com.rubbertranslator.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * @author Raven
 * @version 1.0
 * date  2020/12/4 10:57
 */
public class AppSingletonUtil {

    private static String localFileName = "rt.lck";

    public static boolean isAppRunning() {
        RandomAccessFile randomFile = null;
        FileChannel channel = null;
        try {
            // create lock file
            File fileLock = new File(localFileName);
            if (!fileLock.exists()) {
                fileLock.createNewFile();
            }
            // get lock
            randomFile =
                    new RandomAccessFile(localFileName, "rw");
            channel = randomFile.getChannel();
            if (channel.tryLock() == null)
                return true;
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                if (channel != null) channel.close();
                if (randomFile != null) randomFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
