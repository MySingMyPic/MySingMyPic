package com.ylsg365.pai.activity.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.Voice;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.Listener.OnItemClickListener;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.base.TabFragment;
import com.ylsg365.pai.activity.newsinfo.InfoCommentFragment;
import com.ylsg365.pai.activity.newsinfo.InfoLikeFragment;
import com.ylsg365.pai.activity.newsinfo.NewsInfoForwardFragment;
import com.ylsg365.pai.activity.view.SlidingTabLayout;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.model.MusicPlayer;
import com.ylsg365.pai.service.IService;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONObject;

import java.util.ArrayList;

public class VideoDetalActivity extends BaseActivity implements OnItemClickListener, View.OnClickListener {
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ArrayList<TabFragment> fragments;
    private ViewPager_Adapter viewPager_Adapter;
    private JSONObject newsInfoObj;
    private TextView nickNameTextView;
    public ImageView userHeadImageview;

    private int forwardCount;
    private int commentCount;
    private String nickName;
    private int likeCount;
    private String songName;
    private String headImg;

    private View playView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mv_detail);

        newsInfoObj = JsonUtil.getJSONObject(getIntent().getStringExtra("info"));

        int recordId = JsonUtil.getInt(newsInfoObj, "nid");
        nickName = JsonUtil.getString(newsInfoObj, "nickName");

        forwardCount = JsonUtil.getInt(newsInfoObj, "forwardCount");
        commentCount = JsonUtil.getInt(newsInfoObj, "commentCount");
        likeCount = JsonUtil.getInt(newsInfoObj, "niceCount");
        songName = JsonUtil.getString(newsInfoObj, "songName");
        headImg = JsonUtil.getString(newsInfoObj, "headImg");

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.demo_tab);
        viewPager = (ViewPager) findViewById(R.id.pager_fresh);

        // 设置ViewPager
        fragments = new ArrayList<TabFragment>();
        fragments.add(NewsInfoForwardFragment.newInstance(recordId, forwardCount));
        fragments.add(InfoCommentFragment.newInstance(recordId, commentCount));
        fragments.add(InfoLikeFragment.newInstance(recordId, likeCount));
        viewPager_Adapter = new ViewPager_Adapter(getSupportFragmentManager(),
                fragments);
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setAdapter(viewPager_Adapter);

        // 设置SlidingTab
        slidingTabLayout.setViewPager(viewPager);




        setupToolbar();
        setTitle(songName);

        init();

        getMvDetail(recordId);
    }


    public void getMvDetail(int recordId) {
        YinApi.getMvDetail(recordId, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getMvDetail", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
//                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "data");
//                    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
//                    for (int i = 0; i < infoJsonArray.length(); i++) {
//                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
//                    }
//                    newsInfoImgAdapter = new NewsInfoImgAdapter(R.layout.item_info_img, infoList);
//                    newsInfoImgAdapter.setOnItemClickListener(VideoDetalActivity.this);
//                    infoImageRecyclerView.setAdapter(newsInfoImgAdapter);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    protected void loadViews() {
        nickNameTextView = (TextView) findViewById(R.id.text_nickName);
        userHeadImageview = (ImageView) findViewById(R.id.img_headImg);
        playView = findViewById(R.id.layout_play);
    }

    @Override
    protected void initViews() {
        nickNameTextView.setText(nickName);
        ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + headImg, userHeadImageview);
    }

    @Override
    protected void setupListeners() {
        playView.setOnClickListener(this);

    }

    @Override
    public void onItemClick(View view, int postion) {

    }


    public class ViewPager_Adapter extends FragmentPagerAdapter {

        private ArrayList<TabFragment> fragments;

        public ViewPager_Adapter(FragmentManager fm, ArrayList<TabFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public TabFragment getItem(int pos) {
            return fragments.get(pos);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return String.format("%s%d", getItem(position).getTitle(), forwardCount);
            }
            if (position == 1) {
                return String.format("%s%d", getItem(position).getTitle(), commentCount);
            }
            if (position == 2) {
                return String.format("%s%d", getItem(position).getTitle(), likeCount);
            }
            return getItem(position).getTitle();
        }
    }

    // 音乐
    private String musicUrl;
    private SeekBar seekBar;

    private boolean isplaying = true;

    private MusicServiceBroadcastReceiver musicServiceBroadcastReceiver;
    private IntentFilter intentFilter;
    private boolean isMusicServiceBroadcastReceiverRegister = false;
    // 启动服务
    public void playMusic() {
        try {
//            ib_pause.setBackgroundResource(R.drawable.suspend_selector);
            if (!isMusicServiceBroadcastReceiverRegister) {
                registerReceiver(musicServiceBroadcastReceiver, intentFilter);
                isMusicServiceBroadcastReceiverRegister = true;
            }
//			seekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());

            final Intent intent = new Intent();
            intent.putExtra("url", musicUrl);
            intent.putExtra("PLAY_ACTION", IService.PlayerMag.PLAY_MAG);
//			intent.setClass(VoiceDetailActivity.this, MusicService.class);
            intent.setAction("android.intent.action.MusicService");
            new PlayAsynTask().execute(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class PlayAsynTask extends AsyncTask<Intent, Integer, Voice> {

        @Override
        protected Voice doInBackground(Intent... params) {
            startService(params[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Voice result) {

            super.onPostExecute(result);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isMusicServiceBroadcastReceiverRegister) {
            unregisterReceiver(musicServiceBroadcastReceiver);
        }
    }

    class MusicServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            int action = intent.getIntExtra("action", -1);
            MusicPlayer musicPlayer = (MusicPlayer) intent
                    .getSerializableExtra("player");
            int secondaryProgress=intent.getIntExtra("SecondaryProgress", 1);

            switch (action) {
                case 1:

                    seekBar.setMax(musicPlayer.getDuration());
                    break;
                case 2:
                    seekBar.setMax(musicPlayer.getDuration());
                    seekBar.setProgress(musicPlayer.getCurrentPostion());
                    seekBar.setSecondaryProgress(secondaryProgress);
                    break;
            }

            if (musicPlayer.getPlayStatus() == MusicPlayer.STATUS_PLAYING) {
//				if(!isgetlistener)
                {
//                    seekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
//                    isgetlistener=true;
                }
                isplaying = false;
//                ib_pause.setBackgroundResource(R.drawable.suspend_selector);
            } else if (musicPlayer.getPlayStatus() == MusicPlayer.STATUS_PAUSE) {
//                ib_pause.setBackgroundResource(R.drawable.play_selector);
                isplaying = true;
            } else if (musicPlayer.getPlayStatus() == MusicPlayer.STATUS_END) {
//                ib_pause.setBackgroundResource(R.drawable.play_selector);
                seekBar.setProgress(-1);
                isplaying = true;
            }
        }
    }

    @Override
    public void onClick(View v) {
        playMusic();
    }
}
