package com.ylsg365.pai.activity.music;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.ylsg365.pai.R;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.customview.DialogView;
import com.ylsg365.pai.customview.LyricView;
import com.ylsg365.pai.listener.DialogListViewListener;
import com.ylsg365.pai.listener.DialogMessageListener;
import com.ylsg365.pai.listener.DialogMsgAndPriceListener;
import com.ylsg365.pai.listener.DialogShareListener;
import com.ylsg365.pai.util.DensityUtil;
import com.ylsg365.pai.util.TimeUtil;
import java.util.ArrayList;
import java.util.List;

public class SingASoneActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private TextView leftTextView;
    private TextView rightTextView;

    private ImageView back;

    //跳转
    TextView gotoChangeVolume;
    RelativeLayout finish;

    //调声音
    LinearLayout yuansheng,zhangshaohan,liyuchun,sudalv,chenyixun;
    ImageView yuanshengImg,zhangshaohanImg,liyuchunImg,sudalvImg,chenyixunImg;


    //调音效
    LinearLayout wuxiaoguo,juchang,ktv,xianchang;
    ImageView wuxiaoguoImg,juchangImg,ktvImg,xianchangImg;

    //音乐播放
    private MediaPlayer mediaPlayer;
    private ImageView playButton;
    private SeekBar seekBar;
    private TextView currentTime,totalTime;
    private int time;
    private String path;
    private boolean isPlay=false;
    private Equalizer mEqualizer;

    //歌词
    private LyricView lyricView;
    private int INTERVAL=45;//歌词每行的间隔
    LyricRunnable lyricRunnable;

    //对话框
    DialogView dialog=new DialogView();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_asone);
        Bundle pageData = getIntent().getExtras();
        if (pageData != null)
        {
            path = getIntent().getStringExtra("AUDIO_PATH");
        }
        else {
            path = null;
            Toast.makeText(this, "获取音频文件失败!", Toast.LENGTH_SHORT).show();
        }
        //path = Environment.getExternalStorageDirectory()+ "/1pai/test.mp3";

        setupToolbar();
        initWidget();

        initMusic();
        SerchLrc();
        initListener();
        startThread();
    }

    public void startThread()
    {
        new Thread( updateThread).start();
        lyricRunnable=new LyricRunnable();
        new Thread(lyricRunnable).start();
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
        Log.e("time","time:"+time);
        seekBar.setMax(time);
        totalTime.setText(TimeUtil.getTimeStr2(time));


    } catch (Exception e) {
        e.printStackTrace();
    }
}

    Handler handler = new Handler();
    Runnable updateThread = new Runnable(){
        public void run() {
            //获得歌曲现在播放位置并设置成播放进度条的值
            if(mediaPlayer!=null){
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            currentTime.setText(TimeUtil.getTimeStr2(mediaPlayer.getCurrentPosition()));
            }
            //每次延迟100毫秒再启动线程
            if(updateThread!=null&&handler!=null)
                handler.postDelayed(updateThread, 100);
        }
    };


    public void initWidget()
    {
        back=(ImageView)findViewById(R.id.back);

        yuansheng=(LinearLayout)findViewById(R.id.music_yuansheng);
        zhangshaohan=(LinearLayout)findViewById(R.id.music_zhangshaohan);
        liyuchun=(LinearLayout)findViewById(R.id.music_liyuchun);
        sudalv=(LinearLayout)findViewById(R.id.music_sudalv);
        chenyixun=(LinearLayout)findViewById(R.id.music_chenyixun);

        yuanshengImg=(ImageView)findViewById(R.id.music_yuansheng_img);
        zhangshaohanImg=(ImageView)findViewById(R.id.music_zhangshaohan_img);
        liyuchunImg=(ImageView)findViewById(R.id.music_liyuchun_img);
        sudalvImg=(ImageView)findViewById(R.id.music_sudalv_img);
        chenyixunImg=(ImageView)findViewById(R.id.music_chenyixun_img);

        wuxiaoguo=(LinearLayout)findViewById(R.id.music_wuxiaoguo);
        juchang=(LinearLayout)findViewById(R.id.music_juchang);
        ktv=(LinearLayout)findViewById(R.id.music_ktv);
        xianchang=(LinearLayout)findViewById(R.id.music_xianchang);

        wuxiaoguoImg=(ImageView)findViewById(R.id.music_wuxiaoguo_img);
        juchangImg=(ImageView)findViewById(R.id.music_juchang_img);
        ktvImg=(ImageView)findViewById(R.id.music_ktv_img);
        xianchangImg=(ImageView)findViewById(R.id.music_xianchang_img);

        playButton=(ImageView)findViewById(R.id.play_music);
        seekBar=(SeekBar)findViewById(R.id.seekBar);
        currentTime=(TextView)findViewById(R.id.current_time);
        totalTime=(TextView)findViewById(R.id.total_time);

        lyricView = (LyricView) findViewById(R.id.mylrc);
        gotoChangeVolume=(TextView)findViewById(R.id.goto_change_volume);

        finish=(RelativeLayout)findViewById(R.id.finish);
    }

    public void SerchLrc() {
        String lrc = path;
        lrc = lrc.substring(0, lrc.length() - 4).trim() + ".lrc".trim();
        lrc=Environment.getExternalStorageDirectory()+ "/1pai/test.lrc";
        LyricView.read(lrc);
        lyricView.SetTextSize();
        lyricView.setOffsetY(350);
        lyricView.setSIZEWORD(DensityUtil.dip2px(this,16));


    }

    public void initListener()
    {
        yuansheng.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                setSelect(1);
            }
        });

        zhangshaohan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                setSelect(2);
            }
        });

        liyuchun.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                setSelect(3);
            }
        });

        sudalv.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                setSelect(4);
            }
        });

        chenyixun.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                setSelect(5);
            }
        });

        wuxiaoguo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                setSelect(6);
            }
        });

        juchang.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                setSelect(7);
            }
        });

        ktv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelect(8);
            }
        });

        xianchang.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                setSelect(9);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // fromUser判断是用户改变的滑块的值
                if(fromUser==true){
                    int position = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();
                    mediaPlayer.seekTo(progress);
                    currentTime.setText(TimeUtil.getTimeStr2(progress));
                    lyricView.setOffsetY(220 - lyricView.SelectIndex(progress)
                            * (lyricView.getSIZEWORD() + INTERVAL-1));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlay==false)
                    isPlay=true;
                else isPlay=false;

                if(isPlay==true)
                {
                    mediaPlayer.start();
                    playButton.setImageDrawable(SingASoneActivity.this.getResources().getDrawable(R.drawable.zanting));
                    lyricView.setOffsetY(220 - lyricView.SelectIndex(mediaPlayer.getCurrentPosition())
                            * (lyricView.getSIZEWORD() + INTERVAL-1));
                }
                else
                {
                    playButton.setImageDrawable(SingASoneActivity.this.getResources().getDrawable(R.drawable.bofang));
                    mediaPlayer.pause();

                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                initMusic();
                lyricView.SetTextSize();
                lyricView.setOffsetY(200);
                mediaPlayer.start();
            }
        });

        gotoChangeVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHelper.toChangeMusicVolumeActivity(SingASoneActivity.this,path);
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list=new ArrayList<String>();
                list.add("直接上传");
                list.add("定价后上传");
                list.add("取消");

                dialog.createListViewDialog(SingASoneActivity.this,list,new DialogListViewListener() {
                    @Override
                    public void select(int pos) {
                        switch(pos){
                            case 0:
                                createMessageDialog();
                                break;
                            case 1:
                                createMsgAndPriceDialog();
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

    public void createMessageDialog()
    {
        dialog.createMessageDialog(this,new DialogMessageListener() {
            @Override
            public void getMessage(String message) {
                createShareDialog();
            }
        });
    }

    public void createMsgAndPriceDialog()
    {
        dialog.createMsgAndPriceDialog(this,new DialogMsgAndPriceListener() {
            @Override
            public void getMessage(String message, int price) {
                createShareDialog();
            }
        });
    }

    public void createShareDialog()
    {
        dialog.createShareDialog(this,new DialogShareListener() {
            //0是微信，1是微博
            @Override
            public void select(int choice) {
               switch(choice)
               {
                   case 0:
                       Toast.makeText(SingASoneActivity.this,"微信",Toast.LENGTH_SHORT).show();
                       break;
                   case 1:
                       Toast.makeText(SingASoneActivity.this,"微博",Toast.LENGTH_SHORT).show();
                       break;

               }
            }
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        updateThread=null;
        lyricRunnable.setFlag(false);
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }
    public void setSelect(int choice)
    {
        switch (choice)
        {
            case 1:
                yuanshengImg.setImageDrawable(this.getResources().getDrawable(R.drawable.ys_1));
                zhangshaohanImg.setImageDrawable(this.getResources().getDrawable(R.drawable.wf));
                liyuchunImg.setImageDrawable(this.getResources().getDrawable(R.drawable.zjl));
                sudalvImg.setImageDrawable(this.getResources().getDrawable(R.drawable.zjl));
                chenyixunImg.setImageDrawable(this.getResources().getDrawable(R.drawable.cyx));
                break;
            case 2:
                yuanshengImg.setImageDrawable(this.getResources().getDrawable(R.drawable.ys));
                zhangshaohanImg.setImageDrawable(this.getResources().getDrawable(R.drawable.wf_1));
                liyuchunImg.setImageDrawable(this.getResources().getDrawable(R.drawable.zjl));
                sudalvImg.setImageDrawable(this.getResources().getDrawable(R.drawable.zjl));
                chenyixunImg.setImageDrawable(this.getResources().getDrawable(R.drawable.cyx));
                break;
            case 3:
                yuanshengImg.setImageDrawable(this.getResources().getDrawable(R.drawable.ys));
                zhangshaohanImg.setImageDrawable(this.getResources().getDrawable(R.drawable.wf));
                liyuchunImg.setImageDrawable(this.getResources().getDrawable(R.drawable.zjl_1));
                sudalvImg.setImageDrawable(this.getResources().getDrawable(R.drawable.zjl));
                chenyixunImg.setImageDrawable(this.getResources().getDrawable(R.drawable.cyx));
                break;
            case 4:
                yuanshengImg.setImageDrawable(this.getResources().getDrawable(R.drawable.ys));
                zhangshaohanImg.setImageDrawable(this.getResources().getDrawable(R.drawable.wf));
                liyuchunImg.setImageDrawable(this.getResources().getDrawable(R.drawable.zjl));
                sudalvImg.setImageDrawable(this.getResources().getDrawable(R.drawable.zjl_1));
                chenyixunImg.setImageDrawable(this.getResources().getDrawable(R.drawable.cyx));
                break;
            case 5:
                yuanshengImg.setImageDrawable(this.getResources().getDrawable(R.drawable.ys));
                zhangshaohanImg.setImageDrawable(this.getResources().getDrawable(R.drawable.wf));
                liyuchunImg.setImageDrawable(this.getResources().getDrawable(R.drawable.zjl));
                sudalvImg.setImageDrawable(this.getResources().getDrawable(R.drawable.zjl));
                chenyixunImg.setImageDrawable(this.getResources().getDrawable(R.drawable.cyx_1));
                break;
            case 6:
                wuxiaoguoImg.setImageDrawable(this.getResources().getDrawable(R.drawable.noy_1));
                juchangImg.setImageDrawable(this.getResources().getDrawable(R.drawable.jc));
                ktvImg.setImageDrawable(this.getResources().getDrawable(R.drawable.ktv));
                xianchangImg.setImageDrawable(this.getResources().getDrawable(R.drawable.xc));
                break;
            case 7:
                wuxiaoguoImg.setImageDrawable(this.getResources().getDrawable(R.drawable.noy));
                juchangImg.setImageDrawable(this.getResources().getDrawable(R.drawable.jc_1));
                ktvImg.setImageDrawable(this.getResources().getDrawable(R.drawable.ktv));
                xianchangImg.setImageDrawable(this.getResources().getDrawable(R.drawable.xc));
                break;
            case 8:
                wuxiaoguoImg.setImageDrawable(this.getResources().getDrawable(R.drawable.noy));
                juchangImg.setImageDrawable(this.getResources().getDrawable(R.drawable.jc));
                ktvImg.setImageDrawable(this.getResources().getDrawable(R.drawable.ktv_1));
                xianchangImg.setImageDrawable(this.getResources().getDrawable(R.drawable.xc));
                break;
            case 9:
                wuxiaoguoImg.setImageDrawable(this.getResources().getDrawable(R.drawable.noy));
                juchangImg.setImageDrawable(this.getResources().getDrawable(R.drawable.jc));
                ktvImg.setImageDrawable(this.getResources().getDrawable(R.drawable.ktv));
                xianchangImg.setImageDrawable(this.getResources().getDrawable(R.drawable.xc_1));
                break;
        }
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


            leftTextView = (TextView) v.findViewById(R.id.text_toolbar_left);
            leftTextView.setVisibility(View.GONE);


            rightTextView=(TextView) v.findViewById(R.id.text_right);

            rightTextView.setVisibility(View.GONE);

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
            while (flag) {
                try {
                    Thread.sleep(100);
                    if (mediaPlayer!=null&&mediaPlayer.isPlaying()) {
                        lyricView.setOffsetY(lyricView.getOffsetY() - lyricView.SpeedLrc());
                        lyricView.SelectIndex(mediaPlayer.getCurrentPosition());
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                        mHandler.post(mUpdateResults);
                    }
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

    @SuppressLint("NewApi")
    private void setupEqualizer(int choice) {
        mEqualizer = new Equalizer(0, mediaPlayer.getAudioSessionId());
        mEqualizer.setEnabled(true);

        short bands = mEqualizer.getNumberOfBands();  //获取频带数目  getNumberOfBands
        final short minEQLevel = mEqualizer.getBandLevelRange()[0];  //每个频带最小DB值 第1列 getBandLevelRange
        final short maxEQLevel = mEqualizer.getBandLevelRange()[1];  //每个频带最大DB值 第2列

        switch (choice){
            case 0:
                //mEqualizer.setBandLevel(0,15);
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }
}
