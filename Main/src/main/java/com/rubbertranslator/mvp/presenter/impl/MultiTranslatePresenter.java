package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.enumtype.TranslatorType;
import com.rubbertranslator.mvp.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.mvp.presenter.ModelPresenter;
import com.rubbertranslator.mvp.view.controller.IMultiTranslateView;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MultiTranslatePresenter extends ModelPresenter<IMultiTranslateView> {

    private boolean oldAutoPaste = false;

    private boolean oldAutoCopy = false;


    @Override
    public void switchScene(SceneType sceneType) {
        super.switchScene(sceneType);
        view.destroy();
        view.switchScene(sceneType);
    }

    public void closeAndSaveAutoCopyPaste() {
        oldAutoCopy = configManger.getSystemConfiguration().isAutoCopy();
        oldAutoPaste = configManger.getSystemConfiguration().isAutoPaste();
        configManger.getSystemConfiguration().setAutoCopy(false);
        configManger.getSystemConfiguration().setAutoPaste(false);
        translatorFacade.getAfterProcessor().setAutoCopy(false);
        translatorFacade.getAfterProcessor().setAutoPaste(false);
    }

    public void restoreAutoCopyPasteConfig() {
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
        if (configManger.getSystemConfiguration().isAutoCopy()) {
            clipboardListenerThread.ignoreThisTime();
        }
        // 处理
        view.translateStart();
        AtomicInteger count = new AtomicInteger();
        translatorFacade.multiTranslate(originText, (pair -> {
            if (pair != null) {
                view.setTranslateResult(pair.getFirst(), pair.getSecond());
            }
            // 翻译完成，重新开启输入
            int value = count.incrementAndGet();
            if (value == TranslatorType.values().length) {
                // end
                view.translateEnd();
            }
        }));
    }

    @Override
    public void translate(Image image) {
        super.translate(image);
        // 如果开启了自动复制，那么需要跳过下一次复制
        if (configManger.getSystemConfiguration().isAutoCopy()) {
            clipboardListenerThread.ignoreThisTime();
        }
        // 处理
        view.translateStart();
        String originText;
        boolean error = false;
        try {
            originText = OCRUtils.ocr(image);
            if (originText == null) {
                error = true;
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ocr识别结果:" + originText);
                AtomicInteger count = new AtomicInteger();
                translatorFacade.multiTranslate(originText, (pair -> {
                    if (pair != null) {
                        view.setTranslateResult(pair.getFirst(), pair.getSecond());
                    }
                    // 翻译完成，重新开启输入
                    int value = count.incrementAndGet();
                    if (value == TranslatorType.values().length) {
                        // end
                        view.translateEnd();
                    }
                }));
            }
        } catch (IOException e) {
            error = true;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "ocr识别错误", e);
        }
        if (error) {
            view.setTranslateResult(TranslatorType.GOOGLE, "OCR翻译失败，请检查API配置");
            view.translateEnd();
        }
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
        translatorFacade.getTextPostProcessor().getIndentProcessor().setAutoIndent(isOpen);
        configManger.getSystemConfiguration().setTryFormat(isOpen);
    }

}
