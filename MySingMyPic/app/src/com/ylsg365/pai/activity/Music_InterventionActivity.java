package com.ylsg365.pai.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.view.lrcView.DefaultLrcParser;
import com.ylsg365.pai.activity.view.lrcView.LrcRow;
import com.ylsg365.pai.activity.view.lrcView.LrcView;
import com.ylsg365.pai.util.CommonAdapter;
import com.ylsg365.pai.util.ViewHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 音乐调节
 */
public class Music_InterventionActivity extends BaseActivity {
    private GridView mGridView;
    private List<Map<String, String>> datas;
    private ImageButton play;
    private SeekBar mSeekBar;
    private LrcView mLrcView;
    private MediaPlayer mPlayer;
    private Toast mPlayerToast;
    private Toast mLrcToast;
    private TextView currentTime,totleTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music__intervention);

        setupToolbar();

        setTitle("画沙");

        mGridView = (GridView) findViewById(R.id.gridview);
        play = (ImageButton) findViewById(R.id.play);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mLrcView = (LrcView) findViewById(R.id.lrcView);
        currentTime = (TextView) findViewById(R.id.currentTime);
        totleTime = (TextView) findViewById(R.id.totleTime);

        mPlayer = MediaPlayer.create(this, R.raw.huasha);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                play.setImageResource(R.drawable.btn_play_p);
                play.setTag("play");
                mLrcView.reset();
                handler.removeMessages(0);
                mSeekBar.setProgress(0);
            }
        });

        totleTime.setText(formatTimeFromProgress(mPlayer.getDuration()));
        currentTime.setText("00:00");

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("play".equals(v.getTag())){
                    mPlayer.start();
                    mLrcView.setLrcRows(getLrcRows());
                    handler.sendEmptyMessage(0);
                    play.setImageResource(R.drawable.btn_play_n);
                    play.setTag("pause");
                }else{
                    if(mPlayer.isPlaying()){
                        mPlayer.pause();
                        play.setImageResource(R.drawable.btn_play_p);
                        play.setTag("play");
                    }else{
                        mPlayer.start();
                        play.setImageResource(R.drawable.btn_play_n);
                        play.setTag("pause");
                    }
                }
            }
        });

        mLrcView.setOnSeekToListener(new LrcView.OnSeekToListener() {

            @Override
            public void onSeekTo(int progress) {
                mPlayer.seekTo(progress);

            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(seekBar == mSeekBar){
                    mPlayer.seekTo(seekBar.getProgress());
                    handler.sendEmptyMessageDelayed(0, 100);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(seekBar == mSeekBar){
                    handler.removeMessages(0);
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if(seekBar == mSeekBar){
                    mLrcView.seekTo(progress, true,fromUser);
                    if(fromUser){
                        showPlayerToast(formatTimeFromProgress(progress));
                    }
                }
            }

        });


        datas = new ArrayList<Map<String, String>>();
        datas.add(new HashMap<String, String>());
        datas.add(new HashMap<String, String>());
        datas.add(new HashMap<String, String>());
        datas.add(new HashMap<String, String>());
        datas.add(new HashMap<String, String>());
        datas.add(new HashMap<String, String>());
        datas.add(new HashMap<String, String>());
        datas.add(new HashMap<String, String>());
        mGridView.setAdapter(new CommonAdapter(Music_InterventionActivity.this, datas, R.layout.item_music_intervertion) {

            @Override
            public void convert(ViewHolder holder, Object item) {
                holder.setText(R.id.txt, "原唱");
//                ImageLoader.getInstance().displayImage("drawable://" +R.drawable.img_ktv_room_default, (ImageView) holder.getView(R.id.img));
            }
        });
    }

    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            mSeekBar.setMax(mPlayer.getDuration());
            mSeekBar.setProgress(mPlayer.getCurrentPosition());
            handler.sendEmptyMessageDelayed(0, 100);
            currentTime.setText(formatTimeFromProgress(mPlayer.getCurrentPosition()));
        };
    };

    /**
     * 将播放进度的毫米数转换成时间格式
     * 如 3000 --> 00:03
     * @param progress
     * @return
     */
    private String  formatTimeFromProgress(int progress){
        //总的秒数
        int msecTotal = progress/1000;
        int min = msecTotal/60;
        int msec = msecTotal%60;
        String minStr = min < 10 ? "0"+min:""+min;
        String msecStr = msec < 10 ? "0"+msec:""+msec;
        return minStr+":"+msecStr;
    }
    /**
     * 获取歌词List集合
     * @return
     */
    private List<LrcRow> getLrcRows(){
        List<LrcRow> rows = null;
        InputStream is = getResources().openRawResource(R.raw.hs);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line ;
        StringBuffer sb = new StringBuffer();
        try {
            while((line = br.readLine()) != null){
                sb.append(line+"\n");
            }
            System.out.println(sb.toString());
            rows = DefaultLrcParser.getIstance().getLrcRows(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rows;
    }

    private TextView mPlayerToastTv;
    private void showPlayerToast(String text){
        if(mPlayerToast == null){
            mPlayerToast = new Toast(this);
            mPlayerToastTv = (TextView) LayoutInflater.from(this).inflate(R.layout.toast, null);
            mPlayerToast.setView(mPlayerToastTv);
            mPlayerToast.setDuration(Toast.LENGTH_SHORT);
        }
        mPlayerToastTv.setText(text);
        mPlayerToast.show();
    }
    private TextView mLrcToastTv;
    private void showLrcToast(String text){
        if(mLrcToast == null){
            mLrcToast = new Toast(this);
            mLrcToastTv = (TextView) LayoutInflater.from(this).inflate(R.layout.toast, null);
            mLrcToast.setView(mLrcToastTv);
            mLrcToast.setDuration(Toast.LENGTH_SHORT);
        }
        mLrcToastTv.setText(text);
        mLrcToast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        mLrcView.reset();
    }

}
