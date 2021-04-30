package com.rubbertranslator.mvp.modules.filter;

import com.rubbertranslator.event.ActiveWindowChangeEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/7 12:48
 */
public class ProcessFilter {
    // 当前进程名
    private String currentProcess = "";
    // 是否打开过滤器
    private boolean open = true;

    public ProcessFilter() {
        // 注册消息监听（这里监听active window)
        EventBus.getDefault().register(this);
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    // 过滤名单
    private final Set<String> filterList = new HashSet<>();

    public Set<String> getFilterList() {
        return filterList;
    }

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

    public void removeFilter(String processName){this.filterList.remove(processName);}

    public void removeFilterLists(Collection<String> list){
        this.filterList.removeAll(list);
    }

    public void setFilterList(Collection<String> list) {
        // xxx: 暴力清除，性能降低
        filterList.clear();
        addFilterList(list);
        Logger.getLogger(this.getClass().getName()).info("set filter list");
        Logger.getLogger(this.getClass().getName()).info("过滤器打开：" + open);
    }


    /**
     * 判定是否需要过滤
     *
     * @return 是否需要过滤当前进程 true 过滤
     * false 不过滤
     */
    public boolean check() {
        if (!open) return false; // 没打开，不需要过滤
        return filterList.contains(currentProcess);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onActiveWindowChanged(ActiveWindowChangeEvent event) {
        if(event == null) return;
        currentProcess = event.currentProcessName;
    }
}
