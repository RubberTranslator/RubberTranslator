package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.mvp.modules.history.HistoryEntry;
import com.rubbertranslator.mvp.view.controller.IFocusView;

public class FocusViewPresenter extends SingleTranslatePresenter {

    public void switchTranslateMode() {
        if(view == null) throw new NullPointerException();
        HistoryEntry entry = translatorFacade.getHistory().current();
        ((IFocusView)view).switchTranslateMode(entry);
    }
}
