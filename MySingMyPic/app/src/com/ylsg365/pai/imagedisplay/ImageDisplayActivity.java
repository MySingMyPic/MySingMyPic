package com.ylsg365.pai.imagedisplay;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.app.Constants;

public class ImageDisplayActivity extends Activity {

    public static String PATH="path";
    private String path="";
    private ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_image_display);

        path=getIntent().getStringExtra(PATH);
        image=(ImageView)findViewById(R.id.image);

        Log.e("",Constants.WEB_IMG_DOMIN+path);
        ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN+path,image);
    }


}
