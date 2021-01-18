package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.enumtype.TranslatorType;
import com.rubbertranslator.mvp.presenter.ModelPresenter;
import com.rubbertranslator.mvp.view.controller.IMultiTranslateView;

import java.util.concurrent.atomic.AtomicInteger;

public class MultiTranslatePresenter extends ModelPresenter<IMultiTranslateView> {

    private boolean oldAutoPaste = false;

    private boolean oldAutoCopy = false;


    @Override
    public void switchScene(SceneType sceneType) {
        super.switchScene(sceneType);
        view.switchScene(sceneType);
        view.destroy();
    }

    public void closeAndSaveAutoCopyPaste(){
        oldAutoCopy = configManger.getSystemConfiguration().isAutoCopy();
        oldAutoPaste = configManger.getSystemConfiguration().isAutoPaste();
        translatorFacade.getAfterProcessor().setAutoCopy(false);
        translatorFacade.getAfterProcessor().setAutoPaste(false);
    }

    public void restoreAutoCopyPasteConfig(){
        configManger.getSystemConfiguration().setAutoCopy(oldAutoCopy);
        configManger.getSystemConfiguration().setAutoPaste(oldAutoPaste);
        translatorFacade.getAfterProcessor().setAutoCopy(oldAutoCopy);
        translatorFacade.getAfterProcessor().setAutoPaste(oldAutoPaste);
    }

    @Override
    public void setKeepTop(boolean isKeep) {
        super.setKeepTop(isKeep);
        view.setKeepTop(isKeep);
        configManger.getSystemConfiguration().setKeepTop(isKeep);
    }

    @Override
    public void translate(String originText) {
        super.translate(originText);
        // 如果开启了自动复制，那么需要跳过下一次复制
        if(configManger.getSystemConfiguration().isAutoCopy()){
            clipboardListenerThread.ignoreThisTime();
        }
        // 处理
        view.translateStart();
        AtomicInteger count = new AtomicInteger();
        translatorFacade.multiTranslate(originText, (pair -> {
            view.setTranslateResult(pair.getFirst(),pair.getSecond());
            // 翻译完成，重新开启输入
            int value = count.incrementAndGet();
            if(value == TranslatorType.values().length){
                // end
                view.translateEnd();
            }
        }));
    }


    @Override
    public void clearText() {
        super.clearText();
        view.clearAllText();
        translatorFacade.getTextPreProcessor().cleanBuffer();
    }

    @Override
    public void textFormatSwitch(boolean isOpen) {
        super.textFormatSwitch(isOpen);
        translatorFacade.getTextPreProcessor().setTryToFormat(isOpen);
        configManger.getSystemConfiguration().setTryFormat(isOpen);
    }

}
