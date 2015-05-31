package com.ylsg365.pai.model;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;

public class Player implements OnBufferingUpdateListener,
        MediaPlayer.OnPreparedListener {
    public MediaPlayer mMediaPlayer;
    private SeekBar mProgressSB;
    private boolean mStartWhenPrepared = true;
    private boolean mIsPause = false;
    private Timer mTimer;
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if (mMediaPlayer == null)
                return;
            if (mMediaPlayer.isPlaying() && mProgressSB.isPressed() == false) {
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
    }

    public void setOnCompletionListener(OnCompletionListener l) {
        mMediaPlayer.setOnCompletionListener(l);
    }

    public void play() {
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.start();
    }

    public void playUrl(String url) {
        playUrl(url, true);
    }

    public void playUrl(String url, boolean startWhenPrepared) {
        mStartWhenPrepared = startWhenPrepared;
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
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
        stopTimer();
    }

    public void resume() {
        if (mMediaPlayer != null && mIsPause) {
            mIsPause = false;
            mMediaPlayer.start();
            startTimer();
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
        stopTimer();
    }

    @Override
    /**  
     * 通过onPrepared播放  
     */
    public void onPrepared(MediaPlayer arg0) {
        if (mStartWhenPrepared) {
            mMediaPlayer.start();
            startTimer();
        }
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

    private void startTimer() {
        if (mProgressSB != null) {
            mTimer = new Timer();
            mTimer.schedule(mTimerTask, 0, 1000);
        }
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }
}
