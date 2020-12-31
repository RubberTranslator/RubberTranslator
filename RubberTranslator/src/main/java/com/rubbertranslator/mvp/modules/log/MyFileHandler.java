package com.rubbertranslator.mvp.modules.log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;

/**
 * @author Raven
 * @version 1.0
 * date  2020/5/19 9:22
 * 自定义FileHandler,java log框架存在bug，如果日志文件目录不存在，会抛出异常，详情参考：
 * https://stackoverflow.com/questions/1263876/configuring-java-filehandler-logging-to-create-directories-if-they-do-not-exist
 *
 */
public class MyFileHandler extends FileHandler {

    static {
        LogManager manager = LogManager.getLogManager();
        String cname = MyFileHandler.class.getName();
        String pattern = manager.getProperty(cname + ".pattern");
        if(pattern == null) pattern = "%h/java%u.log";
        File logFile = generate(pattern,1,0,0);
        File dir = logFile.getParentFile();
        if(!dir.exists()){
            dir.mkdirs();
        }
    }

    static File generate(String pat, int count, int generation, int unique) {
        Path path = Paths.get(pat);
        Path result = null;
        boolean sawg = false;
        boolean sawu = false;
        StringBuilder word = new StringBuilder();
        Path prev = null;
        for (Path elem : path) {
            if (prev != null) {
                prev = prev.resolveSibling(word.toString());
                result = result == null ? prev : result.resolve(prev);
            }
            String pattern = elem.toString();
            int ix = 0;
            word.setLength(0);
            while (ix < pattern.length()) {
                char ch = pattern.charAt(ix);
                ix++;
                char ch2 = 0;
                if (ix < pattern.length()) {
                    ch2 = Character.toLowerCase(pattern.charAt(ix));
                }
                if (ch == '%') {
                    if (ch2 == 't') {
                        String tmpDir = System.getProperty("java.io.tmpdir");
                        if (tmpDir == null) {
                            tmpDir = System.getProperty("user.home");
                        }
                        result = Paths.get(tmpDir);
                        ix++;
                        word.setLength(0);
                        continue;
                    } else if (ch2 == 'h') {
                        result = Paths.get(System.getProperty("user.home"));
//                        if (jdk.internal.misc.VM.isSetUID()) {
//                            // Ok, we are in a set UID program.  For safety's sake
//                            // we disallow attempts to open files relative to %h.
//                            throw new IOException("can't use %h in set UID program");
//                        }
                        ix++;
                        word.setLength(0);
                        continue;
                    } else if (ch2 == 'g') {
                        word = word.append(generation);
                        sawg = true;
                        ix++;
                        continue;
                    } else if (ch2 == 'u') {
                        word = word.append(unique);
                        sawu = true;
                        ix++;
                        continue;
                    } else if (ch2 == '%') {
                        word = word.append('%');
                        ix++;
                        continue;
                    }
                }
                word = word.append(ch);
            }
            prev = elem;
        }

        if (count > 1 && !sawg) {
            word = word.append('.').append(generation);
        }
        if (unique > 0 && !sawu) {
            word = word.append('.').append(unique);
        }
        if (word.length() > 0) {
            String n = word.toString();
            Path p = prev == null ? Paths.get(n) : prev.resolveSibling(n);
            result = result == null ? p : result.resolve(p);
        } else if (result == null) {
            result = Paths.get("");
        }

        if (path.getRoot() == null) {
            return result.toFile();
        } else {
            return path.getRoot().resolve(result).toFile();
        }
    }


    public MyFileHandler() throws IOException, SecurityException {
        super();
    }

    public MyFileHandler(String pattern) throws IOException, SecurityException {
        super(pattern);
    }

    public MyFileHandler(String pattern, boolean append) throws IOException, SecurityException {
        super(pattern, append);
    }

    public MyFileHandler(String pattern, int limit, int count) throws IOException, SecurityException {
        super(pattern, limit, count);
    }

    public MyFileHandler(String pattern, int limit, int count, boolean append) throws IOException, SecurityException {
        super(pattern, limit, count, append);
    }

    public MyFileHandler(String pattern, long limit, int count, boolean append) throws IOException {
        super(pattern, (int) limit, count, append);
    }
}
