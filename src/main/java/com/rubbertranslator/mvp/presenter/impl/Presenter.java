package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.enumtype.HistoryEntryIndex;
import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.event.CopyOriginOrTranslationEvent;
import com.rubbertranslator.modules.history.HistoryEntry;
import com.rubbertranslator.modules.textinput.mousecopy.copymethods.CopyRobot;
import com.rubbertranslator.modules.translate.TranslatorType;
import com.rubbertranslator.mvp.presenter.ModelPresenter;
import org.greenrobot.eventbus.EventBus;

public class Presenter extends ModelPresenter {

    @Override
    public void setTranslatorType(TranslatorType type) {
        super.setTranslatorType(type);
        translatorFacade.getTranslatorFactory().setEngineType(type);
    }

    @Override
    public void switchScene(SceneType sceneType) {
        super.switchScene(sceneType);
        scene.switchScene(sceneType);
    }

    @Override
    public void translate(String originText) {
        super.translate(originText);
        if(configManger.getSystemConfiguration().isAutoCopy()){
            EventBus.getDefault().post(new CopyOriginOrTranslationEvent());
        }

        scene.translateStart();
        translatorFacade.process(originText, (stringPair -> {
            scene.setText(stringPair.getFirst(), stringPair.getSecond());
            // end
            scene.translateEnd();
        }));

    }


    @Override
    public void clipboardSwitch(boolean isOpen) {
        super.clipboardSwitch(isOpen);
        clipboardListenerThread.setRun(isOpen);
        configManger.getSystemConfiguration().setOpenClipboardListener(isOpen);
    }

    @Override
    public void dragCopySwitch(boolean isOpen) {
        super.dragCopySwitch(isOpen);
        dragCopyThread.setRun(isOpen);
        configManger.getSystemConfiguration().setDragCopy(isOpen);
    }

    @Override
    public void incrementalCopySwitch(boolean isOpen) {
        super.incrementalCopySwitch(isOpen);
        translatorFacade.getTextPreProcessor().setIncrementalCopy(isOpen);
        configManger.getSystemConfiguration().setIncrementalCopy(isOpen);
    }

    @Override
    public void setHistoryEntry(HistoryEntryIndex index) {
        super.setHistoryEntry(index);
        HistoryEntry entry;
        switch(index)
        {
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
        scene.setText(entry.getOrigin(),entry.getTranslation());
    }

    @Override
    public void setKeepTop(boolean isKeep) {
        super.setKeepTop(isKeep);
        scene.setKeepTop(isKeep);
        configManger.getSystemConfiguration().setKeepTop(isKeep);
    }

    @Override
    public void clearText() {
        super.clearText();
        scene.setText("","");
        translatorFacade.getTextPreProcessor().cleanBuffer();
    }

    @Override
    public void autoCopySwitch(boolean isOpen) {
        super.autoCopySwitch(isOpen);
        scene.autoCopy(isOpen);
        if(!isOpen){
            translatorFacade.getAfterProcessor().setAutoPaste(false);
        }
        translatorFacade.getAfterProcessor().setAutoCopy(isOpen);
        configManger.getSystemConfiguration().setAutoCopy(isOpen);
    }

    @Override
    public void autoPasteSwitch(boolean isOpen) {
        super.autoPasteSwitch(isOpen);
        scene.autoPaste(isOpen);
        if(isOpen){
           translatorFacade.getAfterProcessor().setAutoCopy(true);
        }
        translatorFacade.getAfterProcessor().setAutoPaste(isOpen);
        configManger.getSystemConfiguration().setAutoPaste(isOpen);
    }

    @Override
    public void textFormatSwitch(boolean isOpen) {
        super.textFormatSwitch(isOpen);
        translatorFacade.getTextPreProcessor().setTryToFormat(isOpen);
        configManger.getSystemConfiguration().setTryFormat(isOpen);
    }

    @Override
    public void copyOriginText() {
        super.copyOriginText();
        HistoryEntry current = translatorFacade.getHistory().current();
        copyText(current.getOrigin());
    }

    private void copyText(String text){
        // 通知clipboard，下一次复制，不要翻译
        EventBus.getDefault().post(new CopyOriginOrTranslationEvent());
        CopyRobot.getInstance().copyText(text);
    }

    @Override
    public void copyTranslatedText() {
        super.copyTranslatedText();
        HistoryEntry current = translatorFacade.getHistory().current();
        copyText(current.getTranslation());
    }
}
