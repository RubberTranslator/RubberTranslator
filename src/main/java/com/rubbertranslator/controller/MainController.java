package com.rubbertranslator.controller;

import com.rubbertranslator.Starter;
import com.rubbertranslator.manager.TranslatorFacade;
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
public class MainController {

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
    }

    @FXML
    public void onBtnTranslateClick(ActionEvent actionEvent) {
        String originText = originTextArea.getText();
        String text = facade.process(originText);
        translatedTextAre.setText(text);
    }
}
