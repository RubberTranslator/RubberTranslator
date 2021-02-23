package com.rubbertranslator.mvp.view.custom;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;

public class SponsorDialog {
    public static void show() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(5);

        // 标题和描述
        // 26、13、171
        Label title = new Label("请作者喝杯咖啡吧");

        // 微信
        VBox weChatBox = new VBox();
        weChatBox.setAlignment(Pos.CENTER);
        weChatBox.setSpacing(5);
        Label weChatText = new Label("微信");
        weChatText.setStyle("-fx-text-fill: #2196f3;" +
                "-fx-font-weight: bold");
        // 图片
        InputStream weChatInput = SponsorDialog.class.getResourceAsStream("/pic/wechat.png");
        if (weChatInput == null) return;
        Image weChatImg = new Image(weChatInput, 200, 200, true, false);
        ImageView weChatImgView = new ImageView(weChatImg);
        weChatBox.getChildren().addAll(weChatText, weChatImgView);

        // 支付宝
        VBox alipayBox = new VBox();
        alipayBox.setAlignment(Pos.CENTER);
        alipayBox.setSpacing(5);
        Label aliText = new Label("支付宝");
        aliText.setStyle("-fx-text-fill: #2196f3;" +
                "-fx-font-weight: bold");
        // 图片
        InputStream aliInput = SponsorDialog.class.getResourceAsStream("/pic/alipay.png");
        if (aliInput == null) return;
        Image aliPayImg = new Image(aliInput, 200, 200, true, false);
        ImageView aliImgView = new ImageView(aliPayImg);
        alipayBox.getChildren().addAll(aliText, aliImgView);

        HBox hBox = new HBox(weChatBox, alipayBox);
        hBox.setSpacing(10);

        vBox.getChildren().addAll(title, hBox);
        Scene scene = new Scene(vBox);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }
}
