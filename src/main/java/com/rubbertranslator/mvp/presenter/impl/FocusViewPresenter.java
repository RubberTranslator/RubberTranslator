package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.mvp.modules.history.HistoryEntry;
import com.rubbertranslator.mvp.view.controller.IFocusView;

public class FocusViewPresenter extends Presenter{
    public void autoHideWindow(boolean hide){
        if(configManger == null) throw new NullPointerException();
        configManger.getSystemConfiguration().setAutoHide(hide);
    }

    public void switchBetweenOriginAndTranslatedText() {
        if(scene == null) throw new NullPointerException();
        HistoryEntry entry = translatorFacade.getHistory().current();
        ((IFocusView)scene).switchBetweenOriginAndTranslatedText(entry);
    }
}
