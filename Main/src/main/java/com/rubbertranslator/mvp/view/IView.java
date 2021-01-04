package com.rubbertranslator.mvp.view;

import com.rubbertranslator.system.SystemConfiguration;

public interface IView {
    /**
     * 初始化视图，回显
     */
    void initViews(SystemConfiguration configuration);


    /**
     * 需要延迟加载的views
     */
    default void delayInitViews(){}
}
