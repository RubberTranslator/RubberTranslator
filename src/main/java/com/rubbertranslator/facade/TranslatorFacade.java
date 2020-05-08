package com.rubbertranslator.facade;

import com.rubbertranslator.filter.activewindowfilter.ProcessFilter;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/8 10:00
 */
public class TranslatorFacade {

    // 进程过滤器
    private ProcessFilter processFilter;

    public void setProcessFilter(ProcessFilter processFilter) {
        this.processFilter = processFilter;
    }

    public void process(String text){
        // do translate works
        if(processFilter.check()) return;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,text);
    }
}
