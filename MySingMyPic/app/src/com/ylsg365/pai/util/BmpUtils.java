package com.ylsg365.pai.util;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lanzhihong on 2015/4/1.
 */
public class BmpUtils {
    private static final int defaultWitth = 100;

    /**
     * URI 获取真实路径
     *
     * @param uri
     * @return
     */
    public static String getFilePathFromUri(Activity activity, Uri uri) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.managedQuery(uri,
                proj,                 // Which columns to return
                null,       // WHERE clause; which rows to return (all rows)
                null,       // WHERE clause selection arguments (none)
                null);                 // Order-by clause (ascending by name)

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }


    // 压缩图片容量和尺寸
    public static Bitmap createBitmapAndCompress(String filePath, int width) {

        Bitmap bitmap = null;
        try {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
            newOpts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, newOpts);
            newOpts.inJustDecodeBounds = false;
            int w = newOpts.outWidth;
            int ww = width;
            if (ww == 0) {
                ww = defaultWitth;
            }
            // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
            int be = 1;// be=1表示不缩放
            if (w > ww) {
                be = (int) (newOpts.outWidth / ww);
            }
            if (be <= 0)
                be = 1;
            newOpts.inSampleSize = be;// 设置缩放比例
            // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
            bitmap = BitmapFactory.decodeFile(filePath, newOpts);
            bitmap = zoomImage(bitmap, ww);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;

    }

    public static Bitmap zoomImage(Bitmap bgimage, int ww) {
        if (bgimage == null)
            return null;
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 计算宽高
        int scaleWidth = defaultWitth;
        if (ww == 0) {
            scaleWidth = ww;
        }
        int scaleHeight = (int) (scaleWidth / width * height);
        Bitmap bitmap = null;
        if (width > scaleWidth) { // 缩放图片动作
            bitmap = ThumbnailUtils.extractThumbnail(bgimage, scaleWidth,
                    scaleHeight);
            if (bgimage != null && bgimage.isRecycled()) {
                bgimage.recycle();
            }
        } else {
            bitmap = bgimage;
        }
        return bitmap;
    }


    public static Bitmap optimizeBitmap(String pathName, int maxWidth,
                                        int maxHeight) {
        Bitmap result = null;
        // 图片配置对象，该对象可以配置图片加载的像素获取个数
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 表示加载图像的原始宽高
        options.inJustDecodeBounds = true;
        result = BitmapFactory.decodeFile(pathName, options);
        // Math.ceil表示获取与它最近的整数（向上取值 如：4.1->5 4.9->5）
        int widthRatio = (int) Math.ceil(options.outWidth / maxWidth);
        int heightRatio = (int) Math.ceil(options.outHeight / maxHeight);

        // 设置最终加载的像素比例，表示最终显示的像素个数为总个数的
        if (widthRatio > 1 || heightRatio > 1) {
            if (widthRatio > heightRatio) {
                options.inSampleSize = widthRatio;
            } else {
                options.inSampleSize = heightRatio;
            }
        }
        // 解码像素的模式，在该模式下可以直接按照option的配置取出像素点
        options.inJustDecodeBounds = false;
        result = BitmapFactory.decodeFile(pathName, options);
        return result;
    }

    public static void DownLoad(String url, String path,String name) {

        if (avaiableMedia()) {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            HttpResponse response;
            try {
                response = client.execute(get);
                HttpEntity entity = response.getEntity();
                long length = entity.getContentLength();
                InputStream is = entity.getContent();
                FileOutputStream fileOutputStream = null;
                if (is != null) {
                    File file = new File(path, name);
                    fileOutputStream = new FileOutputStream(file);

                    byte[] buf = new byte[1024];
                    int ch = -1;
                    int count = 0;
                    while ((ch = is.read(buf)) != -1) {
                        fileOutputStream.write(buf, 0, ch);
                        count += ch;
                    }

                }
                fileOutputStream.flush();
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean avaiableMedia() {
        String status = Environment.getExternalStorageState();

        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }
}
