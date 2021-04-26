package com.rubbertranslator.mvp.view.stage.impl;

import com.rubbertranslator.App;
import com.rubbertranslator.entity.WindowSize;
import com.rubbertranslator.event.SetKeepTopEvent;
import com.rubbertranslator.event.SetWindowUnTransparentEvent;
import com.rubbertranslator.event.SwitchSceneEvent;
import com.rubbertranslator.system.ControllerFxmlPath;
import com.rubbertranslator.system.SystemConfiguration;
import com.rubbertranslator.system.SystemResourceManager;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date  2020/12/4 13:43
 */
public class AppStage implements InvalidationListener {
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
        initListeners();
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
        if (lastFxml == null) lastFxml = ControllerFxmlPath.MAIN_CONTROLLER_FXML;
        try {
            loadScene(lastFxml);
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "load last scene失败");
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
    private void initListeners() {
        // event bus register
        EventBus.getDefault().register(this);
        // focus
        appStage.focusedProperty().addListener(this);
        appStage.iconifiedProperty().addListener(this);
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
        // 1. 更新当前位置 为了避免双屏越界问题
        ObservableList<Screen> screenSizes = Screen.getScreens();
        final double[] minScreenWidth = {Double.MAX_VALUE};
        final double[] minScreenHeight = {Double.MAX_VALUE};
        screenSizes.forEach(screen -> {
            minScreenWidth[0] = Math.min(minScreenWidth[0], screen.getBounds().getWidth());
            minScreenHeight[0] = Math.min(minScreenHeight[0], screen.getBounds().getHeight());
        });


        double stageWidth = appStage.getWidth();
        double stageHeight = appStage.getHeight();
        double locationX = Math.min(appStage.getX(), minScreenWidth[0] - stageWidth);
        double locationY = Math.min(appStage.getY(), minScreenHeight[0] - stageHeight);
        locationX = locationX > 0 ? locationX : 0;
        locationY = locationY > 0 ? locationY : 0;

        configuration.setLastPos(new Point(
                (int) locationX, (int) locationY
        ));
        // 2. 更新窗口大小
        configuration.setLastSize(new WindowSize(
                appStage.getScene().getWidth(), appStage.getScene().getHeight()
        ));
        // 3. 更新窗口模式 main or focus
        configuration.setLastFxmlPath(
                (String) appStage.getScene().getUserData()
        );
    }


    @Subscribe(threadMode = ThreadMode.POSTING)
    public void switchScene(SwitchSceneEvent switchSceneEvent) {
        // 切换场景时保存一次配置
        updateConfig();
        // 执行切换
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
                case RECORD_SCENE:
                    loadScene(ControllerFxmlPath.RECORD_CONTROLLER_FXML);
                    break;
                case FILTER_SCENE:
                    loadScene(ControllerFxmlPath.FILTER_CONTROLLER_FXML);
                    break;
                case WORDS_REPLACE_SCENE:
                    loadScene(ControllerFxmlPath.WORDS_REPLACER_CONTROLLER_FXML);
                    break;
                default:
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "未识别的场景");
                    break;
            }
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "切换scene失败");
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getLocalizedMessage());
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

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void windowUnTransparent(SetWindowUnTransparentEvent event) {
        appStage.requestFocus();
    }

    /**
     * 窗口失去焦点
     *
     * @param observable
     */
    @Override
    public void invalidated(Observable observable) {
        if (configuration == null) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "config obj is null");
            return;
        }
        if (observable instanceof ReadOnlyBooleanProperty) {
            ReadOnlyBooleanProperty property = ((ReadOnlyBooleanProperty) observable);
            System.out.println(property);
            if ("focused".equals(property.getName())) {
                focusHandler(property.getValue());
            }
            if ("iconified".equals(property.getName())) {
                minimizedHandler(property.getValue());
            }
        }
    }

    void focusHandler(boolean focus) {
        if (configuration.isLossFocusTransparent()) {
            if (!focus) {
                appStage.setOpacity(configuration.getOpacityValue());
            } else {
                appStage.setOpacity(1.0f);
            }
        }
    }

    void minimizedHandler(boolean minimized){
        if(configuration.isMinimizedCancelListen()){
            System.out.println(!minimized);
            SystemResourceManager.setDragCopyAndCpListenState(!minimized);
        }
    }
}
