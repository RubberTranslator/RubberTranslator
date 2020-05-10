package com.rubbertranslator;

import com.rubbertranslator.modules.system.LoggerManager;
import com.rubbertranslator.modules.TranslatorFacade;
import com.rubbertranslator.modules.filter.ProcessFilter;
import com.rubbertranslator.modules.filter.WindowsPlatformActiveWindowListenerThread;
import com.rubbertranslator.modules.textinput.TextInputCollector;
import com.rubbertranslator.modules.textinput.clipboard.ClipBoardListenerThread;
import com.rubbertranslator.modules.textinput.mousecopy.MouseCopyThread;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/8 10:11
 * 启动所有线程，初始化所有组件
 */
public class Starter {
    public TranslatorFacade start() {
        // 日志
        LoggerManager.configLog();

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
        return facade;

    }

}
