package com.rubbertranslator.filter.activewindowfilter;

import com.rubbertranslator.misc.ThreadKeepAlive;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WindowsPlatformActiveWindowListenerThread extends Thread{
    // 最长title长度
    private static final int MAX_TITLE_LENGTH = 1024;
    private ActiveWindowListener activeWindowListener;

    public WindowsPlatformActiveWindowListenerThread(){

    }

    public WindowsPlatformActiveWindowListenerThread(ActiveWindowListener activeWindowListener){
        this.activeWindowListener = activeWindowListener;
    }

    public void setActiveWindowListener(ActiveWindowListener activeWindowListener) {
        this.activeWindowListener = activeWindowListener;
    }

    @Override
    public void run() {
        String lastTitle = "none";
        String lastProcess = "none";
        long lastChange = System.currentTimeMillis();

        while (true) {
            String currentTitle = getActiveWindowTitle();
            String currentProcess = getActiveWindowProcess();
            if (!lastTitle.equals(currentTitle)) {
                long change = System.currentTimeMillis();
                long time = (change - lastChange) / 1000;
                lastChange = change;
                Logger.getLogger(this.getClass().getName()).log(Level.INFO,"Change! Last title: " + lastTitle + " lastProcess: " + lastProcess + " time: " + time + " seconds");
                lastTitle = currentTitle;
                lastProcess = currentProcess;
                if(activeWindowListener!=null){
                    activeWindowListener.onActiveWindowChange(lastProcess);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING,ex.getMessage());
            }
        }
    }

    public interface ActiveWindowListener{
        void onActiveWindowChange(String processName);
    }


    private static String getActiveWindowTitle() {
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        HWND foregroundWindow = User32DLL.GetForegroundWindow();
        User32DLL.GetWindowTextW(foregroundWindow, buffer, MAX_TITLE_LENGTH);
        String title = Native.toString(buffer);
        return title;
    }

    private static String getActiveWindowProcess() {
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        PointerByReference pointer = new PointerByReference();
        HWND foregroundWindow = User32DLL.GetForegroundWindow();
        User32DLL.GetWindowThreadProcessId(foregroundWindow, pointer);
        Pointer process = Kernel32.OpenProcess(Kernel32.PROCESS_QUERY_INFORMATION | Kernel32.PROCESS_VM_READ, false, pointer.getValue());
        Psapi.GetModuleBaseNameW(process, null, buffer, MAX_TITLE_LENGTH);
        String processName = Native.toString(buffer);
        return processName;
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
