package com.ylsg365.pai.imagedisplay;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.FileUtils;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageSelectActivity extends ActionBarActivity {

    private TextView select;
    private GridView gridview;
    ImageSelectAdapter adapter;
    List<PhotoItem> list = new ArrayList<PhotoItem>();

    private String filepath="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);

        setupToolbar();
        gridview=(GridView)findViewById(R.id.gridview);

        select=(TextView)findViewById(R.id.select_user);
        list.add(null);
        list.addAll(getPhotoItem());
        adapter=new ImageSelectAdapter(this,list);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position==0)
                {
                filepath = FileUtils.path + FileUtils.headPath;
                if (!new File(FileUtils.path).exists()) {
                    new File(FileUtils.path).mkdirs();
                    try {
                        new File(filepath).createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                NavHelper.toPicCutActivityForResule(ImageSelectActivity.this, filepath);
                }
                else
                    adapter.setSelect(position);
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, File> files = new HashMap<String, File>();

                        List<PhotoItem> photoItems=adapter.getSelect();
                        for(int i=0;i<photoItems.size();i++){
                            File file = new File(photoItems.get(i).getPath());
                            if(file.exists())
                                files.put(file.getName(), file);
                        }
                        String url = Constants.WEB_SERVER_DOMAIN2 + "fileController/imgUpload" ;

                        try {
                            String str = YinApi.imgUplode(url, new HashMap<String, String>(), files);
                            if (!StringUtil.isNull(str)) {
                                JSONObject json = new JSONObject(str);
                                if (JsonUtil.getBoolean(json, "status")) {

                                    JSONArray array = JsonUtil.getJSONArray(json, "fileName");
                                    Log.e("imageList",array.toString());
                                    if (array.length() > 0) {

                                        ArrayList<String> imageList=new ArrayList<String>();
                                        for(int i=0;i<array.length();i++){
                                            str =  array.get(i).toString();
                                            imageList.add(str);
                                        }
                                        Intent intent = new Intent();
                                        intent.putStringArrayListExtra("imageList", imageList);

                                        setResult(NavHelper.RESULT_SELECT_IMAGE_SUCCESS, intent);
                                        NavHelper.finish(ImageSelectActivity.this);
                                    }

                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });
    }


    private Toolbar toolbar;
    private TextView toolbarTitle;
    private TextView leftTextView;

    public void setupToolbar() {
        View v = findViewById(R.id.toolbar);
        if (v != null) {
            toolbar = (Toolbar) v;
            setSupportActionBar(toolbar);
            toolbarTitle = (TextView) v.findViewById(R.id.toolbar_title);
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            toolbarTitle.setText("相片胶卷");
            leftTextView = (TextView) v.findViewById(R.id.text_toolbar_left);

            leftTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavHelper.finish(ImageSelectActivity.this,NavHelper.RESULT_SELECT_IMAGE_SUCCESS);
                }
            });
            leftTextView.setText("取消");
        }
    }




    private static final String[] STORE_IMAGES = {
            MediaStore.Images.Media.DISPLAY_NAME, // 显示的名
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.LONGITUDE, // 经度
            MediaStore.Images.Media._ID, // id
            MediaStore.Images.Media.BUCKET_ID, // dir id 目录
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME // dir name 目录名字

    };

    /**
     * 方法描述：按相册获取图片信息
     *
     */
    private List<PhotoItem> getPhotoItem() {
        List<PhotoItem> list = new ArrayList<PhotoItem>();
        String totalPath=""+ Environment.getExternalStorageDirectory()+"/DCIM/Camera/";
        Cursor cursor = MediaStore.Images.Media.query(getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);

        while (cursor.moveToNext()) {
            String path=cursor.getString(1);
            String id = cursor.getString(3);

//					Log.e("info", "id==="+id+"==dir_id=="+dir_id+"==dir=="+dir+"==path="+path);
            if(path.startsWith(totalPath)){
                list.add(new PhotoItem(Integer.valueOf(id),path));

            }
        }
        cursor.close();

        return list;
    }

    private Bitmap mBitmap=null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NavHelper.REQUEST_GO_TO_PICCUT) {   //更换头像返回方法
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                mBitmap = BitmapFactory.decodeFile(filepath, options);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

            if (mBitmap != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        File file = new File(filepath);
                        Map<String, File> files = new HashMap<String, File>();
                        files.put(file.getName(), file);
                        String url = Constants.WEB_SERVER_DOMAIN2 + "fileController/imgUpload" ;

                        try {
                            String str = YinApi.imgUplode(url, new HashMap<String, String>(), files);
                            if (!StringUtil.isNull(str)) {
                                JSONObject json = new JSONObject(str);
                                if (JsonUtil.getBoolean(json, "status")) {

                                    JSONArray array = JsonUtil.getJSONArray(json, "fileName");
                                    Log.e("imageList",array.toString());
                                    if (array.length() > 0) {
                                        str =  array.get(0).toString();
                                        ArrayList<String> imageList=new ArrayList<String>();
                                        imageList.add(str);
                                        Intent intent = new Intent();
                                        intent.putStringArrayListExtra("imageList", imageList);

                                        setResult(NavHelper.RESULT_SELECT_IMAGE_SUCCESS, intent);
                                        NavHelper.finish(ImageSelectActivity.this);
                                    }

                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        }
    }
}
