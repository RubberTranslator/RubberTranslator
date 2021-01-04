import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class Main extends Application {

    // 主进程handler
    Process mainProcess;

    //  进度条弹窗
    private Text percentageText;

    private ProgressBar bar;


    @Override
    public void start(Stage primaryStage) {
        runMainProgram();
        checkUpdate();
    }


    void runMainProgram(){
        try {
            mainProcess =  new ProcessBuilder("E:\\RubberTranslator\\Main\\target\\Main\\Main.exe").start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void checkUpdate() {
//        UpdateUtils.checkUpdate("v3.0.0-release", hasUpdate -> {
//            if (hasUpdate) {
//                Platform.runLater(this::remindUserToUpdate);
//            }
//        });
        remindUserToUpdate();
    }

    /**
     * 显示【提示用户更新】弹窗
     */
    private void remindUserToUpdate() {
        Dialog dialog = new Dialog();
        // 确定和取消
        ButtonType confirmBt = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBt = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmBt, cancelBt);
        dialog.setTitle("检测到新版本");
        dialog.setContentText("RubberTranslator已发布新版本，点击【确定】下载新版本");
        Optional<ButtonType> optional = dialog.showAndWait();
        try {
            if (optional.get() == confirmBt) {
                showUpdatingProgressDialog();
                terminateMainProgram();
                downLoadNewVersion();
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 创建更新过程弹窗
     */
    private void showUpdatingProgressDialog() {
        Text text = new Text("更新中...");
        text.setFont(new Font(16));

        bar = new ProgressBar(0);
        percentageText = new Text("0.0%");
        HBox hBox = new HBox(5, bar, percentageText);
        hBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(5, text, hBox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10, 20, 10, 20));

        Scene scene = new Scene(vBox);
        Stage appStage = new Stage();
        appStage.setScene(scene);
        appStage.show();
    }

    /**
     * 关闭RubberTranslator主程序
     */
    private void terminateMainProgram() {

    }

    /**
     * 开启下载
     */
    private void downLoadNewVersion() {

    }
}
