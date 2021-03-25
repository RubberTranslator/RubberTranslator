package com.rubbertranslator.mvp.view.custom;

import com.rubbertranslator.listener.GenericCallback;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * 透明度设置
 * 0 完全透明
 * 1 完全不透明
 */
public class OpacitySettingDialog {
    private double value;

    private Stage stage;

    private Slider slider;

    private final double minValue = 0.1f;

    private final double maxValue = 1.0f;

    private GenericCallback<Double> listener;

    private javafx.scene.control.Dialog<Double> dialog;

    public OpacitySettingDialog(Stage stage, Double value, GenericCallback<Double> listener) {
        if (stage == null) stage = new Stage();
        this.stage = stage;
        if (value == null) value = maxValue;
        this.value = Math.max(Math.min(value, maxValue), minValue);
        this.listener = listener;
    }

    public Node createContent() {
        // 主内容
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        // 26、13、171
        Label title = new Label("透明度设置");
        title.setStyle("-fx-text-fill: #2196f3;" +
                "-fx-font-weight: bold");
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(5);
        // slider
        slider = new Slider(minValue, maxValue, value);
        Label sliderLabel = new Label(String.format("%.2f", value));
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            sliderLabel.setText(String.format("%.2f", newValue));
            dialog.getDialogPane().setOpacity((Double) newValue);
        });
        hBox.getChildren().addAll(slider, sliderLabel);

        // add hbox
        vBox.getChildren().addAll(title, hBox);
        return vBox;
    }

    public void showDialog() {
        dialog = new javafx.scene.control.Dialog<>();
        // confirm and cancel
        ButtonType confirmBt = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBt = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmBt, cancelBt);
        dialog.getDialogPane().setContent(createContent());
        dialog.initOwner(stage);

        dialog.getDialogPane().setOpacity(value);

        // result convert
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmBt) {
                return slider.getValue();
            }
            return value;
        });

        // result
        dialog.showAndWait().ifPresent((ret) -> {
                    if (listener != null) {
                        listener.callBack(ret);
                    }
                }
        );

    }
}
