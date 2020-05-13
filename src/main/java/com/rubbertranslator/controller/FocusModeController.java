package com.rubbertranslator.controller;

import com.rubbertranslator.App;
import com.rubbertranslator.modules.TranslatorFacade;
import com.rubbertranslator.modules.history.HistoryEntry;
import com.rubbertranslator.modules.system.SystemConfiguration;
import com.rubbertranslator.modules.system.SystemResourceManager;
import com.rubbertranslator.modules.textinput.TextInputListener;
import com.rubbertranslator.modules.textinput.mousecopy.copymethods.CopyRobot;
import com.rubbertranslator.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.modules.translate.TranslatorType;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/9 21:51
 */
public class FocusModeController implements EventHandler<ActionEvent>, TextInputListener, TranslatorFacade.TranslatorFacadeListener {

    @FXML
    private TextArea translationArea;

    @FXML // 退回到主界面
    private Button backBt;

    @FXML   // 翻译组
    private ToggleGroup translatorGroup;
    @FXML
    private ToggleButton googleTranslator;
    @FXML
    private ToggleButton baiduTranslator;
    @FXML
    private ToggleButton youdaoTranslator;

    @FXML // 增量复制
    private ToggleButton incrementalCopyMenu;

    @FXML   // 自动复制
    private ToggleButton autoCopyMenu;
    @FXML   // 自动粘贴
    private ToggleButton autoPasteMenu;


    @FXML // 监听剪切板
    private ToggleButton clipboardListenerMenu;
    @FXML
    private ToggleButton dragCopyMenu;

    @FXML // 历史记录
    private Button preHistoryBt;
    @FXML
    private Button nextHistoryBt;

    @FXML   // 保持置顶
    private ToggleButton keepStageTopBt;

    @FXML   // 清空
    private Button clearBt;

    @FXML
    private Button copyOriginBt;
    @FXML
    private Button copyTranslationBt;

    /**
     * 组件初始化完成后，会调用这个方法
     */
    @FXML
    public void initialize() {
        initViewsDisplay();
        initClickEvents();
        initListeners();
    }

    private void initListeners() {
        // 注册文本变化监听
        SystemResourceManager.getClipBoardListenerThread().setTextInputListener(this);
        // 注册翻译完成监听
        SystemResourceManager.getFacade().setFacadeListener(this);
    }

    /**
     * 回显
     */
    private void initViewsDisplay(){
        SystemConfiguration configurationProxy = SystemResourceManager.getConfigurationProxy();
        // 置顶
        keepStageTopBt.setSelected(configurationProxy.getUiConfig().isKeepTop());
        // 翻译引擎
        switch (configurationProxy.getTranslatorConfig().getCurrentTranslator()) {
            case GOOGLE:
                googleTranslator.setSelected(true);
                break;
            case BAIDU:
                baiduTranslator.setSelected(true);
                break;
            case YOUDAO:
                youdaoTranslator.setSelected(true);
                break;
        }
        // 增量
        incrementalCopyMenu.setSelected(configurationProxy.getTextProcessConfig().getTextPreProcessConfig().isIncrementalCopy());
        // 自动复制
        autoCopyMenu.setSelected(configurationProxy.getAfterProcessorConfig().isAutoCopy());
        // 自动粘贴
        autoPasteMenu.setSelected(configurationProxy.getAfterProcessorConfig().isAutoPaste());
        // 监听版
        clipboardListenerMenu.setSelected(configurationProxy.getTextInputConfig().isOpenClipboardListener());
        // 拖拽
        dragCopyMenu.setSelected(configurationProxy.getTextInputConfig().isDragCopy());
    }

    /**
     * 点击事件
     */
    private void initClickEvents() {
        backBt.setOnAction(this);
        translatorGroup.selectedToggleProperty().addListener(this::onTranslatorTypeChanged);
        incrementalCopyMenu.setOnAction(this);
        preHistoryBt.setOnAction(this);
        nextHistoryBt.setOnAction(this);
        clearBt.setOnAction(this);
        keepStageTopBt.setOnAction(this);
        autoCopyMenu.setOnAction(this);
        autoPasteMenu.setOnAction(this);
        copyOriginBt.setOnAction(this);
        copyTranslationBt.setOnAction(this);
        clipboardListenerMenu.setOnAction(this);
        dragCopyMenu.setOnAction(this);
    }

