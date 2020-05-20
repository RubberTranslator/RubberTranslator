package com.rubbertranslator.controller;

import com.rubbertranslator.App;
import com.rubbertranslator.event.ClipboardContentInputEvent;
import com.rubbertranslator.event.TranslateCompleteEvent;
import com.rubbertranslator.event.TranslatorFacadeEvent;
import com.rubbertranslator.modules.history.HistoryEntry;
import com.rubbertranslator.modules.textinput.mousecopy.copymethods.CopyRobot;
import com.rubbertranslator.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.modules.translate.TranslatorType;
import com.rubbertranslator.system.SystemConfiguration;
import com.rubbertranslator.system.SystemResourceManager;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/9 21:51
 */
public class FocusModeController implements EventHandler<ActionEvent> {

    @FXML
    private VBox rootPane;

    @FXML
    private TextArea textArea;

    @FXML // 退回到主界面
    private Button backBt;
    @FXML  // 翻译按钮
    private Button translateBt;

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

    @FXML // 文本格式化
    private ToggleButton textFormatMenu;

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
        EventBus.getDefault().register(this);
    }

    /**
     * 回显
     */
    private void initViewsDisplay(){
        SystemConfiguration configurationProxy = SystemResourceManager.getConfigurationProxy();
        // 样式加载
        try{
            // 回显
            String path = configurationProxy.getUiConfig().getStyleCssPath();
            if (path != null) {
                File file = new File(path);
                if (file.exists()) {
                    rootPane.getStylesheets().setAll(file.toURI().toURL().toString());
                }
            }
        }catch (MalformedURLException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,e.getLocalizedMessage(),e);
        }


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
        // 文本格式化
        textFormatMenu.setSelected(configurationProxy.getTextProcessConfig().getTextPreProcessConfig().isTryToFormat());
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
        textFormatMenu.setOnAction(this);
    }

    private void onTranslatorTypeChanged(ObservableValue<? extends Toggle> observableValue, Toggle oldValue, Toggle newValue){
        SystemConfiguration.TranslatorConfig translatorConfig = SystemResourceManager.getConfigurationProxy().getTranslatorConfig();
        if (newValue == googleTranslator) {
            translatorConfig.setCurrentTranslator(TranslatorType.GOOGLE);
        } else if (newValue == baiduTranslator) {
            translatorConfig.setCurrentTranslator(TranslatorType.BAIDU);
        } else if (newValue == youdaoTranslator) {
            translatorConfig.setCurrentTranslator(TranslatorType.YOUDAO);
        }else{
            // 走到这个分支，说明用户点击了当前已经选中的按钮
            oldValue.setSelected(true);
        }
    }

    public void switchToMainController(){
        try {
            App.setRoot(ControllerConstant.MAIN_CONTROLLER_FXML);
            EventBus.getDefault().unregister(this);
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"切换主界面失败",e);
        }
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if(source == backBt){
            switchToMainController();
        }else if(source == clearBt){
            updateTextArea("");
            SystemResourceManager.getFacade().clear();
        } else if(source == keepStageTopBt){
            App.setKeepTop(keepStageTopBt.isSelected());
        }else if(source == incrementalCopyMenu){
             SystemResourceManager.getConfigurationProxy().getTextProcessConfig().getTextPreProcessConfig().setIncrementalCopy(incrementalCopyMenu.isSelected());
        }else if(source == preHistoryBt){
            HistoryEntry previous = SystemResourceManager.getFacade().getHistory().previous();
            if(previous!=null){
                updateTextArea(previous.getTranslation());
            }
        }else if(source == nextHistoryBt){
            HistoryEntry next = SystemResourceManager.getFacade().getHistory().next();
            if(next != null){
                updateTextArea(next.getTranslation());
            }
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
        }else if(source == textFormatMenu){
            SystemResourceManager.getConfigurationProxy().getTextProcessConfig().getTextPreProcessConfig().setTryToFormat(textFormatMenu.isSelected());
        }
        else if(source == copyOriginBt){
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
        textArea.setText(translation);
    }

    @FXML
    public void onBtnTranslateClick() {
        String originText = textArea.getText();
        processTranslate(originText);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void translatorFacadeEvent(TranslatorFacadeEvent event) {
        if(event == null) return;
        Platform.runLater(()->{
            if(event.isProcessStart()){ // 处理开始
//                translateBt.setText("翻译中");
                translateBt.setDisable(true);
            }else{      // 处理结束
                translateBt.setText("翻译");
                translateBt.setDisable(false);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onClipboardContentInput(ClipboardContentInputEvent event){
        if (event == null) return;
        if(event.isTextType()){
            processTranslate(event.getText());
        }else{
            try {
                String text = OCRUtils.ocr(event.getImage());
                if (text != null) {
                    processTranslate(text);
                }
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "ocr识别错误", e);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onComplete(TranslateCompleteEvent event) {
        if (event == null) return;
        // 不管从哪里会回调，回到UI线程
        Platform.runLater(() -> {
            updateTextArea(event.getTranslation());
        });
    }
}
