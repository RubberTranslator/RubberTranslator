package com.rubbertranslator;

import com.rubbertranslator.controller.ControllerFxmlPath;
import com.rubbertranslator.system.SystemResourceManager;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX App
 */
public class App extends Application {
    // 主界面
    private static Scene appScene;
    private static String currentContentRoot;

    @Override
    public void init() throws Exception {
        super.init();
        if(!SystemResourceManager.init()){
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"系统配置失败");
            System.exit(-1);
        }else{
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"系统配置成功");
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        SystemResourceManager.destroy();
        Logger.getLogger(this.getClass().getName()).info("系统资源已销毁，退出中");
    }

    @Override
    public void start(Stage stage) throws IOException {
        Logger.getLogger(this.getClass().getName()).info("主界面启动");
        // 避免隐式exit
        Platform.setImplicitExit(false);
        stage.setOnCloseRequest(windowEvent -> Platform.exit());

        // 这个必须在任何ui初始化前注入
        SystemResourceManager.setStage(stage);
        // 初始化ui
        appScene = new Scene(loadFXML(ControllerFxmlPath.FOCUS_CONTROLLER_FXML));
        stage.setScene(appScene);

        stage.show();
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        return fxmlLoader.load();
    }


    public static void resizeStage(){
        // xxx:写得不太好，可进行优化
        if(ControllerFxmlPath.FOCUS_CONTROLLER_FXML.equals(currentContentRoot)){
            SystemResourceManager.getStage().setWidth(660);
            SystemResourceManager.getStage().setHeight(400);
        }else if(ControllerFxmlPath.MAIN_CONTROLLER_FXML.equals(currentContentRoot)){
            SystemResourceManager.getStage().setWidth(800);
            SystemResourceManager.getStage().setHeight(600);
        }
    }


    public static void setRoot(String fxml) throws IOException {
        currentContentRoot = fxml;
        appScene.setRoot(loadFXML(fxml));
        resizeStage();
    }


    public static void main(String[] args) {
        String appId = "RubberTranslator";
        boolean alreadyRunning;
        try {
            JUnique.acquireLock(appId);
            alreadyRunning = false;
        } catch (AlreadyLockedException e) {
            alreadyRunning = true;
        }
        if (!alreadyRunning) {
            // Start sequence here
            launch();
        }else{
            System.exit(0);
        }

    }

}