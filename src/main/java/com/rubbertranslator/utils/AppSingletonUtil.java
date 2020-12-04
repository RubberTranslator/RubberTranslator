package com.rubbertranslator.utils;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

/**
 * @author Raven
 * @version 1.0
 * date  2020/12/4 10:57
 */
public class AppSingletonUtil {
    public static boolean isAppRunning(){
        boolean alreadyRunning;
        try {
            JUnique.acquireLock("RubberTranslator");
            alreadyRunning = false;
        } catch (AlreadyLockedException e) {
            alreadyRunning = true;
        }
        return alreadyRunning;
    }
}
