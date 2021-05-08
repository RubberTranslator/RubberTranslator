package com.rubbertranslator.mvp.view.custom;

import com.rubbertranslator.entity.AppearanceSetting;
import com.rubbertranslator.listener.GenericCallback;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AppearanceSettingDialog {

    private Stage stage;

    // 这里的泛型不适用String，后续会报非常奇怪的错误, 错误在代码65行
    private ComboBox<String> appFontSizeCombo;

    private ComboBox<String> textAreaFontSizeCombo;

    private final AppearanceSetting defaultSetting;

    private final GenericCallback<AppearanceSetting> callback;

    public AppearanceSettingDialog(Stage stage, AppearanceSetting setting, GenericCallback<AppearanceSetting> listener) {
        if (stage == null) stage = new Stage();
        defaultSetting = setting;
        callback = listener;
    }

    public Node createContent() {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(5);
        Label appFontSizeLabel = new Label("界面字体大小设置:");
        appFontSizeCombo = new ComboBox<>();
        Label textAreaFontSizeLabel = new Label("文本区字体大小设置:");
        textAreaFontSizeCombo = new ComboBox<>();
        appFontSizeCombo.setEditable(false);
        textAreaFontSizeCombo.setEditable(false);
        appFontSizeCombo.setValue(String.valueOf(defaultSetting.appFontSize));
        textAreaFontSizeCombo.setValue(String.valueOf(defaultSetting.textFontSize));
        // Set combo range
        for (int i = 5; i < 30; ++i) {
            appFontSizeCombo.getItems().add(String.valueOf(i));
            textAreaFontSizeCombo.getItems().add(String.valueOf(i));
        }
        hBox.getChildren().addAll(appFontSizeLabel, appFontSizeCombo, textAreaFontSizeLabel, textAreaFontSizeCombo);
        return hBox;
    }

    public void showDialog() {
        Dialog<AppearanceSetting> dialog = new Dialog<AppearanceSetting>();
        // confirm and cancel
        ButtonType confirmBt = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBt = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmBt, cancelBt);
        dialog.getDialogPane().setContent(createContent());
        dialog.initOwner(stage);


        // result convert
        dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == confirmBt) {
                        AppearanceSetting setting = new AppearanceSetting();
                        setting.appFontSize = Integer.parseInt(appFontSizeCombo.getValue());
                        setting.textFontSize = Integer.parseInt(textAreaFontSizeCombo.getValue());
                        return setting;
                    }
                    return null;
                }
        );

        // result
        dialog.showAndWait().ifPresent(setting -> {
            if(callback != null) callback.callBack(setting);
        });

    }
}