    private void onTranslatorTypeChanged(ObservableValue<? extends Toggle> observableValue, Toggle oldValue, Toggle newValue){
        // TODO: fxml中如何引用枚举？ 这里暂时采用硬编码
        SystemConfiguration.TranslatorConfig translatorConfig = SystemResourceManager.getConfigurationProxy().getTranslatorConfig();
        if (newValue == googleTranslator) {
            translatorConfig.setCurrentTranslator(TranslatorType.GOOGLE);
        } else if (newValue == baiduTranslator) {
            translatorConfig.setCurrentTranslator(TranslatorType.BAIDU);
        } else if (newValue == youdaoTranslator) {
            translatorConfig.setCurrentTranslator(TranslatorType.YOUDAO);
        }
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if(source == backBt){
            try {
                App.setRoot(ControllerConstant.MAIN_CONTROLLER_FXML);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(source == clearBt){
            updateTextArea("");
            SystemResourceManager.getFacade().clear();
        } else if(source == keepStageTopBt){
            App.setKeepTop(keepStageTopBt.isSelected());
        }else if(source == incrementalCopyMenu){
             SystemResourceManager.getConfigurationProxy().getTextProcessConfig().getTextPreProcessConfig().setIncrementalCopy(incrementalCopyMenu.isSelected());
        }else if(source == preHistoryBt){
            HistoryEntry previous = SystemResourceManager.getFacade().getHistory().previous();
            updateTextArea(previous.getTranslation());
        }else if(source == nextHistoryBt){
            HistoryEntry next = SystemResourceManager.getFacade().getHistory().next();
            updateTextArea(next.getTranslation());
        }else if(source == autoCopyMenu){
            if(!autoCopyMenu.isSelected()){
                autoPasteMenu.setSelected(false);
                SystemResourceManager.getConfigurationProxy().getAfterProcessorConfig().setAutoPaste(false);
            }
            SystemResourceManager.getConfigurationProxy().getAfterProcessorConfig().setAutoCopy(autoCopyMenu.isSelected());
        }else if(source == autoPasteMenu){
            // 自动粘贴依赖于自动复制
            if(autoPasteMenu.isSelected()){
                autoCopyMenu.setSelected(true);
                SystemResourceManager.getConfigurationProxy().getAfterProcessorConfig().setAutoCopy(true);
            }
            SystemResourceManager.getConfigurationProxy().getAfterProcessorConfig().setAutoPaste(autoPasteMenu.isSelected());
        }else if(source == copyOriginBt){
            copyOriginText();
        }else if(source == copyTranslationBt){
            copyTranslationText();
        } else if(source == clipboardListenerMenu){
            SystemResourceManager.getConfigurationProxy().getTextInputConfig().setOpenClipboardListener(clipboardListenerMenu.isSelected());
        }else if(source == dragCopyMenu){
            SystemResourceManager.getConfigurationProxy().getTextInputConfig().setDragCopy(dragCopyMenu.isSelected());
        }
    }

    private void copyOriginText(){
        HistoryEntry current = SystemResourceManager.getFacade().getHistory().current();
        CopyRobot.getInstance().copyText(current.getOrigin());
    }

    private void copyTranslationText(){
        HistoryEntry current = SystemResourceManager.getFacade().getHistory().current();
        CopyRobot.getInstance().copyText(current.getTranslation());
    }

    private void processTranslate(String text) {
        SystemResourceManager.getFacade().process(text);
    }

    private void updateTextArea(String translation){
        translationArea.setText(translation);
    }


    @Override
    public void onTextInput(String text) {
        processTranslate(text);
    }

    @Override
    public void onImageInput(Image image) {
        try {
            String text = OCRUtils.ocr(image);
            if (text != null) {
                onTextInput(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
        }
    }



    @Override
    public void onComplete(String origin, String translation) {
        // 不管从哪里会回调，回到UI线程
        Platform.runLater(() -> updateTextArea(translation));
    }
}
