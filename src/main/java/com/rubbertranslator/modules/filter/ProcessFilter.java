package com.rubbertranslator.modules.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/7 12:48
 */
public class ProcessFilter implements ActiveWindowListener {
    // 当前进程名 XXX:是否违反单一职责？
    private String currentProcess = "";
    // 是否打开过滤器
    private boolean open = true;

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    // 过滤名单
    private final Set<String> filterList = new HashSet<>();

    /**
     * 添加过滤
     *
     * @param processName 需要过滤的进程名字
     */
    public void addFilter(String processName) {
        this.filterList.add(processName);
    }

    /**
     * 添加过滤集
     *
     * @param list 需要过滤的进程名
     */
    public void addFilterList(Collection<String> list) {
        this.filterList.addAll(list);
    }

    public void setFilterList(Collection<String> list){
        // xxx: 暴力清除，性能降低
        filterList.clear();
        addFilterList(list);
        Logger.getLogger(this.getClass().getName()).info("set filter list");
        Logger.getLogger(this.getClass().getName()).info("过滤器打开："+open);
    }


    /**
     * 判定是否需要过滤
     *
     * @return 是否需要过滤当前进程 true 过滤
     * false 不过滤
     */
    public boolean check() {
        if(!open) return false; // 没打开，不需要过滤
        Logger.getLogger(this.getClass().getName()).info("current process "+currentProcess);
        return filterList.contains(currentProcess);
    }

    @Override
    public void onActiveWindowChanged(String processName) {
        currentProcess = processName;
    }
}
