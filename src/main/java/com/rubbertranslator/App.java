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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX App
 */
public class App extends Application {
    // 主界面
    private static Stage appStage;
    private static Map<String,Scene> loadedSceneMap = new HashMap<>();

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
            // 退出前，更新配置
            updateConfig();
            Platform.exit();
        });

        uiInit();
        stage.show();
    }

    private static void updateConfig() {
        // 1. 更新当前位置
        SystemConfiguration configuration = SystemResourceManager.getConfigurationProxy();
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

    private static void uiInit () throws IOException {
        SystemConfiguration configuration = SystemResourceManager.getConfigurationProxy();
        // 初始化ui， load scene
        String lastFxml = configuration.getLastFxmlPath();
        loadScene(lastFxml);

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

    /**
     * 加载scene，并设置scene
     * @param fxml
     * @throws IOException
     */
    // TODO: 切换scene后，必须refresh，这里采用的是改变height的方法,但是这样做相当丑陋，目前也没找到相关的api可以强制刷新（准确来说，javafx不支持强制刷新，它是按照 一定间隔的脉冲来刷新的
    private static int refreshCounter = 0;
    public static void loadScene(String fxml) throws IOException {
        Scene scene;
        if(!loadedSceneMap.containsKey(fxml)){
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
            scene = new Scene(fxmlLoader.load());
            scene.setUserData(fxml);
            loadedSceneMap.put(fxml,scene);
        }else{
            scene = loadedSceneMap.get(fxml);
        }
        appStage.setScene(scene);
        // 必须refresh
        if(refreshCounter++ % 2 == 0){
            appStage.setWidth(appStage.getWidth()-0.5);
        }else{
            appStage.setWidth(appStage.getWidth()+0.5);
        }
        appStage.setHeight(appStage.getHeight());

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