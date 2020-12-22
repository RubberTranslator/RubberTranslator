package com.rubbertranslator.mvp.view;

import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.system.SystemConfiguration;

public interface IView {
    /**
     * 初始化视图，回显
     */
    void initViews(SystemConfiguration configuration);
}
