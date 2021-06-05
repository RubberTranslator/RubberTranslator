package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.enumtype.HistoryEntryIndex;
import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.enumtype.TranslatorType;
import com.rubbertranslator.mvp.modules.history.HistoryEntry;
import com.rubbertranslator.mvp.modules.textinput.mousecopy.copymethods.CopyRobot;
import com.rubbertranslator.mvp.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.mvp.presenter.ModelPresenter;
import com.rubbertranslator.mvp.view.controller.ISingleTranslateView;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingleTranslatePresenter<T extends ISingleTranslateView> extends ModelPresenter<T> {

    @Override
    public void setTranslatorType(TranslatorType type) {
        super.setTranslatorType(type);
        translatorFacade.getTranslatorFactory().setEngineType(type);
        configManger.getSystemConfiguration().setCurrentTranslator(type);
    }

    @Override
    public void switchScene(SceneType sceneType) {
        super.switchScene(sceneType);
        view.destroy();
        view.switchScene(sceneType);
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
        translatorFacade.singleTranslate(originText, (stringPair -> {
            if (stringPair != null) {
                view.setText(stringPair.getFirst(), stringPair.getSecond());
            }
            // end
            view.translateEnd();
            translateEndHook();
        }));
    }

    public void translateEndHook() {

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
        boolean error = false;
        try {
            String text = OCRUtils.ocr(image);
            if (text == null) {
                view.setText("OCR翻译失败，请检查API配置", "OCR翻译失败，请检查API配置");
                error = true;
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ocr识别结果:" + text);
                translatorFacade.singleTranslate(text, (stringPair -> {
                    if (stringPair != null) {
                        view.setText(stringPair.getFirst(), stringPair.getSecond());
                    }
                    // end
                    view.translateEnd();
                    translateEndHook();
                }));
            }
        } catch (IOException e) {
            error = true;
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "ocr识别错误", e);
        }
        if (error) {
            view.translateEnd();
        }
    }

    @Override
    public void setHistoryEntry(HistoryEntryIndex index) {
        super.setHistoryEntry(index);
        HistoryEntry entry;
        switch (index) {
            case PRE_HISTORY:
                entry = translatorFacade.getHistory().previous();
                break;
            case NEXT_HISTORY:
                entry = translatorFacade.getHistory().next();
                break;
            case CURRENT_HISTORY:
            default:
                entry = translatorFacade.getHistory().current();
                break;
        }
        view.setText(entry.getOrigin(), entry.getTranslation());
    }

    @Override
    public void setKeepTop(boolean isKeep) {
        super.setKeepTop(isKeep);
        view.setKeepTop(isKeep);
        configManger.getSystemConfiguration().setKeepTop(isKeep);
    }

    @Override
    public void clearText() {
        super.clearText();
        view.setText("", "");
        translatorFacade.getTextPreProcessor().cleanBuffer();
    }

    @Override
    public void autoCopySwitch(boolean isOpen) {
        super.autoCopySwitch(isOpen);
        view.autoCopy(isOpen);
        if (!isOpen) {
            translatorFacade.getAfterProcessor().setAutoPaste(false);
            configManger.getSystemConfiguration().setAutoPaste(false);
        }
        translatorFacade.getAfterProcessor().setAutoCopy(isOpen);
        configManger.getSystemConfiguration().setAutoCopy(isOpen);
    }

    @Override
    public void autoPasteSwitch(boolean isOpen) {
        super.autoPasteSwitch(isOpen);
        view.autoPaste(isOpen);
        if (isOpen) {
            translatorFacade.getAfterProcessor().setAutoCopy(true);
            configManger.getSystemConfiguration().setAutoCopy(true);
        }
        translatorFacade.getAfterProcessor().setAutoPaste(isOpen);
        configManger.getSystemConfiguration().setAutoPaste(isOpen);
    }

    @Override
    public void textFormatSwitch(boolean isOpen) {
        super.textFormatSwitch(isOpen);
        translatorFacade.getTextPreProcessor().setTryToFormat(isOpen);
        translatorFacade.getTextPostProcessor().getIndentProcessor().setAutoIndent(isOpen);
        configManger.getSystemConfiguration().setTryFormat(isOpen);
    }

    @Override
    public void copyOriginText() {
        super.copyOriginText();
        HistoryEntry current = translatorFacade.getHistory().current();
        copyText(current.getOrigin());
    }

    private void copyText(String text) {
        // 通知clipboard，下一次复制，不要翻译
        clipboardListenerThread.ignoreThisTime();
        CopyRobot.getInstance().copyText(text);
    }

    @Override
    public void copyTranslatedText() {
        super.copyTranslatedText();
        HistoryEntry current = translatorFacade.getHistory().current();
        copyText(current.getTranslation());
    }
}
