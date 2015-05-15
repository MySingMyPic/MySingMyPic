package com.ylsg365.pai.activity.video;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ylsg365.pai.R;
import com.ylsg365.pai.app.NavHelper;

import java.io.File;
import java.text.SimpleDateFormat;

public class VideoActivity extends ActionBarActivity implements View.OnClickListener, MediaRecorder.OnInfoListener {

    private String path;
    private SurfaceView video_sv,render_sv;
    private TextView timer;
    private ImageView video_image;
    private RelativeLayout layout_retry, layout_play,layout_done;
    private MediaRecorder mMediaRecorder;
    private File videoFile;
    private int choice_maxdur;
    private int duration = 0;
    private int hour = 0;
    private int minute = 0;
    private int second = 0;
    private long startTime = 0;
    private boolean isrecording;
    private Camera camera;
    private SurfaceHolder mSurfaceHolder,mRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle pageData = getIntent().getExtras();
        if (pageData != null){
            choice_maxdur = getIntent().getIntExtra("choice",0);
        }
        else {
            choice_maxdur = 0;
            Toast.makeText(this, "get max_dur failed!", Toast.LENGTH_SHORT).show();
        }
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video);
        init();
    }

    private void init() {
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 设置横屏显示
        //getWindow().setFormat(PixelFormat.TRANSLUCENT);
        path = Environment.getExternalStorageDirectory().toString();
        video_sv = (SurfaceView) findViewById(R.id.videosv);
        render_sv = (SurfaceView) findViewById(R.id.rendersv);
        video_image =(ImageView) findViewById(R.id.videoimg);
        timer = (TextView) findViewById(R.id.video_timer);
        layout_retry = (RelativeLayout) findViewById(R.id.videoretry);
        layout_play = (RelativeLayout) findViewById(R.id.videorecord);
        layout_done = (RelativeLayout) findViewById(R.id.videodone);
        //mDraw = (com.ylsg365.pai.SVDraw)findViewById(R.id.mdraw);

        layout_retry.setOnClickListener(this);
        layout_play.setOnClickListener(this);
        layout_done.setOnClickListener(this);
        video_image.setOnClickListener(this);

        //video_image.setVisibility(View.GONE);
        timer.setVisibility(View.GONE);
        switch (choice_maxdur){
            case 0:
                duration =3000;
                break;
            case 1:
                duration =18000;
                break;
            case 2:
                duration =30000;
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.videoretry:
                retry();
                break;
            case R.id.videorecord:
                record();
                break;
            case R.id.videodone:
                done();
                break;
            case R.id.videoimg:
                //done();
                break;
        }
    }

    private void record() {
        if(isrecording){
            isrecording = false;
            stop();
        }
        else if (mMediaRecorder == null) {
            //mDraw.setVisibility(View.VISIBLE);
            //mDraw.dorender();
            video_image.setVisibility(View.GONE);
            render_sv.setVisibility(View.VISIBLE);
            //render();
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
                path=Fpath+"/"+date+"record.mp4";
            }
            path=Fpath+"/"+date+"record.mp4";
            //path=Environment.getExternalStorageDirectory()+"/"+date+"record.mp4";
            videoFile = new File(path);
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.reset();
            camera= Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            if (camera != null) {
                camera.setDisplayOrientation(90);// 摄像图旋转90度
                camera.unlock();
                mMediaRecorder.setCamera(camera);
            }
            mMediaRecorder.setOrientationHint(90);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);     // 设置从麦克风采集声音MIC(或来自录像机的声音AudioSource.CAMCORDER)
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);  // 设置从摄像头采集图像
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 设置视频文件的输出格式// 必须在设置声音编码格式、图像编码格式之前设置
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 设置声音编码的格式
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);  // 设置图像编码的格式
            mMediaRecorder.setVideoSize(720, 480); //设置分辨率
            mMediaRecorder.setVideoFrameRate(50);   // 每秒 4帧
            mMediaRecorder.setMaxDuration(duration);    //设置可录制长度
            mMediaRecorder.setOnInfoListener(this);
            mMediaRecorder.setOutputFile(videoFile.getAbsolutePath());
            mMediaRecorder.setPreviewDisplay(video_sv.getHolder().getSurface());
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
        isrecording = false;
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
        camera.stopPreview();
        camera.release();
        camera = null;
        findViewById(R.id.btnstart).setVisibility(View.VISIBLE);
        findViewById(R.id.btnpause).setVisibility(View.GONE);
    }

    private void done() {
        if (!fileIsExists()) {
            Toast.makeText(this, "您未拍摄视频，请重新录制！", Toast.LENGTH_SHORT).show();
        }
        else if (fileIsExists()&&mMediaRecorder==null) {
            Toast.makeText(this, "视频文件已存在！", Toast.LENGTH_SHORT).show();
            //jumpToProccess();
            if(choice_maxdur == 0){
                NavHelper.toVideoAddEffectActivity(VideoActivity.this,path);
            }
            else{
                NavHelper.toVideoLongActivity(VideoActivity.this,path);
            }
        }
        else{
            isrecording = false;
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
            camera.stopPreview();
            camera.release();
            camera = null;
            findViewById(R.id.btnstart).setVisibility(View.VISIBLE);
            findViewById(R.id.btnpause).setVisibility(View.GONE);
            //jumpToProccess();
            if(choice_maxdur == 0){
                NavHelper.toVideoAddEffectActivity(VideoActivity.this,path);
            }
            else{
                NavHelper.toVideoLongActivity(VideoActivity.this, path);
            }

        }
    }

    private void retry() {
        if (mMediaRecorder != null) {
            //timer.setText(format(hour) + ":" + format(minute) + ":"+ format(second));
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;

        }
        if (camera!=null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        isrecording = false;
        record();

    }

    private void jumpToProccess() {
//        Intent intent = new Intent(MainActivity.this, VideoProcessActivity.class);
//        intent.putExtra("VIDEO_PATH", path);
//        MainActivity.this.startActivity(intent);
//        MainActivity.this.finish();
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
            camera.stopPreview();
            //camera.release();
            //camera = null;
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

    public void render() {
        mRender = render_sv.getHolder();
        render_sv.setZOrderOnTop(true);
        mRender.setFormat(PixelFormat.TRANSPARENT);
        Canvas canvas = mRender.lockCanvas();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.filter_blue);
        canvas.drawBitmap(bitmap, 0, 0, null);
        mRender.unlockCanvasAndPost(canvas);
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

}
