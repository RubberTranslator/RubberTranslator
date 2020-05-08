package com.rubbertranslator.modules.textinput.mousecopy;
import com.rubbertranslator.modules.textinput.mousecopy.initiator.MouseCopyInitiator;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/7 15:01
 */
public class MouseCopyThread extends Thread{
    @Override
    public void run() {
        // 初始化后就退出，无需做保活
        MouseCopyInitiator.initialize();
    }
}
