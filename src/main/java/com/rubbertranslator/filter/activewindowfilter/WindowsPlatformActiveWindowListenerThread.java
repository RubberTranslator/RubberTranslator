package com.rubbertranslator.filter.activewindowfilter;
import com.rubbertranslator.mediator.*;
import com.rubbertranslator.mediator.Module;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WindowsPlatformActiveWindowListenerThread extends Thread implements MessageEntity<String> {
    // 最长title长度
    private static final int MAX_TITLE_LENGTH = 1024;
    // 中介
    private final Mediator mediator = ThreadMediator.getInstance();
    // 记录当前进程名
    private String lastProcess;
    // 线程是否需要退出
    private volatile boolean stop = false;
    // 进程过滤器
    private ProcessFilter processFilter = new ProcessFilter();

    public WindowsPlatformActiveWindowListenerThread(){
        // 注册中介
        mediator.register(Module.FILTER_MODULE_ACTIVE_WINDOW_LISTENER,this);
        // 注册需要过滤的进程 TODO: 添加过滤配置文件读取
        processFilter.addFilter("idea64.exe","chrome.exe");
        // 关闭logg
        Logger.getLogger(this.getClass().getName()).setLevel(Level.OFF);
    }


    @Override
    public void run() {
        lastProcess = "none";
        long lastChange = System.currentTimeMillis();

        while (!stop) {
            String currentProcess = getActiveWindowProcess();
            if (!lastProcess.equals(currentProcess)) {
                long change = System.currentTimeMillis();
                long time = (change - lastChange) / 1000;
                lastChange = change;
                Logger.getLogger(this.getClass().getName()).log(Level.INFO," lastProcess: " + lastProcess + " time: " + time + " seconds");
                lastProcess = currentProcess;
            }
            try {
                // TODO:浪费CPU时间
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING,ex.getMessage());
            }
        }
    }

    public void exit(){
        stop = true;
    }


    private static String getActiveWindowProcess() {
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        PointerByReference pointer = new PointerByReference();
        HWND foregroundWindow = User32DLL.GetForegroundWindow();
        User32DLL.GetWindowThreadProcessId(foregroundWindow, pointer);
        Pointer process = Kernel32.OpenProcess(Kernel32.PROCESS_QUERY_INFORMATION | Kernel32.PROCESS_VM_READ, false, pointer.getValue());
        Psapi.GetModuleBaseNameW(process, null, buffer, MAX_TITLE_LENGTH);
        return Native.toString(buffer);
    }

    /**
     * 过滤进程
     * @param msg 传递的消息
     */
    private void doFilter(String msg){
        if(processFilter.checkFilter(lastProcess)){
            sendMsg(null);
        }else{
            sendMsg(msg);
        }
    }

    @Override
    public void receiveMsg(String msg) {
        Logger.getLogger(this.getClass().getName()).log(Level.WARNING,"拦截器接收到消息");
        // 收到消息，开始做过滤
        doFilter(msg);
    }

    @Override
    public void sendMsg(String msg) {
        try{
            if(msg == null){
                mediator.passMessage(Module.FILTER_MODULE_ACTIVE_WINDOW_LISTENER,null,null);
            }else{
                mediator.passMessage(Module.FILTER_MODULE_ACTIVE_WINDOW_LISTENER,Module.TEXT_PRE_FORMATTER_MODULE,msg);
            }
        } catch (UnRegisterException e) {
            e.printStackTrace();
        }

    }

    static class Psapi {
        static {
            Native.register("psapi");
        }

        public static native int GetModuleBaseNameW(Pointer hProcess, Pointer hmodule, char[] lpBaseName, int size);
    }

    static class Kernel32 {
        static {
            Native.register("kernel32");
        }

        public static int PROCESS_QUERY_INFORMATION = 0x0400;
        public static int PROCESS_VM_READ = 0x0010;

        public static native Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, Pointer pointer);
    }

    static class User32DLL {
        static {
            Native.register("user32");
        }

        public static native int GetWindowThreadProcessId(HWND hWnd, PointerByReference pref);

        public static native HWND GetForegroundWindow();

        public static native int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);
    }
}
