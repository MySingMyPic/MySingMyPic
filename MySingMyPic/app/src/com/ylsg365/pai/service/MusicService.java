package com.ylsg365.pai.service;


import java.io.IOException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.IBinder;
import android.speech.tts.Voice;

import com.ylsg365.pai.model.MusicPlayer;
import com.ylsg365.pai.util.LogUtil;


public class MusicService extends Service implements Runnable,
		MediaPlayer.OnCompletionListener {
	/* 定于一个多媒体对象 */
	public static MediaPlayer mMediaPlayer = null;
	// 用户操作
	public static int PLAY_ACTION;
	
	private MusicPlayer musicPlayer;
	
	private Voice currentVoice;

	/* 定义要播放的文件夹路径 */
	private static String MUSIC_PATH ;
	private static String MUSIC_PATH_TEMP=""; 

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		musicPlayer = new MusicPlayer();
		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		mMediaPlayer = new MediaPlayer();
		/* 监听播放是否完成 */
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); 
		mMediaPlayer.setOnBufferingUpdateListener(new MyOnBufferingUpdateListener());
		mMediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
		
//		flowerchangbroadcastreceiver=new FlowerChangBroadcastReceiver();
//		IntentFilter intentf=new IntentFilter("flowerchang");
//		registerReceiver(flowerchangbroadcastreceiver, intentf);
	}

