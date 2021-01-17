package com.rubbertranslator.mvp.view.custom;

import com.rubbertranslator.entity.ApiInfo;
import com.rubbertranslator.listener.GenericCallback;
import com.rubbertranslator.mvp.view.stage.impl.AppStage;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiDialog {
    private final String dialogTitle;
    private final ApiInfo apiInfo;
    private final String titleClickUrl;
    private final GenericCallback<ApiInfo> listener;
    private final Stage stage;

    public ApiDialog(String dialogTitle, String titleClickUrl, ApiInfo apiInfo,Stage stage, GenericCallback<ApiInfo> listener) {
        this.dialogTitle = dialogTitle;
        this.titleClickUrl = titleClickUrl;
        this.apiInfo = apiInfo;
        this.stage = stage;
        this.listener = listener;
    }

    public TextField apiKeyTf;
    public TextField secretKeyTf;

    private Node create() {
        // 主内容
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        // 26、13、171
        Label title = new Label(dialogTitle);
        title.setStyle("-fx-text-fill: #2196f3;" +
                "-fx-font-weight: bold");
        title.setOnMouseClicked((event -> {
            try {
                Desktop.getDesktop().browse(new URI(titleClickUrl));
            } catch (IOException | URISyntaxException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "错误url:" + titleClickUrl, e);
            }
        }));
        Label apiKeyLabel = new Label("API_KEY");
        apiKeyTf = new TextField();
        apiKeyTf.setText(apiInfo.getApiKey());
        Label secretLabel = new Label("SECRET_KEY");
        secretKeyTf = new TextField();
        secretKeyTf.setText(apiInfo.getSecretKey());
        vBox.getChildren().addAll(title, apiKeyLabel, apiKeyTf, secretLabel, secretKeyTf);
        return vBox;
    }

    public void showDialog() {
        javafx.scene.control.Dialog<ApiInfo> dialog = new javafx.scene.control.Dialog<>();
        // 确定和取消
        ButtonType confirmBt = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBt = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmBt, cancelBt);
        dialog.getDialogPane().setContent(create());
        dialog.initOwner(stage);

        // 结果转换器
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmBt) {
                return new ApiInfo(apiKeyTf.getText(), secretKeyTf.getText());
            }
            return null;
        });
        // 处理结果
        Optional<ApiInfo> result = dialog.showAndWait();
        result.ifPresent(ocrInfo -> {
            // 获取更新
            String apiKey = ocrInfo.apiKey;
            String secretKey = ocrInfo.secretKey;
            if ("".equals(apiKey) || "".equals(secretKey)) {
                Logger.getLogger(this.getClass().getName()).info("api 填写不完整，请重新填写");
            } else {
                listener.callBack(new ApiInfo(apiKey, secretKey));
            }
        });
    }
}
