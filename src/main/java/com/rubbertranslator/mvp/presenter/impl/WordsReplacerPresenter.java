package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.entity.WordsPair;
import com.rubbertranslator.mvp.presenter.ConfigPresenter;
import com.rubbertranslator.mvp.view.controller.IWordsReplacerView;

import java.util.Set;

public class WordsReplacerPresenter extends ConfigPresenter {
    public void apply(Set<WordsPair> set){
        configManger.getSystemConfiguration().setWordsPairs(set);
        ((IWordsReplacerView)scene).apply();
    }
}
