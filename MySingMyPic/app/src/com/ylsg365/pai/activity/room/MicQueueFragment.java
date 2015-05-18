package com.ylsg365.pai.activity.room;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.TabFragment;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

public class MicQueueFragment extends TabFragment implements
        AbsListView.OnItemClickListener, OnCompletionListener {
    private MicQueueAdapter mAdapter;
    private String nid;
    private TextView mModelTV;
    private Player mPlayer = null;

    // Rename and change types of parameters
    public static MicQueueFragment newInstance(String param1, String param2) {
        MicQueueFragment fragment = new MicQueueFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MicQueueFragment() {
    }

    public MicQueueFragment(String nid) {
        this.nid = nid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mic_queue,
                container, false);

        mModelTV = (TextView) rootView.findViewById(R.id.tv_model);
        RecyclerView recyclerView = (RecyclerView) rootView
                .findViewById(R.id.recycler_user);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            Paint paint = new Paint();

            @Override
            public void onDraw(Canvas c, RecyclerView parent,
                    RecyclerView.State state) {
                super.onDraw(c, parent, state);
                super.onDrawOver(c, parent, state);
                paint.setColor(getResources().getColor(R.color.line_radio));
                for (int i = 0, size = parent.getChildCount(); i < size; i++) {
                    View child = parent.getChildAt(i);
                    c.drawLine(child.getLeft() + 20, child.getBottom(),
                            child.getRight(), child.getBottom(), paint);
                }
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent,
                    RecyclerView.State state) {

            }

            @Override
            public void getItemOffsets(Rect outRect, View view,
                    RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        });

        mAdapter = new MicQueueAdapter();
        recyclerView.setAdapter(mAdapter);

        getMicQueueList();

        return rootView;
    }

    public synchronized void refresh() {
        getMicQueueList();
    }

    private void getMicQueueList() {
        YinApi.getHouseSing(nid, 0, new Response.Listener<String>() {
            @Override
            public void onResponse(String res) {
                LogUtil.logd("getHouseSing", res);
                JSONObject response = null;
                try {
                    response = new JSONObject(res);
                } catch (JSONException e) {
                }
                if (response != null && JsonUtil.getBoolean(response, "status")) {
                    List<JSONObject> infoList = mAdapter.getList();
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response,
                            "houses");
                    int length = infoJsonArray.length();
                    for (int i = 0; i < length; i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }
                    mAdapter.notifyDataSetChanged();
                    if (!infoList.isEmpty()) {
                        startPlay();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    private void startPlay() {
        if (mPlayer == null) {
            mPlayer = new Player(null);
            mPlayer.setOnCompletionListener(this);
        }
        if (!mPlayer.isPlaying()) {
            List<JSONObject> infoList = mAdapter.getList();
            String url = Constants.SITE_DOMAIN
                    + JsonUtil.getString(infoList.get(0), "recordUrl");
            mPlayer.playUrl(url);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        List<JSONObject> infoList = mAdapter.getList();
        if (!infoList.isEmpty()) {
            infoList.remove(0);
            mAdapter.notifyDataSetChanged();
            if (!infoList.isEmpty()) {
                startPlay();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
    }

    @Override
    public String getTitle() {
        return "排麦";
    }

    class Player implements OnBufferingUpdateListener,
            MediaPlayer.OnPreparedListener {
        public MediaPlayer mMediaPlayer;
        private SeekBar mProgressSB;
        private boolean mIsPause = false;
        private Timer mTimer = new Timer();
        private TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mMediaPlayer == null)
                    return;
                if (mMediaPlayer.isPlaying()
                        && mProgressSB.isPressed() == false) {
                    handleProgress.sendEmptyMessage(0);
                }
            }
        };

        @SuppressLint("HandlerLeak")
        private Handler handleProgress = new Handler() {
            public void handleMessage(Message msg) {

                int position = mMediaPlayer.getCurrentPosition();
                int duration = mMediaPlayer.getDuration();

                if (duration > 0 && mProgressSB != null) {
                    long pos = mProgressSB.getMax() * position / duration;
                    mProgressSB.setProgress((int) pos);
                }
            };
        };

        public Player(SeekBar skbProgress) {
            this.mProgressSB = skbProgress;
            try {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnBufferingUpdateListener(this);
                mMediaPlayer.setOnPreparedListener(this);
            } catch (Exception e) {
            }
            mTimer.schedule(mTimerTask, 0, 1000);
        }

        public void setOnCompletionListener(OnCompletionListener l) {
            mMediaPlayer.setOnCompletionListener(l);
        }

        public void play() {
            mMediaPlayer.start();
        }

        public void playUrl(String videoUrl) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(videoUrl);
                mMediaPlayer.prepare();// prepare之后自动播放
                // mediaPlayer.start();
            } catch (IllegalArgumentException e) {
            } catch (IllegalStateException e) {
            } catch (IOException e) {
            }
        }

        public void pause() {
            mMediaPlayer.pause();
            mIsPause = true;
        }

        public void resume() {
            if (mMediaPlayer != null && mIsPause) {
                mIsPause = false;
                mMediaPlayer.start();
            }
        }

        public boolean isPlaying() {
            if (mMediaPlayer == null) {
                return false;
            } else {
                return mMediaPlayer.isPlaying();
            }
        }

        public void stop() {
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }

        @Override
        /**  
         * 通过onPrepared播放  
         */
        public void onPrepared(MediaPlayer arg0) {
            mMediaPlayer.start();
        }

        @Override
        public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) {
            if (mProgressSB == null) {
                return;
            }
            mProgressSB.setSecondaryProgress(bufferingProgress);
            int currentProgress = mProgressSB.getMax()
                    * mMediaPlayer.getCurrentPosition()
                    / mMediaPlayer.getDuration();
            Log.e(currentProgress + "% play", bufferingProgress + "% buffer");
        }
    }
}
