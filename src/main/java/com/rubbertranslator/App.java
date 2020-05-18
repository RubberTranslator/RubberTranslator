package com.rubbertranslator;

import com.rubbertranslator.controller.ControllerConstant;
import com.rubbertranslator.system.SystemResourceManager;
import javafx.application.Application;
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
        appStage = stage;
        appScene = new Scene(loadFXML(ControllerConstant.MAIN_CONTROLLER_FXML));
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
        if(ControllerConstant.FOCUS_CONTROLLER_FXML.equals(currentContentRoot)){
            appStage.setWidth(660);
            appStage.setHeight(400);
        }else if(ControllerConstant.MAIN_CONTROLLER_FXML.equals(currentContentRoot)){
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
        launch();
    }

}