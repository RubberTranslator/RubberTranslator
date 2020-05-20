package com.rubbertranslator.modules.filter;

import com.rubbertranslator.event.ActiveWindowChangeEvent;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;
import org.greenrobot.eventbus.EventBus;

import java.util.logging.Logger;

public class WindowsPlatformActiveWindowListenerThread extends Thread {
    // 最长title长度
    private static final int MAX_TITLE_LENGTH = 1024;
    // 线程是否需要退出
    private volatile boolean stop = false;

    public WindowsPlatformActiveWindowListenerThread() {
        setName("ActiveWindowListener Thread");
    }



    @Override
    public void run() {
        String lastProcess = "";
        ActiveWindowChangeEvent activeWindowChangeEvent = new ActiveWindowChangeEvent();
        while (!stop) {
            String currentProcess = getActiveWindowProcess();
            if (!lastProcess.equals(currentProcess)) {
                lastProcess = currentProcess;
                // 推送active windows变化消息
                activeWindowChangeEvent.setCurrentProcessName(lastProcess);
                EventBus.getDefault().post(activeWindowChangeEvent);
            }
            try {
                // XXX:浪费CPU时间
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(this.getClass().getName()).warning(ex.getMessage());
            }
        }
        Logger.getLogger(this.getClass().getName()).info("ActiveWindowListenerThread exit");
    }

    public void exit() {
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
