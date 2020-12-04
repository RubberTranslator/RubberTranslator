package com.rubbertranslator.mvp.view.controller;

import com.rubbertranslator.mvp.modules.history.HistoryEntry;

/**
 * focusView
 */
public interface IFocusView extends ISceneView {
     void switchBetweenOriginAndTranslatedText(HistoryEntry entry);
}
