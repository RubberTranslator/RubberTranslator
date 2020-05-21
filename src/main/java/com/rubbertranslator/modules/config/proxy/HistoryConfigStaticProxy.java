package com.rubbertranslator.modules.config.proxy;

import com.rubbertranslator.modules.config.SystemConfiguration;
import com.rubbertranslator.system.SystemResourceManager;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/11 16:22
 */
public class HistoryConfigStaticProxy extends SystemConfiguration.HistoryConfig {
    private SystemConfiguration.HistoryConfig historyConfig;

    public SystemConfiguration.HistoryConfig getHistoryConfig() {
        return historyConfig;
    }

    public HistoryConfigStaticProxy(SystemConfiguration.HistoryConfig historyConfig) {
        this.historyConfig = historyConfig;
    }

    @Override
    public Integer getHistoryNum() {
        return historyConfig.getHistoryNum();
    }

    @Override
    public String toString() {
        return historyConfig.toString();
    }

    @Override
    public void setHistoryNum(Integer historyNum) {
        historyConfig.setHistoryNum(historyNum);
        SystemResourceManager.getFacade().getHistory().setHistoryCapacity(historyNum);
    }
}
