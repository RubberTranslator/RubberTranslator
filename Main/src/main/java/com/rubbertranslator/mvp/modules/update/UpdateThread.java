//package com.rubbertranslator.mvp.modules.update;
//
//import javafx.application.Platform;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.ButtonBar;
//import javafx.scene.control.ButtonType;
//import javafx.scene.control.Dialog;
//import javafx.scene.control.ProgressBar;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.scene.text.Font;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
//import org.jetbrains.annotations.NotNull;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.Optional;
//import java.util.Properties;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
//import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
//
//public class UpdateThread extends Thread {
//
//    //  进度条弹窗
//    private Text title;
//    private Text percentageText;
//    private ProgressBar bar;
//
//    // 其他辅助变量
//    private String remoteTargetFileUrl = null;
//
//    // 用于辅助判定下载文件是否是压缩文件（因为jar包其实就是一个zip文件）
//    private static final byte[] ZIP_HEADER_1 = new byte[]{80, 75, 3, 4};
//
//    private static final byte[] ZIP_HEADER_2 = new byte[]{80, 75, 5, 6};
//
//    private String tmpUpdateJarPath;
//
//    private String targetJarPath;
//
//    private Stage mainStage;
//
//    public UpdateThread(@NotNull Stage stage) {
//        this.mainStage = stage;
//    }
//
//    {
//        String os = System.getProperty("os.name").toLowerCase();
//        if (os.startsWith("win")) {
//            // 主进程可执行文件路径
//            targetJarPath = "./app/Main.jar";
//            // 更新文件临时放置路径
//            tmpUpdateJarPath = "./app/tmp/Main.jar";
//        } else if (os.startsWith("linux")) {
//            targetJarPath = "../lib/app/Main.jar";
//            tmpUpdateJarPath = "../lib/app/tmp/Main.jar";
//        } else {  // mac?
//
//        }
//    }
//
//    @Override
//    public void run() {
//        super.run();
//        try {
//            Properties props = new Properties();
//            props.load(this.getClass().getResourceAsStream("/config/misc.properties"));
//            String localVersion = props.getProperty("local-version");
//            String remoteVersionUrl = props.getProperty("remote-version-url");
//            remoteTargetFileUrl = props.getProperty("remote-target-file-url");
//            UpdateUtils.checkUpdate(localVersion, remoteVersionUrl, (hasUpdate) -> {
//                if (hasUpdate) {
//                    Platform.runLater(this::remindUserToUpdateDialog);
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 显示【提示用户更新】弹窗
//     */
//    private void remindUserToUpdateDialog() {
//
//        Dialog dialog = new Dialog();
//        // 确定和取消
//        ButtonType confirmBt = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
//        ButtonType cancelBt = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
//        dialog.getDialogPane().getButtonTypes().addAll(confirmBt, cancelBt);
//        dialog.setTitle("检测到新版本");
//        dialog.setContentText("RubberTranslator已发布新版本，点击【确定】下载新版本");
//        dialog.initOwner(mainStage);
//        Optional optional = dialog.showAndWait();
//        try {
//            if (optional.get() == confirmBt) {
//                Logger.getLogger(this.getClass().getName()).info("正在更新...");
//                doUpdate();
//            }
//        } catch (Exception ignored) {
//        }
//    }
//
//    /**
//     * 执行update
//     */
//    void doUpdate() {
//        showUpdatingProgressDialog();
//        downloadNewVersion();
//    }
//
//    /**
//     * 创建更新过程弹窗
//     */
//    private void showUpdatingProgressDialog() {
//        title = new Text("更新中,请勿关闭此窗口");
//        title.setFont(new Font(16));
//
//        bar = new ProgressBar(0);
//        percentageText = new Text("0.0%");
//        HBox hBox = new HBox(5, bar, percentageText);
//        hBox.setAlignment(Pos.CENTER);
//
//        VBox vBox = new VBox(5, title, hBox);
//        vBox.setAlignment(Pos.CENTER);
//        vBox.setPadding(new Insets(10, 20, 10, 20));
//
//
//        Scene scene = new Scene(vBox);
//
//
//
//        Stage appStage = new Stage();
//        appStage.setAlwaysOnTop(true);
//        appStage.setScene(scene);
//        appStage.show();
//    }
//
//    /**
//     * 开启下载
//     */
//    private void downloadNewVersion() {
//        File tmpUpdateJarFile = new File(tmpUpdateJarPath);
//        // 创建文件夹
//        File tmpUpdateJarDir = tmpUpdateJarFile.getParentFile();
//        if (!tmpUpdateJarDir.exists()) {
//            tmpUpdateJarDir.mkdirs();
//        }
//
//        DownloadUtil.get().download(remoteTargetFileUrl, tmpUpdateJarDir.getAbsolutePath(), new DownloadUtil.OnDownloadListener() {
//            @Override
//            public void onDownloadSuccess() {
//                Platform.runLater(() -> {
//                    title.setText("下载完成，重启以生效");
//                    // 检查是否是zip文件
//                    if (isArchiveFile(tmpUpdateJarFile)) {
//                        // move from "tmp" --> "current dir", 不应该放在UI线程
//                        Path tmpPath = Paths.get(tmpUpdateJarPath);
//                        Path targetPath = Paths.get(targetJarPath);
//                        try {
//                            Files.move(tmpPath, targetPath, REPLACE_EXISTING, ATOMIC_MOVE);
//                        } catch (IOException e) {
//                            Logger.getLogger(this.getClass().getName()).severe("update failed");
//                            Logger.getLogger(this.getClass().getName()).severe(e.getLocalizedMessage());
//                        }
//                    } else {
//                        title.setText("下载失败，请手动下载");
//                        Logger.getLogger(this.getClass().getName()).info("update失败，下载文件非zip");
//                    }
//
//                });
//            }
//
//            @Override
//            public void onDownloading(double progress) {
//                Platform.runLater(() -> {
//                    bar.setProgress(progress);
//                    percentageText.setText(String.format("%.2f%%", progress * 100));
//                });
//            }
//
//            @Override
//            public void onDownloadFailed() {
//                Platform.runLater(() -> {
//                    title.setText("更新失败，请手动下载并替换");
//                });
//
//            }
//        });
//    }
//
//
//    /**
//     * 判断文件是否为一个压缩文件
//     *
//     * @param file
//     * @return
//     */
//    public static boolean isArchiveFile(File file) {
//        if (file == null) {
//            return false;
//        }
//
//        if (file.isDirectory()) {
//            return false;
//        }
//
//        boolean isArchive = false;
//        try (InputStream input = new FileInputStream(file)) {
//            byte[] buffer = new byte[4];
//            int length = input.read(buffer, 0, 4);
//            if (length == 4) {
//                isArchive = (Arrays.equals(ZIP_HEADER_1, buffer)) || (Arrays.equals(ZIP_HEADER_2, buffer));
//            }
//        } catch (IOException e) {
//            Logger.getLogger(UpdateUtils.class.getName()).log(Level.SEVERE, "判定下载文件是否为压缩(jar)包失败");
//        }
//
//        return isArchive;
//    }
//
//}
