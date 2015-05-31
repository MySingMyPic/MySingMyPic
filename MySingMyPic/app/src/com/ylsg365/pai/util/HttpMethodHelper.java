package com.ylsg365.pai.util;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpMethodHelper {
    private static final String TAG = "HttpMethodHelper";

    public static HttpGet getHttpGet(String url) {
        HttpGet get = new HttpGet(url);
        // LogUtil.logd(TAG, String.format("getHttpGet  cookie=%s",
        // ReaderApplication.getInstance().getHttpCookies()));
        return get;
    }

    public static HttpPost getHttpPost(String url) {
        HttpPost post = new HttpPost(url);
        // LogUtil.logd(TAG, String.format("getHttpPost  cookie=%s",
        // ReaderApplication.getInstance().getHttpCookies()));
        return post;
    }

    private URL url = null;
    FileUtils fileUtils = new FileUtils();

    public int downfile(String urlStr, String path, String fileName) {
        if(fileName.contains("/")) {
            int index = fileName.lastIndexOf("/");
            fileName = fileName.substring(index + 1, fileName.length());
        }
        if (FileUtils.isFileExist(path + fileName)) {
            return 1;
        } else {

            try {
                InputStream input = null;
                input = getInputStream(urlStr);
                File resultFile = FileUtils.write2SDFromInput(path, fileName,
                        input);
                if (resultFile == null) {
                    return -1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return 0;
    }

    // ���ڵõ�һ��InputStream�����������ļ�����ǰ����Ĳ��������Խ����������װ����һ������
    public InputStream getInputStream(String urlStr) throws IOException {
        InputStream is = null;
        try {
            url = new URL(urlStr);
            HttpURLConnection urlConn = (HttpURLConnection) url
                    .openConnection();
            is = urlConn.getInputStream();

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return is;
    }
}
