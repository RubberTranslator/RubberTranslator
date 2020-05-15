package com.rubbertranslator.utils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author Raven
 * @version 1.0
 * date  2020/5/15 8:58
 * 文件读写工具
 */
public class FileUtil {
    /**
     * 输入流到文件
     *
     * @param in   输入流
     * @param file 文件
     * @throws IOException 文件IO错误
     */
    public static void copyInputStreamToFile(InputStream in, File file) throws IOException {
        if (in == null) return;
        // 父目录创建
        File parent = file.getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            throw new IOException("parent dir cannot be created");
        }
        FileOutputStream fout = null;
        BufferedOutputStream bout = null;
         try{
            fout = new FileOutputStream(file);
            bout = new BufferedOutputStream(fout);
            int b;
            while ((b = in.read()) != -1) {
                bout.write(b);
            }
        } finally {
            in.close();
            if (bout != null) bout.close();
            if (fout != null) fout.close();
        }
    }

    /**
     * 从文件读字符串
     * @param file 文件
     * @param encoding 文件编码
     * @return 文件内容
     *         读取失败，抛出异常
     * @throws IOException 文件IO错误
     */
    public static String readFileToString(File file, Charset encoding) throws IOException {
        InputStream in = null;
        Reader reader = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            in = new FileInputStream(file);
            reader = new InputStreamReader(in,encoding);
            br = new BufferedReader(reader);
            String line;
            while((line = br.readLine())!= null){
                sb.append(line);
            }
        } finally {
            if(in != null) in.close();
            if(br != null) br.close();
            if(reader!=null) reader.close();
        }
        return sb.toString();
    }

    public static void writeStringToFile(File file, String data,  Charset encoding) throws IOException {
        // 父目录创建
        File parent = file.getParentFile();
        if(!parent.exists() && !parent.mkdirs()){
            throw new IOException("parent dir cannot be created");
        }
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), encoding)) {
            writer.write(data);
        }
    }
}
