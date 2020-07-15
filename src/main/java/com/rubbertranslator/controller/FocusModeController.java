package com.rubbertranslator.controller;

import com.rubbertranslator.App;
import com.rubbertranslator.event.*;
import com.rubbertranslator.modules.config.SystemConfiguration;
import com.rubbertranslator.modules.history.HistoryEntry;
import com.rubbertranslator.modules.textinput.mousecopy.copymethods.CopyRobot;
import com.rubbertranslator.modules.textinput.ocr.OCRUtils;
import com.rubbertranslator.modules.translate.TranslatorType;
import com.rubbertranslator.system.SystemResourceManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/9 21:51
 */
public class FocusModeController implements Initializable, EventHandler<ActionEvent>,ChangeListener<Boolean> {

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

    @FXML   // 显示文本： 显示原文或者显示译文
    private Button displayTextBt;

    @FXML   // 复制原文
    private Button copyOriginBt;
    @FXML   // 复制译文
    private Button copyTranslationBt;

    @FXML   // 隐匿模式开关
    private ToggleButton autoHideBt;

    // 辅助变量，记录鼠标点击位置
    private Point currentMouseClickPos;
    // 屏幕缩放率
    private double screenScaleRatio;


    /**
     * 组件初始化完成后，会调用这个方法
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initViewsDisplay();
        initClickEvents();
        initListeners();
        initParams();
    }

    /**
     * 初始化参数
     */
    private void initParams(){
        screenScaleRatio = Toolkit.getDefaultToolkit().getScreenResolution()/96.0;
    }

