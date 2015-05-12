package com.ylsg365.pai.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import com.ylsg365.pai.activity.base.BaseActivity;


/**
 * @Title: PicCutDemoActivity.java
 * @Description: 图片裁剪功能测试
 * @author XiaoMa
 */
public class PicCutActivity extends BaseActivity {
    private String fileLocation = null;
    private Uri imageUri;// The Uri to store the big bitmap

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.roadbook1);
        // 获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeigh = dip2px(this, 250);
        fileLocation = getIntent().getStringExtra("fileLocation");
        imageUri = Uri.parse("file:///" + fileLocation);
        Intent intent = new Intent(Intent.ACTION_PICK, null);

        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 400);
        intent.putExtra("aspectY", 400);
        intent.putExtra("outputX", 400);
        intent.putExtra("outputY", 400);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        // 图片像素不足时 拉伸图片避免黑边
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        startActivityForResult(intent, 3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 取得裁剪后的图片
            case 3:
                /**
                 * 非空判断大家一定要验证，如果不验证的话， 在剪裁之后如果发现不满意，要重新裁剪，丢弃
                 * 当前功能时，会报NullException，
                 *
                 *
                 */
                if (data != null) {
                    setPicToView(data);
                } else {
                    PicCutActivity.this.finish();
                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        PicCutActivity.this.finish();
    }

    /**
     * diptopx
     * @param context
     * @param dpValue
     * @return
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}