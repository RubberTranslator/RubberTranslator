package com.rubbertranslator.mvp.presenter;

import com.rubbertranslator.mvp.view.IView;
import com.rubbertranslator.system.SystemConfigurationManager;


public abstract class BasePresenter<View extends IView> {
    protected View view;

    protected SystemConfigurationManager configManger;

    public void setView(View view){
        if(configManger == null) throw new NullPointerException("config first");
        this.view = view;
        view.initViews(configManger.getSystemConfiguration());
    }

    public void setConfigManger(SystemConfigurationManager configManger) {
        this.configManger = configManger;
    }
}
