package com.ylsg365.pai.util;

import android.os.Environment;

import com.ylsg365.pai.app.Constants;

import java.io.File;

/**
 * Created by lanzhihong on 2015/4/1.
 */
public class FileUtils {
    public static final String path = Environment.getExternalStorageDirectory()
            .toString()+"/IsingIshot/";
    public static final String headPath ="head.jpeg";  //用户头像

    public static final String roomHead ="roomHead.jpeg";  //包房头像

    public static final String host = Constants.SITE_DOMAIN;

    public static final String mp3 ="mp3/";  //mp3路径


    /**
     * 递归删除文件和文件夹
     *
     * @param file
     *            要删除的根目录
     */
    public static void DeleteFile(File file) {
        if (!file.exists()) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    DeleteFile(f);
                }
                file.delete();
            }
        }
    }
}