    /**
     * 注册监听
     */
    private void initListeners() {
        // 注册监听
        EventBus.getDefault().register(this);
        // 延迟注册焦点监听， 直接注册会报NullPointer
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                rootPane.getScene().getWindow().focusedProperty().addListener(FocusModeController.this);
            }
        },500);
    }

    /**
     * 回显
     */
    private void initViewsDisplay() {
        SystemConfiguration configurationProxy = SystemResourceManager.getConfigurationProxy();
        // 样式加载
        try {
            // 回显
            String path = configurationProxy.getUiConfig().getStyleCssPath();
            if (path != null) {
                File file = new File(path);
                if (file.exists()) {
                    rootPane.getStylesheets().setAll(file.toURI().toURL().toString());
                }
            }
        } catch (MalformedURLException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, e.getLocalizedMessage(), e);
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
        translateBt.setOnAction(this);
        displayTextBt.setOnAction(this);
        autoHideBt.setOnAction(this);
    }

    private void onTranslatorTypeChanged(ObservableValue<? extends Toggle> observableValue, Toggle oldValue, Toggle newValue) {
        SystemConfiguration.TranslatorConfig translatorConfig = SystemResourceManager.getConfigurationProxy().getTranslatorConfig();
        if (newValue == googleTranslator) {
            translatorConfig.setCurrentTranslator(TranslatorType.GOOGLE);
        } else if (newValue == baiduTranslator) {
            translatorConfig.setCurrentTranslator(TranslatorType.BAIDU);
        } else if (newValue == youdaoTranslator) {
            translatorConfig.setCurrentTranslator(TranslatorType.YOUDAO);
        } else {
            // 走到这个分支，说明用户点击了当前已经选中的按钮
            oldValue.setSelected(true);
        }
    }

    public void switchToMainController() {
        try {
            App.setRoot(ControllerFxmlPath.MAIN_CONTROLLER_FXML);
            EventBus.getDefault().unregister(this);
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "切换主界面失败", e);
        }
    }

    @Override
    public void handle(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (source == backBt) {   // 返回
            switchToMainController();
        } else if (source == clearBt) {    // 清空文本
            clearText();
        } else if (source == keepStageTopBt) {    // 置顶
            keepTop(keepStageTopBt.isSelected());
        } else if (source == incrementalCopyMenu) {    // 增量复制
            incrementCopy(incrementalCopyMenu.isSelected());
        } else if (source == preHistoryBt) {       // 前一个历史
            previousHistory();
        } else if (source == nextHistoryBt) {  // 后一个历史
            nextHistory();
        } else if (source == autoCopyMenu) {   // 自动复制
            autoCopy(autoCopyMenu.isSelected());
        } else if (source == autoPasteMenu) {  // 自动粘贴
            autoPaste(autoPasteMenu.isSelected());
        } else if (source == textFormatMenu) { // 文本格式化
            textFormat(textFormatMenu.isSelected());
        } else if (source == copyOriginBt) {    // 复制原文
            copyOriginText();
        } else if (source == copyTranslationBt) {  // 复制译文
            copyTranslationText();
        } else if (source == clipboardListenerMenu) { // 剪切板
            clipboardListenerSwitch(clipboardListenerMenu.isSelected());
        } else if (source == dragCopyMenu) {   // 拖拽复制
            dragCopyListenerSwitch(dragCopyMenu.isSelected());
        } else if (source == translateBt) {    // 翻译
            triggerTranslate();
        } else if (source == displayTextBt) {    // 文本显示
            displayText();
        } else if(source == autoHideBt){ // 隐匿模式
        }
    }


    /**
     * 隐匿模式：hide window
     */
    private void hideWindow(){
        if(autoHideBt.isSelected())
        {
            Stage window  = (Stage) rootPane.getScene().getWindow();
            // 回到ui线程
            Platform.runLater(()->{
                if(window.isShowing())
                {
                    window.hide();
                    Logger.getLogger(this.getClass().getName()).info("window hide");
                }
            });
        }
    }

    /**
     * 隐匿模式：show window
     * 跟随鼠标显示，同时window不能超过界面宽度
     */
    private void showWindow(){
        if(autoHideBt.isSelected())
        {
            Stage window  = (Stage) rootPane.getScene().getWindow();
            // 回到ui线程
            Platform.runLater(()->{
                if(currentMouseClickPos!=null)
                {
                    double mouseX = currentMouseClickPos.getX()/screenScaleRatio;
                    double mouseY= currentMouseClickPos.getY()/screenScaleRatio;
                    double width = window.getWidth();
                    double height = window.getHeight();
                    double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
                    double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
                    if(mouseX + width > screenWidth){
                        mouseX = screenWidth - width - 20;
                    }
                    if(mouseY + height > screenHeight){
                        mouseY = screenHeight - height - 20;
                    }

                    rootPane.getScene().getWindow().setX(mouseX);
                    rootPane.getScene().getWindow().setY(mouseY);

                    if(!window.isShowing())
                    {
                        window.show();
                        Logger.getLogger(this.getClass().getName()).info("window show");
                    }
                }
            });
        }
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean focus) {
        if(!focus){
           if(autoHideBt.isSelected()){
               hideWindow();
           }
        }
    }

    private void triggerTranslate(){
        String originText = textArea.getText();
        processTranslate(originText);
    }

    private void clipboardListenerSwitch(boolean open) {
        SystemResourceManager.getConfigurationProxy().getTextInputConfig().setOpenClipboardListener(open);
    }

    private void dragCopyListenerSwitch(boolean open) {
        SystemResourceManager.getConfigurationProxy().getTextInputConfig().setDragCopy(open);
    }

    private void clearText() {
        updateTextArea("");
        SystemResourceManager.getFacade().clear();
    }

    private void keepTop(boolean isKeep) {
        App.setKeepTop(isKeep);
        SystemResourceManager.getConfigurationProxy().getUiConfig().setKeepTop(keepStageTopBt.isSelected());
    }

    private void incrementCopy(boolean openIncrementCopy) {
        SystemResourceManager.getConfigurationProxy().getTextProcessConfig().getTextPreProcessConfig().setIncrementalCopy(openIncrementCopy);
    }

    private void previousHistory() {
        HistoryEntry previous = SystemResourceManager.getFacade().getHistory().previous();
        if (previous != null) {
            updateTextArea(previous.getTranslation());
        }
    }

    private void nextHistory() {
        HistoryEntry next = SystemResourceManager.getFacade().getHistory().next();
        if (next != null) {
            updateTextArea(next.getTranslation());
        }
    }

    private void autoCopy(boolean open) {
        if (!autoCopyMenu.isSelected()) {
            autoPasteMenu.setSelected(false);
            SystemResourceManager.getConfigurationProxy().getAfterProcessorConfig().setAutoPaste(false);
        }
        SystemResourceManager.getConfigurationProxy().getAfterProcessorConfig().setAutoCopy(open);
    }

    private void autoPaste(boolean open) {
        // 自动粘贴依赖于自动复制
        if (autoPasteMenu.isSelected()) {
            autoCopyMenu.setSelected(true);
            SystemResourceManager.getConfigurationProxy().getAfterProcessorConfig().setAutoCopy(true);
        }
        SystemResourceManager.getConfigurationProxy().getAfterProcessorConfig().setAutoPaste(open);
    }

    private void textFormat(boolean open) {
        SystemResourceManager.getConfigurationProxy().getTextProcessConfig().getTextPreProcessConfig().setTryToFormat(open);
    }

    private void displayText() {
        HistoryEntry currentHistoryEntry = SystemResourceManager.getFacade().getHistory().current();
        if(currentHistoryEntry == null) return;

        final String showOrigin = "显示原文";
        final String showTranslation = "显示译文";
        final String currentState = displayTextBt.getText();

        if (showOrigin.equals(currentState)) { // 需要显示原文
            textArea.setText(currentHistoryEntry.getOrigin());
            // 下一个状态：显示译文
            displayTextBt.setText(showTranslation);
        } else if (showTranslation.equals(currentState)) {
            textArea.setText(currentHistoryEntry.getTranslation());
            // 下一个状态：显示原文
            displayTextBt.setText(showOrigin);
        }
    }


    private void copyOriginText() {
        EventBus.getDefault().post(new CopyOriginOrTranslationEvent());
        HistoryEntry current = SystemResourceManager.getFacade().getHistory().current();
        CopyRobot.getInstance().copyText(current.getOrigin());
    }

    private void copyTranslationText() {
        EventBus.getDefault().post(new CopyOriginOrTranslationEvent());
        HistoryEntry current = SystemResourceManager.getFacade().getHistory().current();
        CopyRobot.getInstance().copyText(current.getTranslation());
    }

    private void processTranslate(String text) {
        SystemResourceManager.getFacade().process(text);
    }

    private void updateTextArea(String translation) {
        textArea.setText(translation);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onTranslateStartOrEnd(TranslatorProcessEvent event) {
        if (event == null) return;
        final String showOrigin = "显示原文";
        Platform.runLater(() -> {
            if (event.isProcessStart()) { // 处理开始
//                translateBt.setText("翻译中");
                translateBt.setDisable(true);
                displayTextBt.setDisable(true);
            } else {      // 处理结束
                translateBt.setText("翻译");
                translateBt.setDisable(false);

                // displayBt reinitialize
                displayTextBt.setText(showOrigin);
                displayTextBt.setDisable(false);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onClipboardContentInput(ClipboardContentInputEvent event) {
        if (event == null) return;
        if (event.isTextType()) { // 文字类型
            processTranslate(event.getText());
        } else {                // 图片类型
            try {
                String text = OCRUtils.ocr(event.getImage());
                if (text != null) {
                    processTranslate(text);
                }
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "ocr识别错误", e);
            }
        }

        /*
          下面的代码for 隐匿模式
          隐匿模式下， 自动显示
         */
        // 是否需要显示
        if(autoHideBt.isSelected())
        {
            // 获取焦点
            rootPane.getScene().getWindow().requestFocus();
            // 展示
            showWindow();
        }
    }

    /**
     * 翻译完成时
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onTranslateComplete(TranslateCompleteEvent event) {
        if (event == null) return;
        // 回显
        Platform.runLater(() ->  updateTextArea(event.getTranslation()) );
    }

    /**
     * 鼠标点击时
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMouseClick(MouseClickPositionEvent event) {
        if (event == null) return;
        currentMouseClickPos = event.getPoint();
    }

}
