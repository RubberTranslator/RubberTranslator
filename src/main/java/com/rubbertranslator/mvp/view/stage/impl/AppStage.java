package com.rubbertranslator.mvp.view.stage.impl;

import com.rubbertranslator.App;
import com.rubbertranslator.entity.ControllerFxmlPath;
import com.rubbertranslator.event.SetKeepTopEvent;
import com.rubbertranslator.event.SwitchSceneEvent;
import com.rubbertranslator.system.SystemConfiguration;
import com.rubbertranslator.system.SystemResourceManager;
import com.rubbertranslator.utils.UpdateUtils;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date  2020/12/4 13:43
 */
public class AppStage {
    // 主界面
    private Stage appStage;
    // 配置
    private SystemConfiguration configuration;

    public AppStage(Stage stage) {
        appStage = stage;
    }

    /**
     * 初始化
     */
    public void init() {
        initSysConfig();
        initViews();
        registerEvent();
        checkUpdate();
    }

    private void checkUpdate() {
        new Thread(() -> UpdateUtils.checkUpdate(aBoolean -> {
            if (aBoolean) {
                // remind user to update
                Platform.runLater(this::remindUserToUpdate);
            }
        })).start();
    }

    private void remindUserToUpdate() {
        Dialog dialog = new Dialog();
        // 确定和取消
        ButtonType confirmBt = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBt = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmBt, cancelBt);
        dialog.setTitle("检测到新版本");
        dialog.setContentText("RubberTranslator已发布新版本，点击【确定】访问下载列表");
        dialog.initOwner(appStage);
        Optional<ButtonType> optional = dialog.showAndWait();
        try {
            if (optional.get() == confirmBt) {
                Desktop.getDesktop().browse(new URI("https://ravenxrz.lanzous.com/b01bezbcf"));
            }
        } catch (Exception e) {
        }

    }

    /**
     * 初始化系统配置，同时得到系统配置类引用，方便后续的其它初始化
     */
    private void initSysConfig() {
        configuration = SystemResourceManager.init();
        if (configuration == null) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "系统配置失败");
            System.exit(-1);
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "系统配置成功");
        }
    }

    /**
     * 初始化界面
     */
    public void initViews() {
        // 初始化ui， load scene
        String lastFxml = configuration.getLastFxmlPath();
        try {
            loadScene(lastFxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // last position
        Point lastPos = configuration.getLastPos();
        if (lastPos.getX() != 0 && lastPos.getY() != 0) {
            appStage.setX(lastPos.getX());
            appStage.setY(lastPos.getY());
        }
        // set keepTop or not
        appStage.setAlwaysOnTop(configuration.isKeepTop());

        // 防止最后一个界面dismiss时，整个程序退出
        Platform.setImplicitExit(false);
        appStage.setOnCloseRequest(windowEvent -> {
            updateConfig();
            Platform.exit();
        });

        // 显示
        appStage.show();
    }


    /**
     * 注册事件
     */
    private void registerEvent() {
        EventBus.getDefault().register(this);
    }

    /**
     * 销毁
     */
    public void destroy() {
        // 系统资源释放
        SystemResourceManager.destroy();
        Logger.getLogger(this.getClass().getName()).info("系统资源已销毁");
        // 释放EventBus
        EventBus.getDefault().unregister(this);
        System.exit(0);
    }

    /**
     * 更新由AppStage管理着的配置，包括：当前window位置，大小和当前模式
     */
    public void updateConfig() {
        // 1. 更新当前位置
        configuration.setLastPos(new Point(
                (int) appStage.getX(), (int) appStage.getY()
        ));
        // 2. 更新窗口大小
        configuration.setLastSize(new Point(
                (int) appStage.getWidth(), (int) appStage.getHeight()
        ));
        // 3. 更新窗口模式 main or focus
        configuration.setLastFxmlPath(
                (String) appStage.getScene().getUserData()
        );
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void switchScene(SwitchSceneEvent switchSceneEvent) {
        try {
            switch (switchSceneEvent.getType()) {
                case MAIN_SCENE:
                    loadScene(ControllerFxmlPath.MAIN_CONTROLLER_FXML);
                    break;
                case FOCUS_SCENE:
                    loadScene(ControllerFxmlPath.FOCUS_CONTROLLER_FXML);
                    break;
                case COMPARE_SCENE:
                    loadScene(ControllerFxmlPath.COMPARE_CONTROLLER_FXML);
                    break;
                case FILTER_SCENE:
                    loadScene(ControllerFxmlPath.FILTER_CONTROLLER_FXML);
                    break;
                case WORDS_REPLACE_SCENE:
                    loadScene(ControllerFxmlPath.WORDS_REPLACER_CONTROLLER_FXML);
                    break;
                default:
                    Logger.getLogger(this.getClass().getName()).log(Level.FINE, "未识别的场景");
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载scene，并设置scene
     *
     * @param fxml
     * @throws IOException
     */
    public void loadScene(String fxml) throws IOException {
        Scene scene;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        scene = new Scene(fxmlLoader.load());
        scene.setUserData(fxml);
        appStage.setScene(scene);
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void setKeepTop(SetKeepTopEvent event) {
        appStage.setAlwaysOnTop(event.isKeepTop());
    }
}
