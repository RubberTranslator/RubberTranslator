package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.enumtype.Language;

public class MainViewPresenter extends Presenter{
    @Override
    public void setTranslatorLanguage(boolean isSrc, Language language) {
        super.setTranslatorLanguage(isSrc, language);
        if(isSrc){
            configManger.getSystemConfiguration().setSourceLanguage(language);
            translatorFacade.getTranslatorFactory().setSourceLanguage(language);
        }else{
            configManger.getSystemConfiguration().setDestLanguage(language);
            translatorFacade.getTranslatorFactory().setDestLanguage(language);
        }

    }
}
