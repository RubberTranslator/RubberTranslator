package com.rubbertranslator.mvp.view.controller;

import com.rubbertranslator.mvp.modules.history.HistoryEntry;

/**
 * focusView
 */
public interface IFocusView extends ISceneView {
     void showWindow();

     void hideWindow();

     void switchBetweenOriginAndTranslatedText(HistoryEntry entry);
}
