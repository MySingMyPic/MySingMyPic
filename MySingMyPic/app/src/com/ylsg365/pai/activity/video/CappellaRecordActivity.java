package com.ylsg365.pai.activity.video;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.RecordManager;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.util.FileUtils;

import java.io.File;
import java.io.IOException;


public class CappellaRecordActivity extends BaseActivity implements View.OnClickListener{
    private SurfaceView rateSurfaceView;
    private SurfaceHolder rateHolder;
    private Paint mPaint;

    private TextView recordTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cappella_record);
        rateSurfaceView = (SurfaceView) findViewById(R.id.surface_rate);

        rateSurfaceView.setZOrderOnTop(true);
        rateSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        // sfv.setOnTouchListener(new TouchEvent());
        // sfv.setOnClickListener(new TouchEvent());

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


        init();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ffmpeg4_android, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void loadViews() {
        recordTime = (TextView)findViewById(R.id.text_time);
    }

    private RecordManager recordManager;
    private RecordManager getRecordManager(){
        if(recordManager == null){
            recordManager = new RecordManager(this);
        }

        return recordManager;
    }

    @Override
    protected void setupListeners() {
        findViewById(R.id.layout_cancel).setOnClickListener(this);
        findViewById(R.id.layout_record).setOnClickListener(this);
        findViewById(R.id.layout_finish).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_cancel:
                stopRecord();
                break;
            case R.id.layout_record:
                record();
                break;
            case R.id.layout_finish:
                finishRecord();
                break;
        }
    }

    private void record(){
        String path = getSDPath() + "/pai/record/test.mp4";
        createRecordFile(path);
        getRecordManager().record(path, "test.mp4", new RecordManager.RecordListener() {
            int zeroCount = 0;
            int MaxCheckCount = 3;
            int MaxDP = 100;
            @Override
            public void stop(RecordManager.Recorder rec) {
                stopRecord();
                if( rec.duration < 2 ){
//                    alert("时间太短啦");
                    getRecordManager().delete(rec.path);
                }
            }

            @Override
            public void record(RecordManager.Recorder rec) {
//                UIHelper.showToast("开始录音");

                int a, b;

                //更新时间
                b = (int)rec.duration;

                if ( b >= 60 ){
                    a = (int)Math.floor(rec.duration / 60);
                    b = (int)Math.floor(rec.duration % 60);
                }else{
                    a = 0;
                }

//                if( b < 10 ){
                    recordTime.setText(String.format("正在录制 %02d:%02d", a, b));
//                }else{
//                    recordTime.setText(a+":"+b);
//                }

                //更新声音大小动画
                if( rec.db == 0 ){
                    zeroCount++;
                }else{
                    zeroCount = 0;
                }

                if( zeroCount > 0 && zeroCount <= MaxCheckCount ){
                    return;
                }

                float d = (float) ( Math.atan(rec.db  * 0.025) * 1.25 );
//                recordAnimate( 1+d );
            }
        });
    }

    private void cancel(){

    }

    private void again(){

    }

    private void finishRecord(){
        stopRecord();
    }

    public void stopRecord(){
        if( getRecordManager().isRecording() ){
            UIHelper.showToast("停止录音");
            getRecordManager().stopRecord();
        }
    }


    public String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if   (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录

        }
        return sdDir.toString();

    }

    public void createRecordFile(String path){
        File file = new File(path);
        if(!file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
