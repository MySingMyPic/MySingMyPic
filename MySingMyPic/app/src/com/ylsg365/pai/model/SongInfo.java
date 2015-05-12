package com.ylsg365.pai.model;

/**
 * Created by lanzhihong on 2015/4/10.
 */
public class SongInfo {
    private String singerName;
    private String uploadTime;
    private String originalSongUrl;
    private String crlFile;
    private String songUrl;
    private String songName;


    public String getSingerName() {
        return singerName;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public String getOriginalSongUrl() {
        return originalSongUrl;
    }


    public String getCrlFile() {
        return crlFile;
    }

    public String getSongUrl() {
        return songUrl;
    }


    public String getSongName() {
        return songName;
    }




    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public void setOriginalSongUrl(String originalSongUrl) {
        this.originalSongUrl = originalSongUrl;
    }


    public void setCrlFile(String crlFile) {
        this.crlFile = crlFile;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }


    public void setSongName(String songName) {
        this.songName = songName;
    }


    @Override
    public String toString() {
        return "SongInfo{" +
                ", singerName='" + singerName + '\'' +
                ", uploadTime='" + uploadTime + '\'' +
                ", originalSongUrl='" + originalSongUrl + '\'' +
                ", crlFile='" + crlFile + '\'' +
                ", songUrl='" + songUrl + '\'' +
                ", songName='" + songName + '\'' +
                '}';
    }
}
