package com.rubbertranslator;

import com.rubbertranslator.controller.ControllerFxmlPath;
import com.rubbertranslator.system.SystemResourceManager;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX App
 */
public class App extends Application {
    // 主界面
    private static Scene appScene;
    private static Stage appStage;
    private static String currentContentRoot;

    @Override
    public void init() throws Exception {
        super.init();
        if(!SystemResourceManager.init()){
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"系统启动失败");
            System.exit(-1);
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        SystemResourceManager.destroy();
    }

    @Override
    public void start(Stage stage) throws IOException {
        Logger.getLogger(this.getClass().getName()).info("程序启动");
        // 避免隐式exit
        Platform.setImplicitExit(false);

        appStage = stage;
        appScene = new Scene(loadFXML(ControllerFxmlPath.FOCUS_CONTROLLER_FXML));
        stage.setScene(appScene);
        stage.show();
    }

    public static void setKeepTop(boolean keepTop){
        appStage.setAlwaysOnTop(keepTop);
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        return fxmlLoader.load();
    }


    public static void resizeStage(){
        // xxx:写得不太好，可进行优化
        if(ControllerFxmlPath.FOCUS_CONTROLLER_FXML.equals(currentContentRoot)){
            appStage.setWidth(660);
            appStage.setHeight(400);
        }else if(ControllerFxmlPath.MAIN_CONTROLLER_FXML.equals(currentContentRoot)){
            appStage.setWidth(800);
            appStage.setHeight(600);
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