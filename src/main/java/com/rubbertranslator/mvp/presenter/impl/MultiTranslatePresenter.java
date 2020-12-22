package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.mvp.presenter.ModelPresenter;
import com.rubbertranslator.mvp.view.controller.IMultiTranslateView;

public class MultiTranslatePresenter extends ModelPresenter<IMultiTranslateView> {

    @Override
    public void switchScene(SceneType sceneType) {
        super.switchScene(sceneType);
        view.switchScene(sceneType);
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
        // 关闭剪切板监听线程，翻译未完成前，不允许更多输入
        final boolean cptState = clipboardListenerThread.isRunning();
        clipboardListenerThread.setRun(false);
        // 如果开启了自动复制，那么需要跳过下一次复制
        if(configManger.getSystemConfiguration().isAutoCopy()){
            clipboardListenerThread.ignoreThisTime();
        }
        // 处理
        view.translateStart();
        translatorFacade.multiTranslate(originText, (pair -> {
            view.setTranslateResult(pair.getFirst(),pair.getSecond());
            // end
            view.translateEnd();
            // 翻译完成，重新开启输入
            clipboardListenerThread.setRun(cptState);
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
