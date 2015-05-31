package com.ylsg365.pai.activity.video;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ylsg365.pai.R;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.customview.DialogView;
import com.ylsg365.pai.listener.DialogListViewListener;
import com.ylsg365.pai.listener.DialogMessageListener;
import com.ylsg365.pai.listener.DialogShareListener;
import com.ylsg365.pai.util.HttpMethodHelper;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VideoLongActivity extends ActionBarActivity {

    private LinearLayout finishPlay;
    private LinearLayout inputMusic;

    // 对话框
    DialogView dialog = new DialogView();
    protected String path_audio;
    protected String path_filestore;
    protected String path_url;
    protected String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_long);

        path = getIntent().getStringExtra("VIDEO_PATH");

        finishPlay = (LinearLayout) findViewById(R.id.layout_video_bottom_bar);
        inputMusic = (LinearLayout) findViewById(R.id.input_music);
        finishPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(path_audio == null) {
                    Toast.makeText(getBaseContext(), "请先选择音乐", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<String> list = new ArrayList<String>();
                list.add("上传");
                list.add("取消");

                dialog.createListViewDialog(VideoLongActivity.this, list,
                        new DialogListViewListener() {
                            @Override
                            public void select(int pos) {
                                switch (pos) {
                                case 0:
                                    createMessageDialog();
                                    break;
                                case 1:
                                    dialog.dismissDialog();
                                    break;
                                }
                            }
                        });
            }
        });
        inputMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHelper.toVideoAddMusicSelectActivityForResult(
                        VideoLongActivity.this, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            path_audio = data.getStringExtra("url");
        }
    }

    public void createMessageDialog() {
        dialog.createMessageDialog(this, new DialogMessageListener() {
            @Override
            public void getMessage(String message) {
                createShareDialog();
                Toast.makeText(getBaseContext(), "开始上传", Toast.LENGTH_SHORT).show();
                loadand();
            }
        });
    }

    public void createShareDialog() {
        dialog.createShareDialog(this, new DialogShareListener() {
            // 0是微信，1是微博
            @Override
            public void select(int choice) {
                switch (choice) {
                case 0:
                    Toast.makeText(VideoLongActivity.this, "微信",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    Toast.makeText(VideoLongActivity.this, "微博",
                            Toast.LENGTH_SHORT).show();
                    break;

                }
            }
        });
    }
    
    private void loadand() {
        new Thread() {
            @Override
            public void run() {
                String url = "http://182.92.170.38:18082/Weitie/client/fileController/workByFile";
                // String mpath = Environment.getExternalStorageDirectory() +
                // "/1pai/test.mp3";
                File file = new File(path);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Map<String, File> files = new HashMap<String, File>();
                files.put("file", file);
                Map<String, String> params = new HashMap<String, String>();
                if(path_audio != null) {
                    params.put("type", "2"); // 需要做判断，对应哪种类型
                    params.put("audio", path_audio);
                }

                try {
                    String str = YinApi.mediaUpload(url, params, files);
                    Log.d("str", str);
                    if (!StringUtil.isNull(str)) {
                        JSONObject json = null;
                        json = new JSONObject(str);
                        if (JsonUtil.getBoolean(json, "status")) {
                            JSONArray array = JsonUtil.getJSONArray(json,
                                    "fileName");
                            if (array.length() > 0) {
                                path_filestore = (String) array.get(0);
                                path_url = "http://" + Constants.SITE_DOMAIN + path_filestore;
                            }
                            loopFileState();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }
    
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 11:
                Toast.makeText(getBaseContext(),
                        "get url success!" + msg.obj, Toast.LENGTH_SHORT)
                        .show();
                download();
                break;
            case 0:
                Toast.makeText(getBaseContext(), "完成!" + msg.obj,
                        Toast.LENGTH_SHORT).show();
                String p = path_filestore;
                if (p.contains("/")) {
                    int index = p.lastIndexOf("/");
                    p = p.substring(index + 1, p.length());
                }
                path = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/1pai/" + p;
                LogUtil.logd("path", path);
                break;
            case 1:
                Toast.makeText(getBaseContext(), "已存在!" + msg.obj,
                        Toast.LENGTH_SHORT).show();
                break;
            case -1:
                Toast.makeText(getBaseContext(),
                        "失败，请重试!" + msg.obj, Toast.LENGTH_SHORT).show();
                break;
            case 2:
                loopFileState();
                break;
            }
        }

    };
    
    private void loopFileState() {
        YinApi.getVideoFileState(new Response.Listener<JSONObject>() { // 自写的调用状态接口的方法；注意切换处理音频视频时，需要改里面的状态接口网址
                    @Override
                    public void onResponse(JSONObject response) {
                        if (!JsonUtil.getBoolean(response, "status")) {
                            // 这里需要重新调用状态接口
                            mHandler.sendEmptyMessageDelayed(2, 10000);
                        } else {
                            Message msg = new Message();
                            msg.what = 11;
                            msg.obj = path_url;
                            mHandler.sendMessage(msg);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }, path_filestore);
    }
    private void download() {
        new Thread() {
            @Override
            public void run() {
                HttpMethodHelper httpMethodHelper = new HttpMethodHelper();
                int result = -1;
                result = httpMethodHelper.downfile(path_url, "1pai/",
                        path_filestore);
                mHandler.sendEmptyMessage(result);
            }

        }.start();
    }

}
