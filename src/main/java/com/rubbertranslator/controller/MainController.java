package com.rubbertranslator.controller;

import com.rubbertranslator.App;
import com.rubbertranslator.modules.TranslatorFacade;
import com.rubbertranslator.modules.system.SystemResourceManager;
import com.rubbertranslator.modules.textinput.TextInputListener;
import com.rubbertranslator.modules.textinput.ocr.OCRUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/6 20:49
 */
public class MainController implements TranslatorFacade.TranslatorFacadeListener, TextInputListener {

    // 主功能区
    @FXML
    private TextArea originTextArea;
    @FXML
    private TextArea translatedTextAre;
    @FXML
    private Button translateBt;

    // menuBar区
    // " 功能开关 "
    @FXML
    private ToggleGroup translatorGroup;
    @FXML
    private ToggleGroup sourceLanguageGroup;
    @FXML
    private ToggleGroup destLanguageGroup;

    // 专注模式
    @FXML
    private Menu focusMenu;

    /**
     * 组件初始化完成后，会调用这个方法
     */
    @FXML
    public void initialize() {
        // 注册文本变化监听
        SystemResourceManager.getClipBoardListenerThread().setTextInputListener(this);
        // 注册翻译完成监听
        SystemResourceManager.getFacade().setFacadeListener(this);

        // 专注模式menu初始化
        Label focusMode = new Label("专注模式");
        focusMode.setOnMouseClicked(MainController::switchToFocusMode);
        focusMenu.setText("");  // 清空
        focusMenu.setGraphic(focusMode);
    }


    @FXML
    public void onBtnTranslateClick(ActionEvent actionEvent) {
        String originText = originTextArea.getText();
        processTranslate(originText);
    }

    public static void switchToFocusMode(MouseEvent event) {
        try {
            App.setRoot(ControllerConstant.FOCUS_CONTROLLER_FXML);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(MainController.class.getName()).warning(e.getMessage());
        }
    }


    private void processTranslate(String text){
        SystemResourceManager.getFacade().process(text);
    }

    @Override
    public void onTextInput(String text) {
        Platform.runLater(()->originTextArea.setText(text));
        // 翻译
        processTranslate(text);
    }

    @Override
    public void onImageInput(Image image) {
        try {
            String text = OCRUtils.ocr(image);
            if(text != null){
                onTextInput(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).warning(e.getMessage());
        }
    }

    @Override
    public void onComplete(String text) {
        // 不管从哪里会回调，回到UI线程
        Platform.runLater(()->translatedTextAre.setText(text));
    }

}
