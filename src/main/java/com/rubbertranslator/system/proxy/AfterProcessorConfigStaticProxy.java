package com.rubbertranslator.system.proxy;

import com.rubbertranslator.system.SystemConfiguration;
import com.rubbertranslator.system.SystemResourceManager;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/12 11:05
 */
public class AfterProcessorConfigStaticProxy extends SystemConfiguration.AfterProcessorConfig {

    private final SystemConfiguration.AfterProcessorConfig afterProcessorConfig;

    public SystemConfiguration.AfterProcessorConfig getAfterProcessorConfig() {
        return afterProcessorConfig;
    }

    public AfterProcessorConfigStaticProxy(SystemConfiguration.AfterProcessorConfig afterProcessorConfig) {
        this.afterProcessorConfig = afterProcessorConfig;
    }

    @Override
    public Boolean isAutoCopy() {
        return afterProcessorConfig.isAutoCopy();
    }

    @Override
    public void setAutoCopy(Boolean autoCopy) {
        afterProcessorConfig.setAutoCopy(autoCopy);
        SystemResourceManager.getFacade().getAfterProcessor().setAutoCopy(autoCopy);
    }

    @Override
    public Boolean isAutoPaste() {
        return afterProcessorConfig.isAutoPaste();
    }

    @Override
    public void setAutoPaste(Boolean autoPaste) {
        afterProcessorConfig.setAutoPaste(autoPaste);
        SystemResourceManager.getFacade().getAfterProcessor().setAutoPaste(autoPaste);
    }
}
