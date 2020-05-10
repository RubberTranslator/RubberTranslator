package com.rubbertranslator.controller;

import com.rubbertranslator.Starter;
import com.rubbertranslator.modules.TranslatorFacade;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/6 20:49
 */
public class MainController implements TranslatorFacade.TranslatorFacadeListener {

    @FXML
    private TextArea originTextArea;
    @FXML
    private TextArea translatedTextAre;
    @FXML
    private Button translateBt;
    @FXML
    private ToggleGroup translatorGroup;
    @FXML
    private ToggleGroup sourceLanguageGroup;
    @FXML
    private ToggleGroup destLanguageGroup;

    private TranslatorFacade facade;

    public MainController() {
        Starter starter = new Starter();
        facade = starter.start();
        facade.setFacadeListener(this);
    }

    @FXML
    public void onBtnTranslateClick(ActionEvent actionEvent) {
        String originText = originTextArea.getText();
        facade.process(originText);
    }

    @Override
    public void onComplete(String text) {
        // 不管从哪里会回调，回到UI线程
        Platform.runLater(()->translatedTextAre.setText(text));
    }
}
