package com.rubbertranslator.mvp.presenter;

import com.rubbertranslator.mvp.view.IView;
import com.rubbertranslator.system.SystemConfigurationManager;

import java.util.Timer;
import java.util.TimerTask;


public abstract class BasePresenter<View extends IView> {
    protected View view;

    protected SystemConfigurationManager configManger;

    public void setView(View view){
        if(configManger == null) throw new NullPointerException("config first");
        this.view = view;
    }

    public void initView(){
        if(view == null) throw new NullPointerException("Inject view first");
        view.initViews(configManger.getSystemConfiguration());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                view.delayInitViews();
            }
        },500);
    }

    public void setConfigManger(SystemConfigurationManager configManger) {
        this.configManger = configManger;
    }
}
