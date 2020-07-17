package com.rubbertranslator;

import com.rubbertranslator.entity.ControllerFxmlPath;
import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.system.SystemConfiguration;
import com.rubbertranslator.system.SystemResourceManager;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX App
 */
public class App extends Application {
    // 主界面
    private static Stage appStage;
    // 配置
    private static SystemConfiguration configuration;

    @Override
    public void init() throws Exception {
        super.init();
        configuration = SystemResourceManager.init();
        if (configuration == null) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "系统配置失败");
            System.exit(-1);
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "系统配置成功");
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        SystemResourceManager.destroy();
        Logger.getLogger(this.getClass().getName()).info("系统资源已销毁，现在退出");
        // TODO:强制退出
        System.exit(0);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Logger.getLogger(this.getClass().getName()).info("主界面启动");
        // 避免隐式exit
        appStage = stage;
        Platform.setImplicitExit(false);
        stage.setOnCloseRequest(windowEvent -> {
            updateConfig(configuration);
            Platform.exit();
        });
        initViews(configuration);
        stage.show();
    }


    public void updateConfig(SystemConfiguration configuration) {
        // 1. 更新当前位置
        configuration.setLastPos(new Point(
                (int)appStage.getX(),(int)appStage.getY()
        ));
        // 2. 更新窗口大小
        configuration.setLastSize(new Point(
                (int)appStage.getWidth(),(int)appStage.getHeight()
        ));
        // 3. 更新窗口模式 main or focus
        configuration.setLastFxmlPath(
                (String) appStage.getScene().getUserData()
        );
    }


    public static void switchScene(SceneType sceneType) {
        try{
            switch (sceneType){
                case MAIN_SCENE:
                    loadScene(ControllerFxmlPath.MAIN_CONTROLLER_FXML);
                    break;
                case FOCUS_SCENE:
                    loadScene(ControllerFxmlPath.FOCUS_CONTROLLER_FXML);
                    break;
                case FILTER_SCENE:
                    loadScene(ControllerFxmlPath.FILTER_CONTROLLER_FXML);
                    break;
                case WORDS_REPLACE_SCENE:
                    loadScene(ControllerFxmlPath.WORDS_REPLACER_CONTROLLER_FXML);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 加载scene，并设置scene
     * @param fxml
     * @throws IOException
     */
    public static void loadScene(String fxml) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load());
        scene.setUserData(fxml);
        appStage.setScene(scene);
    }


    public void initViews(SystemConfiguration configuration) {
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
    }


    public static void main(String[] args) {
        boolean alreadyRunning;
        try {
            JUnique.acquireLock("RubberTranslator");
            alreadyRunning = false;
        } catch (AlreadyLockedException e) {
            alreadyRunning = true;
        }
        if (!alreadyRunning) {
            // Start sequence here
            launch();
        } else {
            System.exit(0);
        }
    }

}