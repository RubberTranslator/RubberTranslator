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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


/**
 * Launcher：
 * 1. 负责热更新检测与执行
 * 2. 调用、终止主程序
 * 这部分代码很多强耦合，功能比较简单，所以没有考虑较好的设计
 */
public class Launcher extends Application {

    // 主进程handler
    private Process mainProcess;

    private final String mainDir = "E:\\RubberTranslator\\out\\RubberTranslator";

    private final String mainProcessPath;

    private final String mainProcessJarDir;

    {
        // 主进程可执行文件路径
        mainProcessPath = mainDir + File.separator + "Main.exe";

        // 更新文件临时放置路径
        mainProcessJarDir = mainDir + File.separator + "app/tmp";

    }

    private String localVersion;
    private String remoteVersionUrl;
    // 更新文件路径
    private String remoteFileUrl;

    //  进度条弹窗
    private Text title;
    private Text percentageText;
    private ProgressBar bar;


    @Override
    public void start(Stage primaryStage) {
        initLog();
        runMainProgram();
        checkUpdate();
    }

    private void initLog() {
        LoggerManager.configLog();
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


    void checkUpdate() {
        new Thread(() -> {
            setNecessaryInfos();
            if (localVersion == null) {
                Logger.getLogger(this.getClass().getName()).severe("获取本地version失败");
                System.exit(-1);
            }
            // 获取localVersion
            UpdateUtils.checkUpdate(localVersion, remoteVersionUrl, hasUpdate -> {
                if (hasUpdate) {
                    Platform.runLater(this::remindUserToUpdateDialog);
                } else {
                    // 无需更新，直接退出
                    System.exit(0);
                }
            });
        }).start();
    }

    /**
     * 从主程序中获取必要信息
     * 1. 本地version
     * 2. 远端versionUrl
     * 3. 远端目标文件Url
     *
     * @return 成功 version
     * 失败 null
     */
    private void setNecessaryInfos() {
        // 从子进程中获取
        BufferedInputStream in = new BufferedInputStream(mainProcess.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        BufferedOutputStream out = new BufferedOutputStream(mainProcess.getOutputStream());
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

        try {
            bw.write(Protocol.LOCAL_VERSION + "\n");
            bw.flush();
            localVersion = br.readLine().split("\n")[0];
            Logger.getLogger(this.getClass().getName()).info("local version:" + localVersion);

            bw.write(Protocol.REMOTE_VERSION_URL + "\n");
            bw.flush();
            remoteVersionUrl = br.readLine().split("\n")[0];
            Logger.getLogger(this.getClass().getName()).info("remote version url:" + remoteVersionUrl);

            bw.write(Protocol.REMOTE_TARGET_FILE_URL + "\n");
            bw.flush();
            remoteFileUrl = br.readLine().split("\n")[0];
            Logger.getLogger(this.getClass().getName()).info("remote file url:" + remoteFileUrl);

            // end
            bw.write(Protocol.END + "\n");
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 两层try-catch，有没有更好的写法？
            try {
                br.close();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            } else {
                // 无需更新
                System.exit(0);
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
        downloadNewVersion();
    }

    /**
     * 创建更新过程弹窗
     */
    private void showUpdatingProgressDialog() {
        title = new Text("更新中,请勿关闭此窗口");
        title.setFont(new Font(16));

        bar = new ProgressBar(0);
        percentageText = new Text("0.0%");
        HBox hBox = new HBox(5, bar, percentageText);
        hBox.setAlignment(Pos.CENTER);

        VBox vBox = new VBox(5, title, hBox);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10, 20, 10, 20));

        Scene scene = new Scene(vBox);
        Stage appStage = new Stage();
        appStage.setScene(scene);

        appStage.setOnCloseRequest(event -> {
            System.exit(-1);
        });

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
    private void downloadNewVersion() {
        File tmpDir = new File(mainProcessJarDir);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        File targetDir = tmpDir.getParentFile();

        DownloadUtil.get().download(remoteFileUrl, tmpDir.getAbsolutePath(), new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                Platform.runLater(() -> {
                    title.setText("下载完成，正在启动");

                    // move from "app/tmp" --> "app", 不应该放在UI线程
                    Path tmpPath = Paths.get(tmpDir + File.separator + "Main.jar");
                    Path targetPath = Paths.get(targetDir + File.separator + "Main.jar");
                    try {
//                        Files.copy(tmpPath,targetPath,REPLACE_EXISTING, ATOMIC_MOVE);
                        Files.move(tmpPath, targetPath, REPLACE_EXISTING, ATOMIC_MOVE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    runMainProgram();
                    System.exit(0);
                });
            }

            @Override
            public void onDownloading(double progress) {
                Platform.runLater(() -> {
                    bar.setProgress(progress);
                    percentageText.setText(String.format("%.2f%%", progress * 100));
                });
            }

            @Override
            public void onDownloadFailed() {
                Platform.runLater(() -> {
                    title.setText("更新失败，请手动下载并替换");
                });
            }
        });
    }
}

