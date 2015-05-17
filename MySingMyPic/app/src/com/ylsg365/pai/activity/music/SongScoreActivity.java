package com.ylsg365.pai.activity.music;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.customview.CurveView;
import com.ylsg365.pai.customview.LyricView;
import com.ylsg365.pai.util.DensityUtil;
import com.ylsg365.pai.util.TimeUtil;

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

    String path = Environment.getExternalStorageDirectory()+ "/LyricSync/test.mp3";
    private MediaPlayer mediaPlayer;
    private int INTERVAL=45;//歌词每行的间隔
    LyricRunnable lyricRunnable;

    CurveRunnable curveRunnable;
    private int curScore=0;
    private int Score=0;
    private int time=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_score);

        song=getIntent().getStringExtra("song");
        singer=getIntent().getStringExtra("singer");
        setupToolbar();
        initWidget();

        initMusic();
        SerchLrc();
        startThread();
    }

    public void startThread()
    {

        lyricRunnable=new LyricRunnable();
        new Thread(lyricRunnable).start();

        curveRunnable=new CurveRunnable();
        new Thread(curveRunnable).start();

        //开始播放
        mediaPlayer.start();
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
        // TODO Auto-generated method stub
            while (flag) {
                try {
                    Thread.sleep(100);
                    if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
                        lyricView.setOffsetY(lyricView.getOffsetY() - lyricView.SpeedLrc());
                        lyricView.SelectIndex(mediaPlayer.getCurrentPosition());

                        mHandler.post(mUpdateResults);
                    }
                } catch (InterruptedException e) {
        // TODO Auto-generated catch block
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
            // TODO Auto-generated method stub
            while (flag) {
                try {
                    handler.sendEmptyMessage(0x1234);
                    Thread.sleep(2000);

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
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
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        lyricRunnable.setFlag(false);
        curveRunnable.setFlag(false);
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    public void SerchLrc() {
        String lrc = path;
        lrc = lrc.substring(0, lrc.length() - 4).trim() + ".lrc".trim();
        lrc= Environment.getExternalStorageDirectory()+ "/LyricSync/test.lrc";
        LyricView.read(lrc);
        lyricView.SetTextSize();
        lyricView.setOffsetY(350);
        lyricView.setSIZEWORD(DensityUtil.dip2px(this, 16));

    }

    //初始化音乐播放
    void initMusic(){
        //进入Idle
        mediaPlayer = new MediaPlayer();
        try {
            //初始化
            mediaPlayer.setDataSource(path);

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // prepare 通过异步的方式装载媒体资源
            mediaPlayer.prepare();
            time=mediaPlayer.getDuration();

        } catch (Exception e) {
            // TODO Auto-generated catch block
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
                musicTime.setText("正在录制 "+TimeUtil.getTimeStr3(mediaPlayer.getCurrentPosition()));

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
}
