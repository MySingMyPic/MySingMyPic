package com.ylsg365.pai.model;

import java.io.Serializable;

public class MusicPlayer implements Serializable{
	private static final long serialVersionUID = 1814566050666985720L;

	private int playStatus = -1;
	
	public static final int STATUS_START = 1;
	public static final int STATUS_PLAYING = 2;
	public static final int STATUS_PAUSE = 3;
	public static final int STATUS_END = 4;
	public static final int STATUS_ERROR = 5;
	
	private String userName;
	
	private String singerName;
	
	private String songName;
	
	private int duration;
	
	private int currentPostion;

	public int getPlayStatus() {
		return playStatus;
	}

	public void setPlayStatus(int playStatus) {
		this.playStatus = playStatus;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSingerName() {
		return singerName;
	}

	public void setSingerName(String singerName) {
		this.singerName = singerName;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getCurrentPostion() {
		return currentPostion;
	}

	public void setCurrentPostion(int currentPostion) {
		this.currentPostion = currentPostion;
	}


}
