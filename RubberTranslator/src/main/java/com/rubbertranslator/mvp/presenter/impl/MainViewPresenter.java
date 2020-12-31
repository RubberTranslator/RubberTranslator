package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.enumtype.Language;
import com.rubbertranslator.enumtype.TextAreaCursorPos;

public class MainViewPresenter extends SingleTranslatePresenter {

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


    @Override
    public void setTextCursorPos(TextAreaCursorPos pos) {
        super.setTextCursorPos(pos);
        configManger.getSystemConfiguration().setTextAreaCursorPos(pos);
        view.setTextAreaCursorPos(pos);
    }
}
