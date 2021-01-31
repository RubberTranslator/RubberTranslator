package com.rubbertranslator.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date  2020/12/4 10:57
 */
public class AppSingletonUtil {

    private static String localFileName;

    static{
        if(OSTypeUtil.isMac()){
           localFileName = System.getProperty("user.home") + "/RubberTranslator/rt.lock";
        }else {
            localFileName = System.getProperty("user.dir") + "/RubberTranslator/rt.lock";
        }
    }

    /**
     * 通过检测文件锁，来判别是否有程序已经启动，释放资源由操作系统来做
     * @return
     */
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
            Logger.getLogger(AppSingletonUtil.class.getName()).severe(e.getLocalizedMessage());
        }
        return false;
    }
}
