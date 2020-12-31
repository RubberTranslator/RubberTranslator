package com.rubbertranslator;

import com.rubbertranslator.mvp.view.stage.impl.AppStage;
import com.rubbertranslator.utils.AppSingletonUtil;
import javafx.application.Application;
import javafx.stage.Stage;
import java.util.logging.Logger;

/**
 * JavaFX App
 */
public class App extends Application {

    private AppStage appStage;


    @Override
    public void start(Stage stage) {
        Logger.getLogger(this.getClass().getName()).info("主界面启动");
        appStage = new AppStage(stage);
        appStage.init();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // TODO: destroy
        appStage.destroy();
    }

    public static void main(String[] args) {
        if (!AppSingletonUtil.isAppRunning()) {
            // Start sequence here
            launch();
        } else {
            System.exit(0);
        }
    }
}