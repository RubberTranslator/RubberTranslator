package com.rubbertranslator.mvp.modules.update;

import com.rubbertranslator.utils.DownloadUtil;
import com.rubbertranslator.utils.OSTypeUtil;
import com.rubbertranslator.utils.UpdateUtils;
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

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

public class UpdateTask implements Runnable {

    // 本地信息
    private String localVersion;
    private String remoteVersionUrl;
    private String remoteTargetFileUrl;

    // 文件存放路径
    private final String mainDir = ".";
    private String tmpUpdateJarPath;

    {
        if (OSTypeUtil.isWin()) {
            tmpUpdateJarPath = mainDir + File.separator + "app/tmp/Main.jar";
        } else if (OSTypeUtil.isLinux()) {
            tmpUpdateJarPath = mainDir + File.separator + "../lib/app/tmp/Main.jar";
        } else if (OSTypeUtil.isMac()) {  // mac?
            tmpUpdateJarPath = mainDir + File.separator + "tmp/Main.jar";
        }
    }

    //  进度条弹窗
    private Text title;
    private Text percentageText;
    private ProgressBar bar;

    {
        try {
            Properties props = new Properties();
            props.load(this.getClass().getResourceAsStream("/config/misc.properties"));
            localVersion = props.getProperty("local-version");
            remoteVersionUrl = props.getProperty("remote-version-url");
            remoteTargetFileUrl = props.getProperty("remote-target-file-url");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        tryUpdate();
    }


    void tryUpdate() {
        if (localVersion == null || remoteVersionUrl == null) return;
        printLocalInfo();

        // check update
        UpdateUtils.checkUpdate(localVersion, remoteVersionUrl, hasUpdate -> {
            if (hasUpdate) {
                Platform.runLater(this::remindUserToUpdateDialog);
            }
        });
    }

    void printLocalInfo(){
        Logger.getLogger(UpdateTask.class.getName()).info("local version: " + localVersion);
        Logger.getLogger(UpdateTask.class.getName()).info("remote version url: " + remoteVersionUrl);
        Logger.getLogger(UpdateTask.class.getName()).info("remote target file url: " + remoteTargetFileUrl);
    }

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
            }
        } catch (Exception e) {
            Logger.getLogger(this.getClass().getName()).severe(e.getLocalizedMessage());
        }
    }


    /**
     * 执行update
     */
    void doUpdate() {
        showUpdatingProgressDialog();
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
        vBox.setFillWidth(true);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10, 20, 10, 20));

        Scene scene = new Scene(vBox,300,100);
        Stage appStage = new Stage();
        appStage.setScene(scene);

        appStage.show();
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

        DownloadUtil.get().download(remoteTargetFileUrl, tmpUpdateJarDir.getAbsolutePath(), new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                Platform.runLater(() -> {
                    title.setText("下载完成，请重启应用以完成更新");
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
