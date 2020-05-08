package com.rubbertranslator.manager;

import com.rubbertranslator.modules.filter.ProcessFilter;
import org.jnativehook.GlobalScreen;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/8 10:00
 */
public class TranslatorFacade {

    // 进程过滤器
    private ProcessFilter processFilter;

    public TranslatorFacade() {
    }

    public void setProcessFilter(ProcessFilter processFilter) {
        this.processFilter = processFilter;
    }

    public void process(String text){
        // do translate works
        if(processFilter.check()) return;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO,text);
    }
}
