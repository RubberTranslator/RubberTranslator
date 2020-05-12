package com.rubbertranslator.controller;

import com.rubbertranslator.App;
import com.rubbertranslator.modules.system.SystemResourceManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;


/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/12 19:29
 */
public class OCRController {
    @FXML
    private TextField apiKeyTf;
    @FXML
    private TextField secretKeyTf;

    @FXML
    public void initialize() {
        // 回显
        apiKeyTf.setText(SystemResourceManager.getConfigurationProxy().getTextInputConfig().getBaiduOcrApiKey());
        secretKeyTf.setText(SystemResourceManager.getConfigurationProxy().getTextInputConfig().getBaiduOcrSecretKey());
    }

    @FXML
    public void onConfirmBtnClick(ActionEvent actionEvent){
        System.out.println("确认OCR");
        // 获取更新
        String apiKey = apiKeyTf.getText();
        String secretKey = secretKeyTf.getText();
        if("".equals(apiKey) || "".equals(secretKey)){
           // Dialog to remind user
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setHeaderText("OCR信息不完整");
            alert.setContentText("请填写所有字段");
            alert.showAndWait();
        }else{
            SystemResourceManager.getConfigurationProxy().getTextInputConfig().setBaiduOcrApiKey(apiKey);
            SystemResourceManager.getConfigurationProxy().getTextInputConfig().setBaiduOcrSecretKey(secretKey);
        }
        App.closeOCRDialog();
    }
}
