package com.ylsg365.pai.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.AsyncTask;
import android.os.Handler;

import com.ylsg365.pai.util.HttpMethodHelper;
import com.ylsg365.pai.util.URLUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class RecordManager {

    public MediaPlayer player = null;
    public MediaRecorder recorder = null;
    public Recorder rec = null;

    private Context context;
    private DownloadTask lastDownloadTask;
    private UploadTask lastUploadTask;
    private Handler recordHandler = new Handler();
    private Runnable runnable = null;
    private boolean recording = false;
    private boolean downloading = false;
    private boolean uploading = false;
    private PlayListener lastPlayListener;

    private int limit = 0;
    private int maxSec = 0;
    private int duration = 0;
    private long startTime = 0;
    private long endTime = 0;

    private int base = 600;
    private int space = 16;
    public final static int AUDIO_SAMPLE_RATE = 44100;  //44.1KHz,普遍使用的频率
    public final static int AUDIO_ENCODE_RATE = 128000;
    private String lastPlayName;

    private RecordListener listener = null;

    public RecordManager(Context context, int limit) {
        this.context = context;
        this.limit = limit;
        this.maxSec = limit / 1000;
        //registerHeadsetReceiver();
    }

    public RecordManager(Context context) {
        this(context, 3 * 60 * 1000);
    }

    public void record(String path, String name, RecordListener lis) {

        stopPlay();

        rec = new Recorder();
        rec.path = path;
        rec.name = name;

        duration = 0;
        listener = lis;

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
        recorder.setAudioEncodingBitRate(AUDIO_ENCODE_RATE);
        recorder.setAudioSamplingRate(AUDIO_SAMPLE_RATE);
        recorder.setAudioChannels(1);
        recorder.setMaxDuration(limit);
        recorder.setOutputFile(rec.path);
        recorder.setOnInfoListener(new OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    stopRecord();
                }
            }
        });

        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            recorder.reset();
            return;
        }

        startTime = System.currentTimeMillis();
        recording = true;

        runnable = new Runnable() {
            @Override
            public void run() {
                if (recording && duration < maxSec) {
                    updateMicStatus();
                    rec.mills = System.currentTimeMillis() - startTime;
                    rec.duration = (int) Math.ceil(rec.mills / 1000);
                    listener.record(rec);
                }
            }
        };

        recordHandler.post(runnable);
        recorder.start();
    }

    public void updateMicStatus() {

        if (recorder == null || !recording) {
            return;
        }

        int ratio = recorder.getMaxAmplitude() / base;
        int db = 0;

        if (ratio > 1) {
            db = (int) (20 * Math.log10(ratio));
        }

        rec.db = db;
        recordHandler.postDelayed(runnable, space);
    }

    public void stopRecord() {
        if (recorder == null || !recording) {
            return;
        }

        endTime = System.currentTimeMillis();
        recording = false;
        recorder.stop();
        recorder.release();
        rec.mills = endTime - startTime;
        rec.duration = rec.mills / 1000;
        rec.db = 0;
        recordHandler.removeCallbacks(runnable);
        listener.stop(rec);
    }

    public Recorder getRecorder() {
        return rec;
    }

    public void reset() {
        recorder = null;
        rec = null;
    }

    public void play(String fileUrl, String downloadDir, final PlayListener mpl) {
        String name = "", path = fileUrl;
        boolean isUrlPath = fileUrl.indexOf("http://") == 0;

        //如果是网络数据
        if (isUrlPath) {
            name = getNameFromUri(fileUrl);
            path = downloadDir + "/" + name;
        }

        //如果本地有文件就直接播放
        if (exists(path)) {
            playRecord(path, mpl);
            //保存最后一次播放路径
            lastPlayName = name;
        } else {

            if (isUrlPath) {
                //在未下载完就尝试下载其他音频，直接取消在执行的任务
                if (name != lastPlayName && lastDownloadTask != null && !lastDownloadTask.isCancelled()) {
                    cancelDownload();
                }
                mpl.download();
                lastPlayName = name;
                lastDownloadTask = new DownloadTask(fileUrl, downloadDir, new AsyncListener() {
                    @Override
                    public void onComplete(String result) {
                        //异步下载与期待播放不是同一个，取消立即播放
                        if (lastDownloadTask.name.equals(lastPlayName)) {
                            playRecord(result, mpl);
                        }
                    }

                    @Override
                    public void onExecute() {
                    }

                    @Override
                    public void onExecute(String response) {
                    }

                    @Override
                    public void onProgress(Void... values) {
                    }

                    @Override
                    public void onError(String message) {
                        mpl.error();
                    }
                });
                lastDownloadTask.execute();
            } else {
                mpl.error();
            }
        }
    }

    public void playRecord(String path, final PlayListener mpl) {

        stopPlay();

        lastPlayListener = mpl;
        player = new MediaPlayer();

        try {
            player.setDataSource(path);
            player.prepareAsync();
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mp.release();
                    mpl.stop();
                    return false;
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mpl.stop();
                }
            });
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mp != null) {
                        player.start();
                        mpl.start();
                    }
                }
            });
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        if (isPlaying()) {
            player.reset();
            player.release();
        }
        if (lastPlayListener != null) {
            lastPlayListener.stop();
        }
        player = null;
    }

    public boolean isPlaying() {
        try {
            if (player != null && player.isPlaying()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isDownloading() {
        return downloading;
    }

    public boolean isUploading() {
        return uploading;
    }

    public boolean isRecording() {
        return recording;
    }

    public AsyncTask<Void, Void, String> upload(int id, int type, String content, AsyncListener lis) {
        if (getRecorder() != null) {
            if (new File(getRecorder().path).exists()) {
                lastUploadTask = new UploadTask(id, type, content, lis);
                lastUploadTask.execute();
                return lastUploadTask;
            } else {
                lis.onError("录音文件已丢失");
                return null;
            }
        }
        lis.onError("还没有创建录音");
        return null;
    }

    private class UploadTask extends AsyncTask<Void, Void, String> {

        public AsyncListener lis;
        public int objectId;
        public int objectType;
        public String content;

        public UploadTask(int id, int type, String content, AsyncListener lis) {
            this.lis = lis;
            this.content = content;
            objectId = id;
            objectType = type;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            lis.onExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            uploading = true;
            Recorder rec = getRecorder();

            HttpClient client = new DefaultHttpClient();
            HttpParams params2 = client.getParams();
            HttpConnectionParams.setSoTimeout(params2, 10 * 1000);
            HttpConnectionParams.setConnectionTimeout(params2, 10 * 1000);
            HttpConnectionParams.setTcpNoDelay(params2, true);

//            URLUtils.
//
//            HttpPost httppost = HttpMethodHelper.getHttpPost("/reviewvoice/add/" + objectId + "/" + objectType);
//            MultipartEntity mEntity = new MultipartEntity();
//            FileBody fileBody = new FileBody(new File(rec.path));
//            mEntity.addPart("uploadfile", fileBody);
//
//            try {
//                mEntity.addPart("fileUrl", new StringBody(rec.name));
//                mEntity.addPart("time", new StringBody(rec.duration + ""));
//                mEntity.addPart("content", new StringBody(content, Charset.forName("UTF-8")));
//
//                httppost.setEntity(mEntity);
//                try {
//                    HttpResponse execute = client.execute(httppost);
//                    HttpEntity entity2 = execute.getEntity();
//                    String str = EntityUtils.toString(entity2);
//                    entity2.consumeContent();
//                    return str;
//                } catch (ClientProtocolException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            lis.onComplete(result);
            uploading = false;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            lis.onProgress(values);
        }
    }

    private class DownloadTask extends AsyncTask<Void, Void, String> {

        private AsyncListener lis;
        private String mUrl;
        public String name;
        private String downloadDir;
        private HttpURLConnection urlConn;

        DownloadTask(String url, String downloadDir, AsyncListener lis) {
            mUrl = url;
            this.downloadDir = downloadDir;
            this.lis = lis;
        }

        @Override
        protected String doInBackground(Void... params) {
            OutputStream output = null;
            name = getNameFromUri(mUrl);
            String path = downloadDir + "/" + name;
            downloading = true;
            try {
                URL url = new URL(mUrl + "?_=" + (int) (Math.random() * 100000));
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.connect();
                InputStream input = urlConn.getInputStream();
                File file = new File(path);

                if (file.exists()) {
                    file.delete();
                }

                file.createNewFile();

                output = new FileOutputStream(file);
                byte buffer[] = new byte[1024];
                int inputSize = -1;
                while ((inputSize = input.read(buffer)) != -1) {
                    output.write(buffer, 0, inputSize);
                }
                output.flush();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    output.close();
                    return path;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            lis.onComplete(result);
            downloading = false;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            downloading = false;
            urlConn.disconnect();
        }
    }

    public void cancelDownload() {
        if (lastDownloadTask != null && !lastDownloadTask.isCancelled()) {
            lastDownloadTask.cancel(true);
            downloading = false;
        }
        if (lastPlayListener != null) {
            lastPlayListener.stop();
        }
    }

    public void cancelUpload() {
        if (lastUploadTask != null && !lastUploadTask.isCancelled()) {
            lastUploadTask.cancel(true);
            uploading = false;
        }
    }

    public void cancelRecord() {
        if (isRecording()) {
            recorder.stop();
            recordHandler.removeCallbacks(runnable);
        }
    }

    public boolean exists(String path) {
        File f = new File(path);
        if (f.exists()) {
            return true;
        }
        return false;
    }

    public String getNameFromUri(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public boolean delete(String path) {
        boolean flag = false;
        File file = new File(path);
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        recorder = null;
        rec = null;
        return flag;
    }

    public static interface RecordListener {
        public void record(Recorder rec);

        public void stop(Recorder rec);
    }

    public static class PlayListener {
        public void start() {
        }

        ;

        public void stop() {
        }

        ;

        public void playing() {
        }

        ;

        public void error() {
        }

        ;

        public void download() {
        }

        ;
    }

    public static interface AsyncListener {
        public void onExecute();

        public void onExecute(String response);

        public void onProgress(Void... values);

        public void onComplete(String result);

        public void onError(String message);
    }

    public class Recorder {
        public long duration;
        public long mills;
        public String path;
        public String name;
        public int db;
    }

    public boolean sdcardExists() {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    private HeadsetPlugRecive hp;

    public void registerHeadsetReceiver() {
        hp = new HeadsetPlugRecive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.HEADSET_PLUG");
        context.registerReceiver(hp, intentFilter);
    }

    public void destroyRegister() {
        context.unregisterReceiver(hp);
    }

    public class HeadsetPlugRecive extends BroadcastReceiver {
        private static final String tag = "HeadsetPlugReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state")) {
                if (intent.getIntExtra("state", 0) == 0) {

                } else if (intent.getIntExtra("state", 0) == 1) {

                }
            }
        }
    }
}
