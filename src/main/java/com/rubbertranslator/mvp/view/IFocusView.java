package com.rubbertranslator.mvp.view;

import com.rubbertranslator.modules.history.HistoryEntry;

/**
 * focusView
 */
public interface IFocusView extends ISceneView {
     void showWindow();

     void hideWindow();

     void switchBetweenOriginAndTranslatedText(HistoryEntry entry);
}
