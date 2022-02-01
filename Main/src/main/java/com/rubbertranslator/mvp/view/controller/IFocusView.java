package com.rubbertranslator.mvp.view.controller;

import com.rubbertranslator.mvp.modules.history.HistoryEntry;

/**
 * focusView
 */
public interface IFocusView extends ISingleTranslateView {
     void switchTranslateMode(HistoryEntry entry);
}
