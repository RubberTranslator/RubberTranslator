package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.entity.ApiInfo;
import com.rubbertranslator.enumtype.Language;
import com.rubbertranslator.enumtype.TextAreaCursorPos;
import com.rubbertranslator.enumtype.TranslatorType;
import com.rubbertranslator.mvp.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.mvp.modules.translate.AbstractTranslator;
import com.rubbertranslator.mvp.view.controller.ISingleTranslateView;

public class MainViewPresenter extends SingleTranslatePresenter<ISingleTranslateView> {

    @Override
    public void setTranslatorLanguage(boolean isSrc, Language language) {
        super.setTranslatorLanguage(isSrc, language);
        if (isSrc) {
            configManger.getSystemConfiguration().setSourceLanguage(language);
            translatorFacade.getTranslatorFactory().setSourceLanguage(language);
        } else {
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

    public void setOcrApi(ApiInfo apiInfo) {
        OCRUtils.setApiKey(apiInfo.getApiKey());
        OCRUtils.setSecretKey(apiInfo.getSecretKey());
        configManger.getSystemConfiguration().setBaiduOcrApiKey(apiInfo.getApiKey());
        configManger.getSystemConfiguration().setBaiduOcrSecretKey(apiInfo.getSecretKey());
    }


    public void setBaiduTranslatorApi(ApiInfo apiInfo) {
        AbstractTranslator translator = translatorFacade.getTranslatorFactory().getTranslator(TranslatorType.BAIDU);
        if (translator != null) {
            translator.setAppKey(apiInfo.getApiKey());
            translator.setSecretKey(apiInfo.getSecretKey());
        }
        configManger.getSystemConfiguration().setBaiduTranslatorApiKey(apiInfo.getApiKey());
        configManger.getSystemConfiguration().setBaiduTranslatorSecretKey(apiInfo.getSecretKey());
    }


    public void setYoudaoTranslatorApi(ApiInfo apiInfo) {
        AbstractTranslator translator = translatorFacade.getTranslatorFactory().getTranslator(TranslatorType.YOUDAO);
        if (translator != null) {
            translator.setAppKey(apiInfo.getApiKey());
            translator.setSecretKey(apiInfo.getSecretKey());
        }
        configManger.getSystemConfiguration().setYouDaoTranslatorApiKey(apiInfo.getApiKey());
        configManger.getSystemConfiguration().setYouDaoTranslatorSecretKey(apiInfo.getSecretKey());
    }
}
