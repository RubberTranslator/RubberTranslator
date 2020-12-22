package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.entity.WordsPair;
import com.rubbertranslator.mvp.presenter.ModelPresenter;
import com.rubbertranslator.mvp.view.controller.IWordsReplacerView;

import java.util.Set;

public class WordsReplacerPresenter extends ModelPresenter<IWordsReplacerView> {
    public void apply(Set<WordsPair> set){
        configManger.getSystemConfiguration().setWordsPairs(set);
        translatorFacade.getTextPostProcessor().getReplacer().setWordsPairs(set);
        // update view
        view.apply();
    }
}
