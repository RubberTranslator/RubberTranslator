package com.rubbertranslator.filter.activewindowfilter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/7 12:48
 */
public class ProcessFilter {
    // 过滤名单
    private final Set<String> filterList = new HashSet<>();

    /**
     * 添加过滤
     * @param processNames 需要过滤的进程名字
     */
    public void addFilter(String ...processNames){
        this.filterList.addAll(Arrays.asList(processNames));
    }

    /**
     * 添加过滤集
     * @param list 需要过滤的进程名
     */
    public void addFilterList(Collection<String> list){
        this.filterList.addAll(list);
    }


    /**
     * 判定是否需要过滤
     * @param currentProcess 当前进程名
     * @return 是否需要过滤当前进程
     */
    public boolean checkFilter(String currentProcess){
        return filterList.contains(currentProcess);
    }
}
