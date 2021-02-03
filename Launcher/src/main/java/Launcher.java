import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


/**
 * Launcher:
 * 1. 检查升级临时目录是否有待更新文件，有则移动
 * 具体而言是将"tmpDirectory"下的所有文件移动到其上一层
 * 2. 启动主程序
 */
public class Launcher {

    // 主进程handler
    private static Process mainProcess;

    // home目录
    private static String mainDir = ".";

    // Main可执行文件路径
    private static String mainExePath;

    // 下载的更新文件目录路径
    private static String tmpDirectory;

    // mac下需要添加后缀
    private static String macJarSuffixString = "-1.0-SNAPSHOT-jfx";

    static {
        if (OSTypeUtil.isWin()) {
            // 主进程可执行文件路径
            mainExePath = mainDir + File.separator + "Main.exe";
            tmpDirectory = mainDir + File.separator + "app/tmp/";
        } else if (OSTypeUtil.isLinux()) {
            mainExePath = mainDir + File.separator + "Main";
            tmpDirectory = mainDir + File.separator + "../lib/app/tmp";
        } else if (OSTypeUtil.isMac()) {  // mac?
            mainExePath = mainDir + File.separator + "../MacOS/Main";
            tmpDirectory = mainDir + File.separator + "tmp";
        }
    }



    public static void main(String[] args) {
        initLog();
        mayUpdate();
        launchMainProgram();
    }

    private static void initLog() {
        LoggerManager.configLog();
    }

    private static void mayUpdate() {
        File tmpDir = new File(tmpDirectory);
        // 初始化
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }

        File[] files = tmpDir.listFiles();
        // 检查tmpDir下是否有文件
        if (files == null || files.length == 0) return;// if no files found

        for (File file : files) {
            // tmp的上一层目录
            String upDir = file.getParentFile().    // tmp
                    getParentFile().        // tmp的上一层
                    getAbsolutePath();
            String fileName = file.getName();
            if (OSTypeUtil.isMac()) {
                int dotIndex = fileName.indexOf('.');
                fileName = fileName.substring(0, dotIndex) + macJarSuffixString + fileName.substring(dotIndex);
            }
            try {
                moveFileAtomically(file.getAbsolutePath(),
                        upDir + File.separator + fileName);
            } catch (IOException e) {
                Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, "copy " + fileName + " failed");
            }
        }
    }

    private static void moveFileAtomically(String srcPath, String destPath) throws IOException {
        Path srcP = Paths.get(srcPath);
        Path destP = Paths.get(destPath);
        Files.move(srcP, destP, REPLACE_EXISTING, ATOMIC_MOVE);
    }

    private static void launchMainProgram() {
        try {
            mainProcess = new ProcessBuilder(mainExePath).start();
        } catch (IOException e) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, "启动主进程失败");
        }
    }


//    private void checkUpdate() {
//        new Thread(() -> {
//            setNecessaryInfos();
//            if (localVersion == null) {
//                Logger.getLogger(this.getClass().getName()).severe("获取本地version失败");
//                destroy(-1);
//            }
//            // 获取localVersion
//            UpdateUtils.checkUpdate(localVersion, remoteVersionUrl, hasUpdate -> {
//                if (hasUpdate) {
//                    Platform.runLater(this::remindUserToUpdateDialog);
//                } else {
//                    // 无需更新，直接退出
//                    destroy(0);
//                }
//            });
//        }).start();
//    }
//
//    /**
//     * 从主程序中获取必要信息
//     * 1. 本地version
//     * 2. 远端versionUrl
//     * 3. 远端目标文件Url
//     *
//     * @return 成功 version
//     * 失败 null
//     */
//    private void setNecessaryInfos() {
//        Socket client = null;
//        BufferedReader br = null;
//        BufferedWriter bw = null;
//        try {
//            client = socket.accept();
//            br = new BufferedReader(new InputStreamReader(new BufferedInputStream(new DataInputStream(client.getInputStream()))));
//            bw = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(new DataOutputStream(client.getOutputStream()))));
//
//            bw.write(Protocol.LOCAL_VERSION + "\n");
//            bw.flush();
//            localVersion = br.readLine().split("\n")[0];
//            Logger.getLogger(this.getClass().getName()).info("local version:" + localVersion);
//
//            bw.write(Protocol.REMOTE_VERSION_URL + "\n");
//            bw.flush();
//            remoteVersionUrl = br.readLine().split("\n")[0];
//            Logger.getLogger(this.getClass().getName()).info("remote version url:" + remoteVersionUrl);
//
//            bw.write(Protocol.REMOTE_TARGET_FILE_URL + "\n");
//            bw.flush();
//            remoteFileUrl = br.readLine().split("\n")[0];
//            Logger.getLogger(this.getClass().getName()).info("remote file url:" + remoteFileUrl);
//
//            // end
//            bw.write(Protocol.END + "\n");
//            bw.flush();
//        } catch (IOException | NullPointerException e) {
//            Logger.getLogger(this.getClass().getName()).severe(e.getLocalizedMessage());
//            destroy(-1);
//        } finally {
//            // 两层try-catch，有没有更好的写法？
//            try {
//                if (br != null) br.close();
//                if (bw != null) bw.close();
//                if (client != null) client.close();
//            } catch (IOException e) {
//                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "关闭通信输入、输出识别");
//                destroy(-1);
//            }
//        }
//    }
//
//    /**
//     * 显示【提示用户更新】弹窗
//     */
//    private void remindUserToUpdateDialog() {
//        Dialog dialog = new Dialog();
//        // 确定和取消
//        ButtonType confirmBt = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
//        ButtonType cancelBt = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
//        dialog.getDialogPane().getButtonTypes().addAll(confirmBt, cancelBt);
//        dialog.setTitle("检测到新版本");
//        dialog.setContentText("RubberTranslator已发布新版本，点击【确定】下载新版本");
//        Optional optional = dialog.showAndWait();
//        try {
//            if (optional.get() == confirmBt) {
//                Logger.getLogger(this.getClass().getName()).info("正在更新...");
//                doUpdate();
//            } else {
//                // 无需更新
//                destroy(0);
//            }
//        } catch (Exception ignored) {
//            destroy(-1);
//        }
//    }
}

