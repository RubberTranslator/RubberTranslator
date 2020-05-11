package com.rubbertranslator;

import com.rubbertranslator.modules.system.SystemResourceManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void init() throws Exception {
        super.init();
        SystemResourceManager.init();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        SystemResourceManager.destroy();
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("/fxml/main.fxml"));
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.show();
    }
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        return fxmlLoader.load();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }


    public static void main(String[] args) {
        launch();
    }

}