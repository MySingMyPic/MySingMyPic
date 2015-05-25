package com.ylsg365.pai.activity.music;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ylsg365.pai.R;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.customview.CurveView;
import com.ylsg365.pai.customview.LyricView;
import com.ylsg365.pai.util.DensityUtil;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Random;

public class SongScoreActivity extends ActionBarActivity {

    private String song="";
    private String singer="";

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private CheckBox rightCheck;

    private TextView musicTime,currentScore,totalScore;
    private LyricView lyricView;
    private CurveView curve;

    private String recordpath; //录音文件保存路径，根据时间产生文件名
    private String playpath1 = Environment.getExternalStorageDirectory()+ "/1pai/test1.mp3";  //播放文件1默认路径
    private String playpath2 = Environment.getExternalStorageDirectory()+ "/1pai/test2.mp3";  //播放文件2(伴奏)默认路径
    private String lrcpath = Environment.getExternalStorageDirectory()+ "/1pai/play.lrc";  //歌词文件路径

    private MediaPlayer mediaPlayer;
    private int INTERVAL=45;//歌词每行的间隔
    LyricRunnable lyricRunnable;

    CurveRunnable curveRunnable;
    private int curScore=0;
    private int Score=0;
    private int time=0;

    private RelativeLayout btn_cancel,btn_retry,btn_save;
    private MediaRecorder mediaRecorder;
    private File audioFile;
    private long startTime;
    private int minute,second,current; //显示的分钟，秒数；当前播放位置（用于切换原唱伴唱记录位置）
    private boolean isrecording;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_score);

        song=getIntent().getStringExtra("song");
        singer=getIntent().getStringExtra("singer");
        current = 0;
        setupToolbar();
        initWidget();

        initMusic(1);
        initRecord();
        SerchLrc();
        startThread();
        //开始播放
        mediaPlayer.start();
        mediaPlayer.seekTo(current);
        mediaRecorder.start();
    }

    private void initRecord() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
        String date = sDateFormat.format(new java.util.Date());
        String Fpath = Environment.getExternalStorageDirectory() + "/1pai";
        File file = new File(Fpath);
        if (!file.exists()) {
            file.mkdir();
        }
        recordpath = Fpath + "/" + date + "record.mp3";
        //path=Environment.getExternalStorageDirectory()+"/"+date+"record.mp4";
        audioFile = new File(recordpath);
        mediaRecorder = new MediaRecorder();
        //mediaRecorder.reset();
        //mediaRecorder.setOrientationHint(90);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);     // 设置从麦克风采集声音MIC(或来自录像机的声音AudioSource.CAMCORDER)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioSamplingRate(44100);  //采样率
        mediaRecorder.setAudioChannels(1);          //单声道
        //mediaRecorder.setCaptureRate();             //
        mediaRecorder.setAudioEncodingBitRate(128000);//比特率
        //mediaRecorder.setMaxDuration(duration);    //设置可录制长度
        //mediaRecorder.setOnInfoListener(this);
        mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// 设置声音编码的格式
        try {
            startTime = System.currentTimeMillis();
            thandler.postDelayed(recordtimertask, 0);
            isrecording = true;
            mediaRecorder.prepare();
//            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void startThread()
    {
//        lyricRunnable.setFlag(false);
//        curveRunnable.setFlag(false);

        lyricRunnable=new LyricRunnable();
        new Thread(lyricRunnable).start();

        curveRunnable=new CurveRunnable();
        new Thread(curveRunnable).start();


        lyricView.setOffsetY(220 - lyricView.SelectIndex(mediaPlayer.getCurrentPosition())
                * (lyricView.getSIZEWORD() + INTERVAL-1));
    }

    public void initWidget()
    {

        lyricView=(LyricView)findViewById(R.id.mylrc);
        musicTime =(TextView)findViewById(R.id. music_time);
        currentScore  =(TextView)findViewById(R.id. current_score);
        totalScore  =(TextView)findViewById(R.id. total_score);
        curve=(CurveView)findViewById(R.id.curve);
        btn_cancel = (RelativeLayout) findViewById(R.id.cancel);
        btn_retry = (RelativeLayout) findViewById(R.id.play_again);
        btn_save = (RelativeLayout) findViewById(R.id.finish);


        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMusic(1);
                initRecord();
                //开始播放
                mediaPlayer.start();
                mediaPlayer.seekTo(current);
                mediaRecorder.start();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHelper.finish(SongScoreActivity.this);
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SongScoreActivity.this,"录音文件已保存："+recordpath ,Toast.LENGTH_SHORT);

                lyricRunnable.setFlag(false);
                curveRunnable.setFlag(false);
                isrecording = false;
                if(mediaPlayer!=null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer=null;
                }
                if(mediaRecorder!=null)
                {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder=null;
                }
                NavHelper.toSingASoneActivity(SongScoreActivity.this,recordpath);

            }
        });



    }

    public void setupToolbar() {
        View v = findViewById(R.id.toolbar);
        if (v != null) {
            toolbar = (Toolbar) v;
            setSupportActionBar(toolbar);
            toolbarTitle = (TextView) v.findViewById(R.id.toolbar_title);
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            toolbarTitle.setVisibility(View.VISIBLE);
            toolbarTitle.setText(singer+"-"+song);
            rightCheck=(CheckBox)v.findViewById(R.id.text_right);
            rightCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        if (mediaPlayer.isPlaying()){
                            current = mediaPlayer.getCurrentPosition();
                        }else{
                            current=0;
                        }
                        initMusic(2);
                        mediaPlayer.start();
                        mediaPlayer.seekTo(current);
                    }else{
                        if (mediaPlayer.isPlaying()){
                            current = mediaPlayer.getCurrentPosition();
                        }else{
                            current=0;
                        }
                        initMusic(1);
                        mediaPlayer.start();
                        mediaPlayer.seekTo(current);
                    }

                }
            });
        }
    }

    class LyricRunnable implements Runnable {

        boolean flag=true;
        public void setFlag(boolean flag)
        {
            this.flag=flag;
        }
        @Override
        public void run() {
            while (isrecording) {
                try {
                    Thread.sleep(100);
                    if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
                        lyricView.setOffsetY(lyricView.getOffsetY() - lyricView.SpeedLrc());
                        lyricView.SelectIndex(mediaPlayer.getCurrentPosition());
                        mHandler.post(mUpdateResults);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CurveRunnable implements Runnable {

        boolean flag=true;
        public void setFlag(boolean flag)
        {
            this.flag=flag;
        }
        @Override
        public void run() {
            while (flag) {
                try {
                    handler.sendEmptyMessage(0x1234);
                    Thread.sleep(2000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    Handler mHandler = new Handler();
    Runnable mUpdateResults = new Runnable() {
        public void run() {
            lyricView.invalidate(); // 更新视图
        }
    };

    private Handler thandler = new Handler();
    private Runnable recordtimertask = new Runnable() {
        public void run() {
            if (isrecording) {
                long mills = System.currentTimeMillis() - startTime;
                int dura = (int) Math.ceil(mills / 1000);
                minute = (int)Math.floor(dura / 60);
                second = (int)Math.floor(dura % 60);
                thandler.postDelayed(this, 1000);
                musicTime.setText("正在录制:" + format(minute) + ":"+ format(second));
            }
        }
    };
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        lyricRunnable.setFlag(false);
        curveRunnable.setFlag(false);
        isrecording = false;
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
        if(mediaRecorder!=null)
        {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder=null;
        }
    }

    public void SerchLrc() {

        String lrc= lrcpath;
        LyricView.read(lrc);
        lyricView.SetTextSize();
        lyricView.setOffsetY(350);
        lyricView.setSIZEWORD(DensityUtil.dip2px(this, 16));

    }

    //初始化音乐播放
    void initMusic(int choice){
        //进入Idle
        if (mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        try {
            //初始化
            if (choice==2){
                mediaPlayer.setDataSource(playpath2);
            }
            else{
                mediaPlayer.setDataSource(playpath1);
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(true);
            // prepare 通过异步的方式装载媒体资源
            mediaPlayer.prepare();
            //time=mediaPlayer.getDuration();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what==0x1234)
            {
                if(mediaPlayer.getCurrentPosition()<mediaPlayer.getDuration()){
                //musicTime.setText("正在录制 "+TimeUtil.getTimeStr3(mediaPlayer.getCurrentPosition()));

                int score=new Random().nextInt(4) + 1;
                curve.setData(score);
                curScore=score*25;
                Score+=curScore;
                    time++;
                 int  itemScore=Score/time;
                currentScore.setText(curScore+"");
                totalScore.setText("总分"+Score+"分");
                }
                else
                {
                    curveRunnable.setFlag(false);
                }
            }
        }
    };

    /*
     * 格式化时间
     */
    public String format(int i) {
        String s = i + "";
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }
}
