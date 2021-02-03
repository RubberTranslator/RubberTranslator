import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;
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


        try {
            for (File file : files) {
                if (!isJarFile(file.getAbsolutePath())) {
                    Files.delete(Paths.get(file.getAbsolutePath()));
                    continue;
                }

                // tmp的上一层目录
                String upDir = file.getParentFile().    // tmp
                        getParentFile().        // tmp的上一层
                        getAbsolutePath();
                String fileName = file.getName();
                if (OSTypeUtil.isMac()) {
                    int dotIndex = fileName.indexOf('.');
                    fileName = fileName.substring(0, dotIndex) + macJarSuffixString + fileName.substring(dotIndex);
                }
                moveFileAtomically(file.getAbsolutePath(),
                        upDir + File.separator + fileName);
            }
        } catch (IOException e) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, e.getLocalizedMessage());
        }
    }

    private static void moveFileAtomically(String srcPath, String destPath) throws IOException {
        Path srcP = Paths.get(srcPath);
        Path destP = Paths.get(destPath);
        Files.move(srcP, destP, REPLACE_EXISTING, ATOMIC_MOVE);
    }

    private static boolean isJarFile(String filePath) {
        // Your jar file
        JarFile file = null;
        try {
            file = new JarFile(new File(filePath));
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (file != null)
                    file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    private static void launchMainProgram() {
        try {
            mainProcess = new ProcessBuilder(mainExePath).start();
        } catch (IOException e) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, "启动主进程失败");
        }
    }

}

