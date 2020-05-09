package com.rubbertranslator.modules.textinput.mousecopy.initiator;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/6 12:59
 */
public class MouseCopyInitiator {
    public static void initialize() {
        GlobalScreenHookInitiator.initialize();
        // xxx：功能多了以后，系统托盘初始化不应该在这里
        SystemTrayInitiator.initialize();
    }
}
