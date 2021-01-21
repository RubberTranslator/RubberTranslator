package com.rubbertranslator.mvp.presenter;

import com.rubbertranslator.enumtype.*;
import com.rubbertranslator.mvp.modules.TranslatorFacade;
import com.rubbertranslator.mvp.modules.filter.WindowsPlatformActiveWindowListenerThread;
import com.rubbertranslator.mvp.modules.textinput.clipboard.ClipboardListenerThread;
import com.rubbertranslator.mvp.modules.textinput.mousecopy.DragCopyThread;
import com.rubbertranslator.mvp.view.IView;

import java.awt.*;

/**
 * presenter
 */
public abstract class ModelPresenter<View extends IView> extends BasePresenter<View> {
    // 所有mode层class
    protected ClipboardListenerThread clipboardListenerThread;
    protected DragCopyThread dragCopyThread;
    protected WindowsPlatformActiveWindowListenerThread activeWindowListenerThread;
    protected TranslatorFacade translatorFacade;

    public void setClipboardListenerThread(ClipboardListenerThread clipboardListenerThread) {
        this.clipboardListenerThread = clipboardListenerThread;
    }

    public void setDragCopyThread(DragCopyThread dragCopyThread) {
        this.dragCopyThread = dragCopyThread;
    }

    public void setActiveWindowListenerThread(WindowsPlatformActiveWindowListenerThread activeWindowListenerThread) {
        this.activeWindowListenerThread = activeWindowListenerThread;
    }

    public void setTranslatorFacade(TranslatorFacade translatorFacade) {
        this.translatorFacade = translatorFacade;
    }

    public void setTranslatorType(TranslatorType type){
        if(translatorFacade == null) throw new NullPointerException("inject facade first");
    }

    public void switchScene(SceneType sceneType){
        if (view == null) throw new NullPointerException("inject scene first");
    }

    public void setKeepTop(boolean isKeep) {
        if (configManger == null || view == null) throw new NullPointerException("inject facade or view first");
    }

    public void translate(String originText) {
        if (translatorFacade == null || view == null) throw new NullPointerException("inject facade or view first");
    }

    public void translate(Image image) {
        if (translatorFacade == null || view == null) throw new NullPointerException("inject facade or view first");
    }


    public void clipboardSwitch(boolean isOpen) {
        if (clipboardListenerThread == null) throw new NullPointerException("inject clpTread first");
        clipboardListenerThread.setRun(isOpen);
        configManger.getSystemConfiguration().setOpenClipboardListener(isOpen);
    }

    public void dragCopySwitch(boolean isOpen) {
        if (dragCopyThread == null) throw new NullPointerException("inject dragCopyThread first");
        dragCopyThread.setRun(isOpen);
        configManger.getSystemConfiguration().setDragCopy(isOpen);
    }

    public void incrementalCopySwitch(boolean isOpen){
        if(translatorFacade == null) throw new NullPointerException("inject facade first");
        translatorFacade.getTextPreProcessor().setIncrementalCopy(isOpen);
        configManger.getSystemConfiguration().setIncrementalCopy(isOpen);
    }

    public void setTranslatorLanguage(boolean isSrc, Language language){
        if(configManger == null || translatorFacade == null) throw new NullPointerException("inject vars first");
    }

    public void setTextCursorPos(TextAreaCursorPos pos){
        if(configManger == null) throw new NullPointerException("inject vars first");
    }

    public void setHistoryEntry(HistoryEntryIndex index){
        if(translatorFacade == null) throw new NullPointerException("inject facade first");
    }

    public void clearText(){
        if(translatorFacade == null || view == null) throw new NullPointerException("inject vars first");
    }

    public void autoCopySwitch(boolean isOpen){
        if(translatorFacade == null) throw new NullPointerException("inject facade first");
    }

    public void autoPasteSwitch(boolean isOpen){
        if(translatorFacade == null) throw new NullPointerException("inject facade first");
    }

    public void textFormatSwitch(boolean isOpen){
        if(translatorFacade == null) throw new NullPointerException("inject facade first");
    }

    public void copyOriginText(){
        if(clipboardListenerThread == null || translatorFacade == null) throw new NullPointerException("inject vars first");
    }

    public void copyTranslatedText(){
        if(clipboardListenerThread == null || translatorFacade == null) throw new NullPointerException("inject vars first");
    }

}
