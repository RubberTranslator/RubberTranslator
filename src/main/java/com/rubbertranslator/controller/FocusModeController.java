package com.rubbertranslator.controller;

import com.rubbertranslator.App;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/9 21:51
 */
public class FocusModeController {
    @FXML
    private ToggleGroup translatorGroup;

    @FXML
    public void switchToMainMode() throws IOException {
        App.setRoot(ControllerConstant.MAIN_CONTROLLER_FXML);
    }
}
