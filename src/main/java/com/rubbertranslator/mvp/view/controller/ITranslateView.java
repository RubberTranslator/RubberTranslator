package com.rubbertranslator.mvp.view.controller;

import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.mvp.view.IView;

public interface ITranslateView extends IView {
    default void setKeepTop(boolean isKeep){}

    /**
     * 切换场景
     * @param type
     */
    default void switchScene(SceneType type){}

    default void translateStart(){}

    default void translateEnd(){}
}
