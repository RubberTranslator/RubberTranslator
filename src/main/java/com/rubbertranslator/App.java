package com.rubbertranslator;

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
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX App
 */
public class App extends Application {
    // 主界面
    private static Scene appScene;

    @Override
    public void init() throws Exception {
        super.init();
        if (!SystemResourceManager.init()) {
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
    }

    @Override
    public void start(Stage stage) throws IOException {
        Logger.getLogger(this.getClass().getName()).info("主界面启动");
        // 避免隐式exit
        Platform.setImplicitExit(false);
        stage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
        });

        // 这个必须在任何ui初始化前注入
        SystemResourceManager.setStage(stage);
        uiInit();
        stage.show();
    }

    private static void uiInit() throws IOException {
        Stage appStage = SystemResourceManager.getStage();
        SystemConfiguration configuration = SystemResourceManager.getConfigurationProxy();
        // 初始化ui， load scene
        String lastFxml = configuration.getLastFxmlPath();
        appScene = loadScene(lastFxml);
        appStage.setScene(appScene);
        // last position
        Point lastPos = configuration.getLastPos();
        if (lastPos.getX() != 0 && lastPos.getY() != 0) {
            appStage.setX(lastPos.getX());
            appStage.setY(lastPos.getY());
        }
        // last size
        Point lastSize = configuration.getLastSize();
        if (lastSize.getX() != 0 && lastSize.getY() != 0) {
            appStage.setWidth(lastSize.getX());
            appStage.setHeight(lastSize.getY());
        }
        // set keepTop or not
        appStage.setAlwaysOnTop(configuration.isKeepTop());
        // load css
        try {
            // 回显
            String path = configuration.getStyleCssPath();
            if (path != null) {
                File file = new File(path);
                if (file.exists()) {
                    appStage.getScene().getStylesheets().setAll(file.toURI().toURL().toString());
                }
            }
        } catch (MalformedURLException e) {
            Logger.getLogger(SystemResourceManager.class.getName()).log(Level.WARNING, e.getLocalizedMessage(), e);
        }

    }

    public static Scene loadScene(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load());
        scene.setUserData(fxml);
        return scene;
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