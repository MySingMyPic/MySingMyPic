package com.ylsg365.pai.activity.video;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.music.SingASoneActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.RecordManager;
import com.ylsg365.pai.app.UIHelper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;


public class CappellaRecordActivity extends Activity implements View.OnClickListener, MediaRecorder.OnInfoListener {
    private SurfaceView rateSurfaceView;
    private SurfaceHolder rateHolder;
    private Paint mPaint;
    private String path;
    private MediaRecorder mMediaRecorder;
    private File audioFile;
    private int duration = 0;
    private int hour = 0;
    private int minute = 0;
    private int second = 0;
    private long startTime = 0;
    private boolean isrecording;

    private TextView timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cappella_record);

        rateSurfaceView = (SurfaceView) findViewById(R.id.surface_rate);
        rateSurfaceView.setZOrderOnTop(true);
        rateSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        timer = (TextView)findViewById(R.id.text_time);
        findViewById(R.id.layout_cancel).setOnClickListener(this);
        findViewById(R.id.layout_record).setOnClickListener(this);
        findViewById(R.id.layout_finish).setOnClickListener(this);
        // sfv.setOnTouchListener(new TouchEvent());
        // sfv.setOnClickListener(new TouchEvent());
        drawwave();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_cancel:
                stop();
                break;
            case R.id.layout_record:
                record();
                break;
            case R.id.layout_finish:
                done();
                break;
        }
    }

    private void record() {
        if(isrecording){
            stop();
        }
        else if (mMediaRecorder == null) {
            second = 0;
            minute = 0;
            hour = 0;
            findViewById(R.id.btnstart).setVisibility(View.GONE);
            findViewById(R.id.btnpause).setVisibility(View.VISIBLE);
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            String date = sDateFormat.format(new java.util.Date());
            String Fpath = Environment.getExternalStorageDirectory()+"/1pai";
            File file=new File(Fpath);
            if(!file.exists()) {
                file.mkdir();
                path=Fpath+"/"+date+"record.mp3";
            }
            path=Fpath+"/"+date+"record.mp3";
            //path=Environment.getExternalStorageDirectory()+"/"+date+"record.mp4";
            audioFile = new File(path);
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.reset();
            //mMediaRecorder.setOrientationHint(90);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);     // 设置从麦克风采集声音MIC(或来自录像机的声音AudioSource.CAMCORDER)
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mMediaRecorder.setAudioSamplingRate(44100);  //采样率
            mMediaRecorder.setAudioChannels(1);          //单声道
//            mMediaRecorder.setCaptureRate();             //
            mMediaRecorder.setAudioEncodingBitRate(128000);//比特率
            //mMediaRecorder.setMaxDuration(duration);    //设置可录制长度
            mMediaRecorder.setOnInfoListener(this);
            mMediaRecorder.setOutputFile(audioFile.getAbsolutePath());
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// 设置声音编码的格式
            try {
                timer.setVisibility(View.VISIBLE);
                startTime = System.currentTimeMillis();
                handler.postDelayed(recordtimertask, 0);
                isrecording = true;
                mMediaRecorder.prepare();
                mMediaRecorder.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Toast.makeText(this, "在录制中，请不要重复操作!", Toast.LENGTH_SHORT).show();
        }
    }

    private void stop() {
        if (isrecording){
            isrecording = false;
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
            findViewById(R.id.btnstart).setVisibility(View.VISIBLE);
            findViewById(R.id.btnpause).setVisibility(View.GONE);
        }
    }

    private void done() {
        if (!fileIsExists()) {
            Toast.makeText(this, "您未录取音频，请重新录制！", Toast.LENGTH_SHORT).show();
        }
        else if (fileIsExists()&&mMediaRecorder==null) {
            Toast.makeText(this, "音频文件已存在！", Toast.LENGTH_SHORT).show();
            NavHelper.toSingASoneActivity(CappellaRecordActivity.this,path);
        }
        else{
            isrecording = false;
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
            findViewById(R.id.btnstart).setVisibility(View.VISIBLE);
            findViewById(R.id.btnpause).setVisibility(View.GONE);
            NavHelper.toSingASoneActivity(CappellaRecordActivity.this,path);
        }
    }

    private void retry() {
        if (mMediaRecorder != null) {
            //timer.setText(format(hour) + ":" + format(minute) + ":"+ format(second));
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;

        }
        isrecording = false;
        record();

    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {

            if (mMediaRecorder != null) {
                Toast.makeText(this, "已经达到最长录制时间!", Toast.LENGTH_SHORT).show();
                isrecording = false;
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;
                isrecording = false;
                findViewById(R.id.btnstart).setVisibility(View.VISIBLE);
                findViewById(R.id.btnpause).setVisibility(View.GONE);
            }
        }
    }

    private Handler handler = new Handler();
    private Runnable recordtimertask = new Runnable() {
        public void run() {
            if (isrecording) {
                long mills = System.currentTimeMillis() - startTime;
                int dura = (int) Math.ceil(mills / 1000);
                minute = (int)Math.floor(dura / 60);
                second = (int)Math.floor(dura % 60);
                handler.postDelayed(this, 1000);
                timer.setText(format(hour) + ":" + format(minute) + ":"+ format(second));
            }
        }
    };

    /*
     * 格式化时间
     */
    public String format(int i) {
        String s = i + "";
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }


    public boolean fileIsExists() {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 画波形
     */
    private void drawwave() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.purple));// 画笔为绿色
        mPaint.setStyle(Paint.Style.STROKE);//设置空心
        mPaint.setStrokeWidth(4);// 设置画笔粗细

        rateHolder = rateSurfaceView.getHolder();

        rateHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                int startX = 100;
                int totalHeight = 396;
                int heightCenter = totalHeight / 2;
                int minLineHalf = 10;
                int increaseHeight = 10;
                int increaseWidth = 20;
                int initLineHeight = minLineHalf;


                Canvas canvas = holder.lockCanvas();// 获取画布

                if(canvas!=null){
//            canvas.drawBitmap(backgroundImage, 0, 0, mPaint);


//            rateSurfaceView.getHolder().unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像

                    RectF oval=new RectF(150,20,170,40);                     //RectF对象
////                    oval.left=100;                              //左边
////                    oval.top=120;                                   //上边
////                    oval.right=120;                             //右边
////                    oval.bottom=110;                                //下边
//                    canvas.drawArc(oval, 180, 180, false, mPaint);    //绘制圆弧
//                    canvas.drawLine(150,30,150,220, mPaint);
//                    canvas.drawLine(170,30,170,220, mPaint);
//
//                     oval=new RectF(170,220 - 10,190,240 - 10);
//                    canvas.drawArc(oval, 0, 180, false, mPaint);    //绘制圆弧
//                    canvas.drawLine(190,30,190,220, mPaint);

                    for(int i=1; i<50; i++) {
                        canvas.drawLine(startX + increaseWidth * (i-1), heightCenter, startX + increaseWidth * (i-1), heightCenter + increaseHeight * i, mPaint);
//                        canvas.drawLine(startX + increaseWidth * (i-1), heightCenter, startX + increaseWidth * (i-1), heightCenter - increaseHeight * i, mPaint);
                        int topX = startX + increaseWidth * (i-1);
                        int topY = heightCenter - increaseHeight * i;
                        oval = new RectF(topX, topY - 10, topX + 20, topY + 10);
                        canvas.drawArc(oval, 180, 180, false, mPaint);    //绘制圆弧

                        canvas.drawLine(startX + increaseWidth * i, heightCenter, startX + increaseWidth * i, heightCenter - increaseHeight * i, mPaint);


                        canvas.drawLine(startX + increaseWidth * i, heightCenter, startX + increaseWidth * i, heightCenter + increaseHeight * (i + 2), mPaint);


                        int bottomX = startX + increaseWidth * i;
                        int bottomY = heightCenter + increaseHeight * (i+2);
                        oval.set(bottomX, bottomY - 10, bottomX + 20, bottomY + 10);
                        canvas.drawArc(oval, 0, 180, false, mPaint);    //绘制圆弧



                        startX = startX + increaseWidth * (i);
                    }
                    holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

    }

