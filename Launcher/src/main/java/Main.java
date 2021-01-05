import javafx.application.Application;
import javafx.application.Platform;
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
import sun.rmi.runtime.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    // 主进程handler
    private Process mainProcess;

    // 主进程可执行文件路径
    private final String mainProcessPath = "E:\\RubberTranslator\\Main\\target\\Main\\Main.exe";

    //  进度条弹窗
    private Text percentageText;

    private ProgressBar bar;


    @Override
    public void start(Stage primaryStage) {
        runMainProgram();
        checkUpdate();
    }


    /**
     * 执行主程序
     */
    void runMainProgram() {
        try {
            mainProcess = new ProcessBuilder(mainProcessPath).start();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "启动主进程失败");
            System.exit(-1);
        }
    }

    /**
     * 获取本地version
     *
     * @return 成功 version
     * 失败 null
     */
    private String getLocalVersion() {
        // 从子进程中获取
        BufferedInputStream in = new BufferedInputStream(mainProcess.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            String version = br.readLine().split("\n")[0];
            return version;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 两层try-catch，有没有更好的写法？
            try {
                br.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    void checkUpdate() {
        new Thread(() -> {
            String localVersion = getLocalVersion();
            if (localVersion == null) {
                Logger.getLogger(this.getClass().getName()).severe("获取本地version失败");
                System.exit(-1);
            }
            // 获取localVersion
            UpdateUtils.checkUpdate(localVersion, hasUpdate -> {
                if (hasUpdate) {
                    Platform.runLater(this::remindUserToUpdateDialog);
                }else{
                    // 无需更新，直接退出
                    System.exit(0);
                }
            });
        }).start();
    }

    /**
     * 显示【提示用户更新】弹窗
     */
    private void remindUserToUpdateDialog() {
        Dialog dialog = new Dialog();
        // 确定和取消
        ButtonType confirmBt = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBt = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmBt, cancelBt);
        dialog.setTitle("检测到新版本");
        dialog.setContentText("RubberTranslator已发布新版本，点击【确定】下载新版本");
        Optional optional = dialog.showAndWait();
        try {
            if (optional.get() == confirmBt) {
                doUpdate();
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 执行update
     */
    void doUpdate() {
        showUpdatingProgressDialog();
        terminateMainProgram();
        downLoadNewVersion();
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
        mainProcess.destroy();
    }

    /**
     * 开启下载
     */
    private void downLoadNewVersion() {

    }
}
