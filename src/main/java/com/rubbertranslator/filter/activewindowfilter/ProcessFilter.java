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
    private Set<String> filterList = new HashSet<>();

    /**
     * 添加过滤
     * @param processName
     */
    public void addFilter(String ...processNames){
        this.filterList.addAll(Arrays.asList(processNames));
    }

    /**
     * 添加过滤集
     * @param list
     */
    public void addFilterList(Collection<String> list){
        this.filterList.addAll(list);
    }


    /**
     * 判定是否需要过滤
     * @param currentProcess
     * @return
     */
    public boolean checkFilter(String currentProcess){
        if(filterList.contains(currentProcess)){
            return true;
        }else{
            return false;
        }
    }
}