//    private void record(){
//        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
//        String date = sDateFormat.format(new java.util.Date());
//        String Fpath = Environment.getExternalStorageDirectory()+"/1pai";
//        //File file=new File(Fpath);
//        //if(!file.exists()) {
//        //    file.mkdir();
//        //}
//        path=Fpath+"/"+date+"record.mp3";
//        //path = getSDPath() + "/pai/record/test.mp3";
//        createRecordFile(path);
//        getRecordManager().record(path, date+"record.mp3", new RecordManager.RecordListener() {
//            int zeroCount = 0;
//            int MaxCheckCount = 3;
//            int MaxDP = 100;
//            @Override
//            public void stop(RecordManager.Recorder rec) {
//                stopRecord();
//                if( rec.duration < 2 ){
////                    alert("时间太短啦");
//                    getRecordManager().delete(rec.path);
//                }
//            }

//            @Override
//            public void record(RecordManager.Recorder rec) {
////                UIHelper.showToast("开始录音");
//
//                int a, b;
//
//                //更新时间
//                b = (int)rec.duration;
//
//                if ( b >= 60 ){
//                    a = (int)Math.floor(rec.duration / 60);
//                    b = (int)Math.floor(rec.duration % 60);
//                }else{
//                    a = 0;
//                }
//
////                if( b < 10 ){
//                    recordTime.setText(String.format("正在录制 %02d:%02d", a, b));
////                }else{
////                    recordTime.setText(a+":"+b);
////                }
//
//                //更新声音大小动画
//                if( rec.db == 0 ){
//                    zeroCount++;
//                }else{
//                    zeroCount = 0;
//                }
//
//                if( zeroCount > 0 && zeroCount <= MaxCheckCount ){
//                    return;
//                }
//
//                float d = (float) ( Math.atan(rec.db  * 0.025) * 1.25 );
////                recordAnimate( 1+d );
//            }
//        });
//    }
//
//    private void cancel(){
//
//    }
//
//    private void again(){
//
//    }
//
//    private void finishRecord(){
//        stopRecord();
//        NavHelper.toSingASoneActivity(this,path);
//        //NavHelper.toSingASoneActivity(this);
//    }
//
//    public void stopRecord(){
//        if( getRecordManager().isRecording() ){
//            UIHelper.showToast("停止录音");
//            getRecordManager().stopRecord();
//        }
//    }
//
//
//    public String getSDPath(){
//        File sdDir = null;
//        boolean sdCardExist = Environment.getExternalStorageState()
//                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
//        if   (sdCardExist)
//        {
//            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
//
//        }
//        return sdDir.toString();
//
//    }
//
//    public void createRecordFile(String path){
//        File file = new File(path);
//        if(!file.getParentFile().exists()){
//            file.getParentFile().mkdirs();
//        }
//        if(!file.exists()){
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
