package com.ylsg365.pai.activity.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.ylsg365.pai.util.FileUtils;
import com.ylsg365.pai.util.HttpMethodHelper;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.StringUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;

public class VideoAddEffectActivity extends ActionBarActivity implements MediaPlayer.OnInfoListener {

    private String path, path_audio; //读取的视频文件路径，下载的音频文件路径（加音乐选中的）
    private String path_url,path_filestore,effectchose; //分别为：下载链接，下载后保存到本地的文件名，选择色特效类型
//    private int duration,current;
    private static int currVolume = 0;
    private SurfaceView video_sv,render_sv;
    private SurfaceHolder mRender;
    private MediaPlayer mMediaPlayer;
//    private File videoFile;
//    private int hour = 0;
//    private int minute = 0;
//    private int second = 0;
//    private boolean isplaying;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private TextView leftTextView;
    private TextView rightTextView;
    private CheckBox video_switcher;
    private LinearLayout yuantuLayout,lomoLayout,kesongLayout,mokaLayout;
    private ImageView yuantuImg,lomoImg,kesongImg,mokaImg,videoImage;
    private Canvas canvas;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case 11:
                    Toast.makeText(VideoAddEffectActivity.this,"get url success!"+msg.obj,Toast.LENGTH_SHORT);
                    download();
                    break;
                case 0:
                    Toast.makeText(VideoAddEffectActivity.this,"完成!"+msg.obj,Toast.LENGTH_SHORT);
                    break;
                case 1:
                    Toast.makeText(VideoAddEffectActivity.this,"已存在!"+msg.obj,Toast.LENGTH_SHORT);
                    break;
                case -1:
                    Toast.makeText(VideoAddEffectActivity.this,"失败，请重试!"+msg.obj,Toast.LENGTH_SHORT);
                    break;
            }
        }

    };
    DialogView dialog=new DialogView();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_add_effect);
        Bundle pageData = getIntent().getExtras();
        if (pageData != null)
        {
            if (getIntent().getStringExtra("VIDEO_PATH")!=null&&!getIntent().getStringExtra("VIDEO_PATH").equals("")){
                path = getIntent().getStringExtra("VIDEO_PATH");
            }
            if (getIntent().getStringExtra("AUDIO_PATH")!=null&&!getIntent().getStringExtra("AUDIO_PATH").equals("")){
                path_audio = getIntent().getStringExtra("AUDIO_PATH");
            }else{
                path_audio = null;
            }

        }
        else {
            Toast.makeText(this, "获取视频文件失败!", Toast.LENGTH_SHORT).show();
        }
        setupToolbar();
        initWidget();
        initListener();
    }

    public void initWidget()
    {
        video_sv = (SurfaceView) findViewById(R.id.videosv);
        render_sv = (SurfaceView) findViewById(R.id.rendersv) ;
        video_switcher=(CheckBox)findViewById(R.id.video_add_effect_checkBox);
        yuantuLayout=(LinearLayout)findViewById(R.id.video_yuantu);
        lomoLayout=(LinearLayout)findViewById(R.id.video_lomo);
        kesongLayout=(LinearLayout)findViewById(R.id.video_kesong);
        mokaLayout=(LinearLayout)findViewById(R.id.video_moka);

        yuantuImg=(ImageView)findViewById(R.id.video_yuantu_img);
        lomoImg=(ImageView)findViewById(R.id.video_lomo_img);
        kesongImg=(ImageView)findViewById(R.id.video_kesong_img);
        mokaImg=(ImageView)findViewById(R.id.video_moka_img);
        videoImage = (ImageView) findViewById(R.id.imageView);
        //videoImage.setVisibility(View.GONE);

        path_filestore = "testback.mp3";
        //canvas = mRender.lockCanvas();
        video_sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    public void initListener()
    {
        video_switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    OpenSpeaker();
                }
                else{
                    CloseSpeaker();
                }
            }
        });
        yuantuLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    setSelectState(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        lomoLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    setSelectState(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        kesongLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    setSelectState(2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mokaLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    setSelectState(3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setSelectState(int choice) throws IOException {
        if (mMediaPlayer!=null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        switch (choice)
        {
            case 0:
                yuantuImg.setImageDrawable(this.getResources().getDrawable(R.drawable.yuanshengxuanzhong));
                lomoImg.setImageDrawable(this.getResources().getDrawable(R.drawable.lomo));
                kesongImg.setImageDrawable(this.getResources().getDrawable(R.drawable.kesong));
                mokaImg.setImageDrawable(this.getResources().getDrawable(R.drawable.moka));
                render(0);
                effectchose = null;
                play();
                break;
            case 1:
                yuantuImg.setImageDrawable(this.getResources().getDrawable(R.drawable.yuansheng));
                lomoImg.setImageDrawable(this.getResources().getDrawable(R.drawable.lomoxuanzhong));
                kesongImg.setImageDrawable(this.getResources().getDrawable(R.drawable.kesong));
                mokaImg.setImageDrawable(this.getResources().getDrawable(R.drawable.moka));
                render(1);
                effectchose = "0";
                play();
                break;
            case 2:
                yuantuImg.setImageDrawable(this.getResources().getDrawable(R.drawable.yuansheng));
                lomoImg.setImageDrawable(this.getResources().getDrawable(R.drawable.lomo));
                kesongImg.setImageDrawable(this.getResources().getDrawable(R.drawable.kesongxuanzhong));
                mokaImg.setImageDrawable(this.getResources().getDrawable(R.drawable.moka));
                render(2);
                effectchose = "1";
                play();
                break;
            case 3:
                yuantuImg.setImageDrawable(this.getResources().getDrawable(R.drawable.yuansheng));
                lomoImg.setImageDrawable(this.getResources().getDrawable(R.drawable.lomo));
                kesongImg.setImageDrawable(this.getResources().getDrawable(R.drawable.kesong));
                mokaImg.setImageDrawable(this.getResources().getDrawable(R.drawable.mokaxuanzhong));
                render(3);
                effectchose = "2";
                play();
                break;
        }
    }

    private void play() throws IOException {
        if(path==null){
            Toast.makeText(this, "无视频文件，请返回上一界面重新录制!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Log.i("VIDEO PLAY","--->start");
            videoImage.setVisibility(View.GONE);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDisplay(video_sv.getHolder());
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setLooping(true);
            //mMediaPlayer.prepareAsync();
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    private void setupToolbar() {
        View v = findViewById(R.id.toolbar);
        if (v != null) {
            toolbar = (Toolbar) v;
            setSupportActionBar(toolbar);
            toolbarTitle = (TextView) v.findViewById(R.id.toolbar_title);
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            toolbarTitle.setVisibility(View.INVISIBLE);
            leftTextView = (TextView) v.findViewById(R.id.text_toolbar_left);

            leftTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavHelper.toVideoStartActivity(VideoAddEffectActivity.this);
                    NavHelper.finish(VideoAddEffectActivity.this);
                }
            });

            rightTextView=(TextView) v.findViewById(R.id.text_right);
            rightTextView.setText("完成");
            rightTextView.setVisibility(View.VISIBLE);
            rightTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 完成操作
                    List<String> list=new ArrayList<String>();
                    list.add("插入音频");
                    list.add("上传");
                    list.add("取消");

                    dialog.createListViewDialog(VideoAddEffectActivity.this,list,new DialogListViewListener() {
                        @Override
                        public void select(int pos) {
                            switch(pos){
                                case 0:
                                    NavHelper.toVideoAddMusicSelectActivity(VideoAddEffectActivity.this,0);
                                    break;
                                case 1:
                                    //createMessageDialog();
                                    loadand();
                                    break;
                                case 2:
                                    dialog.dismissDialog();
                                    break;
                            }
                        }
                    });
                }
            });
        }
    }

    private void loadand() {
        new Thread(){
            @Override
            public void run() {
                String url = "http://182.92.170.38:18080/Weitie/client/fileController/workByFile";
                //String mpath = Environment.getExternalStorageDirectory() + "/1pai/test.mp3";
                File file = new File(path);
                if (!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Map<String, File> files = new HashMap<String, File>();
                files.put("video", file);
                if (path_audio!=null){
                    File audfile = new File(path_audio);
                    files.put("audio", audfile);
                }
                Map<String, String> params = new HashMap<String, String>();
                params.put("flag", effectchose);
                params.put("type", "0");  //TODO 需要做判断，对应哪种类型

                try {
                    String str = YinApi.mediaUpload(url,params,files);
                    Log.i("uploadand","start!");
                    if (!StringUtil.isNull(str)) {
                        JSONObject json = null;
                        json = new JSONObject(str);
                        if (JsonUtil.getBoolean(json, "status")) {

                            loopFileState();

                            JSONArray array = JsonUtil.getJSONArray(json, "fileName");
                            if (array.length() > 0) {
                                path_filestore = (String)array.get(0);
                                path_url = "http://"+FileUtils.host +str;
                                //Toast.makeText(VideoAddEffectActivity.this,path_url,Toast.LENGTH_SHORT);
                                Message msg = new Message();
                                msg.what = 11;
                                msg.obj = path_url;
                                mHandler.sendMessage(msg);
                            }

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    private void render(int type) {
        Log.i("VIDEO RENDER","--->start");
        mRender = null;
        canvas =null;
        mRender = render_sv.getHolder();
        render_sv.setZOrderOnTop(true);
        mRender.setFormat(PixelFormat.TRANSPARENT);
        canvas = mRender.lockCanvas();
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //Canvas mcanvas = canvas;
        Bitmap bitmap = null;
        switch(type){
            case 0:
                //canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.filter_blue);
                canvas.drawBitmap(bitmap, 0, 0, null);
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.filter_red);
                canvas.drawBitmap(bitmap, 0, 0, null);
                break;
            case 3:
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.filter_green);
                canvas.drawBitmap(bitmap, 0, 0, null);
                break;
        }

        mRender.unlockCanvasAndPost(canvas);
    }

    // 打开扬声器
    private void OpenSpeaker() {
        try {
            // 判断扬声器是否在打开
            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            // 获取当前通话音量
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(
                        AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //关闭扬声器
    private void CloseSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Toast.makeText(context,扬声器已经关闭",Toast.LENGTH_SHORT).show();
    }

    private void createMessageDialog()
    {
        dialog.createMessageDialog(this,new DialogMessageListener() {
            @Override
            public void getMessage(String message) {
                //TODO 上传数据，后台处理
                createShareDialog();
            }
        });

    }

    private void loopFileState() {
        YinApi.getVideoFileState(new Response.Listener<JSONObject>() { // 自写的调用状态接口的方法；注意切换处理音频视频时，需要改里面的状态接口网址
            @Override
            public void onResponse(JSONObject response) {
                if (!JsonUtil.getBoolean(response, "status")) {
                    //这里需要重新调用状态接口
                    loopFileState();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    private void download(){
        new Thread(){
            @Override
            public void run() {
                HttpMethodHelper httpMethodHelper = new HttpMethodHelper();
                int result = -1;
                result = httpMethodHelper.downfile(path_url, "1pai/", path_filestore);
                mHandler.sendEmptyMessage(result);
            }

        }.start();
    }

    private void createShareDialog()
    {
        dialog.createShareDialog(this,new DialogShareListener() {
            //0是微信，1是微博
            @Override
            public void select(int choice) {
                switch(choice)
                {
                    case 0:
                        Toast.makeText(VideoAddEffectActivity.this, "微信", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(VideoAddEffectActivity.this,"微博",Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });
    }
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }
}
