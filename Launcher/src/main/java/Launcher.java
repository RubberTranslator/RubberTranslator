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

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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

    // 用于辅助判定下载文件是否是压缩文件（因为jar包其实就是一个zip文件）
    private static final byte[] ZIP_HEADER_1 = new byte[]{80, 75, 3, 4};

    private static final byte[] ZIP_HEADER_2 = new byte[]{80, 75, 5, 6};

    // 主进程handler
    private Process mainProcess;

    private final String mainDir = ".";

    private String mainExePath;

    private String tmpUpdateJarPath;

    private String targetJarPath;

    {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("win")) {
            // 主进程可执行文件路径
            mainExePath = mainDir + File.separator + "Main.exe";
            targetJarPath = mainDir + File.separator + "app/Main.jar";
            // 更新文件临时放置路径
            tmpUpdateJarPath = mainDir + File.separator + "app/tmp/Main.jar";
        } else if (os.startsWith("linux")) {
            mainExePath = mainDir + File.separator + "Main";
            targetJarPath = mainDir + File.separator + "../lib/app/Main.jar";
            tmpUpdateJarPath = mainDir + File.separator + "../lib/app/tmp/Main.jar";
        } else {  // mac?

        }

    }

    // socket
    private ServerSocket socket;

    // 版本信息
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
        initSocket();
        runMainProgram();
        checkUpdate();
    }

    private void initSocket() {
        try {
            socket = new ServerSocket(21453);
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).severe(e.getLocalizedMessage());
        }
    }

    private void initLog() {
        LoggerManager.configLog();
    }


    /**
     * 执行主程序
     */
    private void runMainProgram() {
        try {
            mainProcess = new ProcessBuilder(mainExePath).start();
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "启动主进程失败");
            destroy(-1);
        }
    }

    private void destroy(int status) {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "关闭socket失败");
        } finally {
            System.exit(status);
        }

    }

    private void checkUpdate() {
        new Thread(() -> {
            setNecessaryInfos();
            if (localVersion == null) {
                Logger.getLogger(this.getClass().getName()).severe("获取本地version失败");
                destroy(-1);
            }
            // 获取localVersion
            UpdateUtils.checkUpdate(localVersion, remoteVersionUrl, hasUpdate -> {
                if (hasUpdate) {
                    Platform.runLater(this::remindUserToUpdateDialog);
                } else {
                    // 无需更新，直接退出
                    destroy(0);
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
        Socket client = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            client = socket.accept();
            br = new BufferedReader(new InputStreamReader(new BufferedInputStream(new DataInputStream(client.getInputStream()))));
            bw = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(new DataOutputStream(client.getOutputStream()))));

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
        } catch (IOException | NullPointerException e) {
            Logger.getLogger(this.getClass().getName()).severe(e.getLocalizedMessage());
        } finally {
            // 两层try-catch，有没有更好的写法？
            try {
                if (br != null) br.close();
                if (bw != null) bw.close();
                if (client != null) client.close();
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "关闭通信输入、输出识别");
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
                Logger.getLogger(this.getClass().getName()).info("正在更新...");
                doUpdate();
            } else {
                // 无需更新
                destroy(0);
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
            destroy(-1);
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
        File tmpUpdateJarFile = new File(tmpUpdateJarPath);
        // 创建文件夹
        File tmpUpdateJarDir = tmpUpdateJarFile.getParentFile();
        if (!tmpUpdateJarDir.exists()) {
            tmpUpdateJarDir.mkdirs();
        }

        DownloadUtil.get().download(remoteFileUrl, tmpUpdateJarDir.getAbsolutePath(), new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                Platform.runLater(() -> {
                    title.setText("下载完成，正在启动");
                    // 检查是否是zip文件
                    if (isArchiveFile(tmpUpdateJarFile)) {
                        // move from "tmp" --> "current dir", 不应该放在UI线程
                        Path tmpPath = Paths.get(tmpUpdateJarPath);
                        Path targetPath = Paths.get(targetJarPath);
                        try {
                            Files.move(tmpPath, targetPath, REPLACE_EXISTING, ATOMIC_MOVE);
                        } catch (IOException e) {
                            Logger.getLogger(this.getClass().getName()).severe(e.getLocalizedMessage());
                        }
                        Logger.getLogger(this.getClass().getName()).info("update success");
                        runMainProgram();
                        destroy(0);
                    } else {
                        title.setText("下载失败，请手动下载");
                        Logger.getLogger(this.getClass().getName()).info("update失败，下载文件非zip");
                    }
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


    /**
     * 判断文件是否为一个压缩文件
     *
     * @param file
     * @return
     */
    public static boolean isArchiveFile(File file) {
        if (file == null) {
            return false;
        }

        if (file.isDirectory()) {
            return false;
        }

        boolean isArchive = false;
        try (InputStream input = new FileInputStream(file)) {
            byte[] buffer = new byte[4];
            int length = input.read(buffer, 0, 4);
            if (length == 4) {
                isArchive = (Arrays.equals(ZIP_HEADER_1, buffer)) || (Arrays.equals(ZIP_HEADER_2, buffer));
            }
        } catch (IOException e) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, "判定下载文件是否为压缩(jar)包失败");
        }

        return isArchive;
    }

}