//	class FlowerChangBroadcastReceiver extends BroadcastReceiver
//	{
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			currentVoice
//			
//		}
//		
//	}
	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	/* 启动service时执行的方法 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/* 得到从startService传来的动作，后是默认参数，这里是我自定义的常量 */
		if(null==intent)
			return super.onStartCommand(intent, flags, startId);
		PLAY_ACTION = intent.getIntExtra("MSG", IService.PlayerMag.PLAY_MAG);
		MUSIC_PATH = intent.getStringExtra("url");
		if (PLAY_ACTION == IService.PlayerMag.PLAY_MAG) {
			if(!MUSIC_PATH.equals(MUSIC_PATH_TEMP))
			{
				currentVoice = (Voice)intent.getSerializableExtra("voice");
				playMusic();
				MUSIC_PATH_TEMP=MUSIC_PATH;
			}
			else if(musicPlayer.getPlayStatus() == MusicPlayer.STATUS_PAUSE)
			{
				musicPlayer.setPlayStatus(MusicPlayer.STATUS_PLAYING);
				if(null!=mMediaPlayer)
				{
					mMediaPlayer.start();
					new Thread(this).start();
				}
			}
			if(musicPlayer.getPlayStatus() == MusicPlayer.STATUS_END)
			{
				currentVoice = (Voice)intent.getSerializableExtra("voice");
				playMusic();
				MUSIC_PATH_TEMP=MUSIC_PATH;
				
			}
		}
		if (PLAY_ACTION == IService.PlayerMag.PAUSE) {
			if(mMediaPlayer!=null)
			{
				if (mMediaPlayer.isPlaying()) {//正在播放
					mMediaPlayer.pause();// 暂停
					Intent pauseIntent = new Intent();
					pauseIntent.setAction("playerInfo");
					musicPlayer.setPlayStatus(MusicPlayer.STATUS_PAUSE);
					pauseIntent.putExtra("player", musicPlayer);
					
					sendBroadcast(pauseIntent);
				} else {// 没有播放
					mMediaPlayer.start();
				}
				
			}
		}
		
		if(PLAY_ACTION == IService.PlayerMag.PLAYER_INIT) {
			currentVoice = (Voice)intent.getSerializableExtra("voice");
			Intent initIntent = new Intent();
			initIntent.setAction("playerInfo");
			initIntent.putExtra("action", 3);
			initIntent.putExtra("voice", currentVoice);
			initIntent.putExtra("player", musicPlayer);
			
			sendBroadcast(intent);
		}
		if(PLAY_ACTION==IService.PlayerMag.SENDFLOWER||PLAY_ACTION==IService.PlayerMag.STORE||PLAY_ACTION==IService.PlayerMag.FRIEND)
		{
			currentVoice = (Voice)intent.getSerializableExtra("voice");
			Intent initIntent = new Intent();
			initIntent.setAction("playerInfo");
			initIntent.putExtra("action", 3);
			initIntent.putExtra("voice", currentVoice);
			initIntent.putExtra("player", musicPlayer);
			sendBroadcast(intent);
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	
	private Voice lastVoice;
	public void playMusic() {
		try {
			if(null==mMediaPlayer)
			{
				mMediaPlayer = new MediaPlayer();
				/* 监听播放是否完成 */
				mMediaPlayer.setOnCompletionListener(this);
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); 
				mMediaPlayer.setOnBufferingUpdateListener(new MyOnBufferingUpdateListener());
				mMediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
				
			}
			if(mMediaPlayer.isPlaying())
			{
				mMediaPlayer.pause();
				mMediaPlayer.stop();
			}
			/* 重置多媒体 */
			mMediaPlayer.reset();
			/* 读取mp3文件 */
			mMediaPlayer.setDataSource(MUSIC_PATH);
LogUtil.logd("MusicService", "读取网络视频的地址：" + MUSIC_PATH);
			/* 准备播放 */
			new AsyncTask<Object, Object, Object>()
			{

				@Override
				protected Object doInBackground(Object... params) {
					try{
//						if(api.comfirmInternet(MUSIC_PATH))
						mMediaPlayer.prepare();
						}catch(Exception e){
							LogUtil.logd("mMediaPlayer", "在准备的时候就报异常了！！！！！！！！！！！！" + e.toString());
							mMediaPlayer.stop();
							mMediaPlayer.release();
							mMediaPlayer = null;
							stopthisService();
						}
					return null;
				}
				protected void onPostExecute(Object result) {
					if(null!=mMediaPlayer)
					{
						/* 开始播放 */
						mMediaPlayer.start();
						/* 是否单曲循环 */
//					mMediaPlayer.setLooping(isLoop);
						// 设置进度条最大值
						LogUtil.logd("MusicService", "长度：" + mMediaPlayer.getDuration());
						musicPlayer.setDuration(mMediaPlayer.getDuration());
						musicPlayer.setPlayStatus(MusicPlayer.STATUS_PLAYING);
						Intent intent = new Intent();
						intent.setAction("playerInfo");
						intent.putExtra("action", 1);
						intent.putExtra("voice", currentVoice);
						intent.putExtra("player", musicPlayer);
						sendBroadcast(intent);
						new Thread(MusicService.this).start();
						lastVoice = currentVoice;
					}
				};
			}.execute();
		} catch (IOException e) {
			LogUtil.logd("mMediaPlayer", "在播放的时候就报异常了！！！！！！！！！！！！"+e.toString());
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			stopthisService();
		}
	}

	private void stopthisService()
	{
		this.stopSelf();
	}
	/**
	 * 音频的准备好的时候就播放
	 * @author： qubian
	 * @Time： 2014-1-8
	 * @Email： naibbian@163.com 
	 *
	 */
	class MyOnPreparedListener implements OnPreparedListener
	{
		@Override
		public void onPrepared(MediaPlayer mp) {
			mMediaPlayer.start();
		}
		
	}
	int SecondaryProgress;
	/**
	 * MediaPlayer 的缓冲长度
	 * @author： qubian
	 * @Time： 2014-1-8
	 * @Email： naibbian@163.com 
	 *
	 */
	class MyOnBufferingUpdateListener implements OnBufferingUpdateListener
	{

		@Override
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			SecondaryProgress=percent;
		}
		
	}
	// 刷新进度条
	@Override
	public void run() {
		if(mMediaPlayer != null)
		{
			int CurrentPosition = 0;// 设置默认进度条当前位置\
			int total = mMediaPlayer.getDuration();//
			
			while (mMediaPlayer != null && CurrentPosition <= total && musicPlayer.getPlayStatus() == MusicPlayer.STATUS_PLAYING) {
				try {
					Thread.sleep(1000);
					if (mMediaPlayer != null) {
						if(!mMediaPlayer.isPlaying())
						{
							return;
						}
						CurrentPosition = mMediaPlayer.getCurrentPosition();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				musicPlayer.setCurrentPostion(CurrentPosition);
				Intent intent = new Intent();
				intent.setAction("playerInfo");
				
				intent.putExtra("action", 2);
				intent.putExtra("voice", currentVoice);
				intent.putExtra("player", musicPlayer);
				intent.putExtra("SecondaryProgress", SecondaryProgress);
				sendBroadcast(intent);
			}
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		musicPlayer.setPlayStatus(MusicPlayer.STATUS_END);
		Intent intent = new Intent();
		intent.setAction("playerInfo");
		intent.putExtra("action", 3);
		intent.putExtra("voice", currentVoice);
		intent.putExtra("player", musicPlayer);
		
		sendBroadcast(intent);
	}
	
	

}