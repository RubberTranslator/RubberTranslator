package com.rubbertranslator.modules.system.proxy;

import com.rubbertranslator.modules.system.SystemConfiguration;
import com.rubbertranslator.modules.system.SystemResourceManager;

import java.util.List;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/11 16:03
 */
public class ProcessFilterStaticConfig extends SystemConfiguration.ProcessFilterConfig {
    private SystemConfiguration.ProcessFilterConfig processFilterConfig;


    public SystemConfiguration.ProcessFilterConfig getProcessFilterConfig() {
        return processFilterConfig;
    }

    @Override
    public Boolean isOpenProcessFilter() {
        return processFilterConfig.isOpenProcessFilter();
    }


    @Override
    public List<String> getProcessList() {
        return processFilterConfig.getProcessList();
    }

    public ProcessFilterStaticConfig(SystemConfiguration.ProcessFilterConfig processFilterConfig) {
        this.processFilterConfig = processFilterConfig;
    }

    public void setOpenProcessFilter(Boolean openProcessFilter) {
        processFilterConfig.setOpenProcessFilter(openProcessFilter);
        SystemResourceManager.getFacade().getProcessFilter().setOpen(openProcessFilter);
    }
    public void setProcessList(List<String> processList) {
        processFilterConfig.setProcessList(processList);
        SystemResourceManager.getFacade().getProcessFilter().setFilterList(processList);
    }
}
