package com.rubbertranslator;

import com.rubbertranslator.facade.TranslatorFacade;
import com.rubbertranslator.filter.activewindowfilter.ProcessFilter;
import com.rubbertranslator.filter.activewindowfilter.WindowsPlatformActiveWindowListenerThread;
import com.rubbertranslator.textread.TextInputCollector;
import com.rubbertranslator.textread.clipboard.ClipBoardListenerThread;
import com.rubbertranslator.textread.mousecopy.MouseCopyThread;
import com.rubbertranslator.textread.mousecopy.initiator.MouseCopyInitiator;

import java.awt.event.WindowStateListener;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/8 10:11
 * 启动所有线程，初始化所有组件
 */
public class Starter {
    public void start(){
        TranslatorFacade facade = new TranslatorFacade();

        // 1. 活动窗口监听线程 only for windows platform
//        if(windows平台)
        ProcessFilter processFilter = new ProcessFilter();
        WindowsPlatformActiveWindowListenerThread activeWindowListenerThread = new WindowsPlatformActiveWindowListenerThread(processFilter);
        // 2. 剪切板状态监听线程
        TextInputCollector textInputCollector = new TextInputCollector(facade);
        ClipBoardListenerThread clipBoardListenerThread = new ClipBoardListenerThread(textInputCollector);
        // 3. 鼠标复制线程
        MouseCopyThread mouseCopyThread = new MouseCopyThread();

        //4. 后续模块注入
        facade.setProcessFilter(processFilter);

        // 启动所有线程
        activeWindowListenerThread.start();
        clipBoardListenerThread.start();
        mouseCopyThread.start();
    }

}
