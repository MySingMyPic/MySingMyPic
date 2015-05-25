package com.ylsg365.pai.activity.music;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ylsg365.pai.R;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.customview.DrawView;
import com.ylsg365.pai.customview.MoveView;
import com.ylsg365.pai.customview.ObservableScrollView;
import com.ylsg365.pai.customview.RepeatImageView;
import com.ylsg365.pai.listener.ScrollViewListener;
import com.ylsg365.pai.util.DensityUtil;
import com.ylsg365.pai.util.FileUtils;
import com.ylsg365.pai.util.HttpMethodHelper;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.StringUtil;
import com.ylsg365.pai.util.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeMusicVolumeActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private TextView leftTextView;
    private TextView rightTextView;

    private int rootWidth;
    //音准范围选定

    private int timeLength=0;
    private MoveView huadong,huadong2;
    private DrawView draw1;
    private RelativeLayout moveLayout;
    private RepeatImageView repeatImage;

    int leftX=0,rightX=0;
    int mov_x=0;
    int firstX=0;
    boolean isMoveLeft=true;

    com.ylsg365.pai.customview.ObservableScrollView hScrollView;

    //调音量
    /**
     * 音调List
     * */
    List<Integer> volumeList=new ArrayList<Integer>();

    private GridView grid;
    com.ylsg365.pai.customview.ObservableScrollView data_Hscroll;
    ViewGroup _root;
    ImageView tag;
    private int difference=0;
    ChangeMusicVolumeListAdapter adapter;
    private int scrollBefore=0;
    private int scrollAfter=0;
    private int imageWidth=0;

    //时间显示
    TextView timeFrom,timeTo;

    //音量换算
    private long time=0;//音频的总时长，毫秒
    //dp
    int itemWidth=100;
    //毫秒
    int itemTime=5000;
    /**
     * 最后的截取时间，leftTime是开始时间，rightTime结束时间
     * */
    long leftTime=0,rightTime=0;
    /**
     * 音调高低
     * */
    int volume=0;

    //音乐播放
    private  MediaPlayer mediaPlayer;
    //本地歌曲的路径 以及获取的处理后的网络路径 以及保存在本地的文件名
    private  String path,path_url,path_filestore;

    RelativeLayout playLayout,cancel,finish;
    ImageView playImg;
    private boolean isPlay=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_music_volume);
        Bundle pageData = getIntent().getExtras();
        if (pageData != null)
        {
            path = getIntent().getStringExtra("AUDIO_PATH");
        }
        else {
            path = null;
            Toast.makeText(this, "获取音频文件失败!", Toast.LENGTH_SHORT).show();
        }
        //path = Environment.getExternalStorageDirectory()+ "/1pai/test.mp3";
        //just for test
        init();

        timeLength= DensityUtil.dip2px(this,getTimeLength());

        initVolumeList();
        setupToolbar();
        initWidget();
        initListener();

        leftTime=0;
        rightTime=time*rootWidth/timeLength;
        setTimeText();

        volume=volumeList.get(0);
        grid.setSelection(0);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }
    }

    //初始化音乐播放
    void init(){
        //进入Idle
        mediaPlayer = new MediaPlayer();
        try {
            //初始化
            mediaPlayer.setDataSource(path);

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            // prepare 通过异步的方式装载媒体资源
            mediaPlayer.prepare();
            time=mediaPlayer.getDuration();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Log.e("flag",""+isPlay);
             switch(msg.what)
             {
                 case 0:
                     Toast.makeText(ChangeMusicVolumeActivity.this,"下载成功！",Toast.LENGTH_SHORT);
                     NavHelper.toSingASoneActivity(ChangeMusicVolumeActivity.this,Environment.getExternalStorageDirectory()+"/1pai/"+path_filestore);
                     NavHelper.finish(ChangeMusicVolumeActivity.this);
                     break;
                 case 1:
                     Toast.makeText(ChangeMusicVolumeActivity.this,"文件已存在！",Toast.LENGTH_SHORT);
                     break;
                 case -1:
                     Toast.makeText(ChangeMusicVolumeActivity.this,"下载失败！",Toast.LENGTH_SHORT);
                     break;
                 case 11:
                     Toast.makeText(ChangeMusicVolumeActivity.this,"处理成功！",Toast.LENGTH_SHORT);
                     download();
                     break;
                 case 3:
                     mediaPlayer.seekTo((int)leftTime);
                     mediaPlayer.start();
                     setPlayButton();
                     break;
                 case 4:
                     mediaPlayer.pause();
                     setPlayButton();
                     break;
             }
        }
    };
    //100dp对应1000毫秒
    public int getTimeLength()
    {
        return (int)((double)(itemWidth*time)/(double)itemTime);
    }

    public void initVolumeList()
    {
        for(int i=0;i<30;i++)
            volumeList.add(i-14);
    }

    public void initWidget()
    {
        timeFrom=(TextView)findViewById(R.id.time_from);
        timeTo=(TextView)findViewById(R.id.time_to);

        hScrollView=(com.ylsg365.pai.customview.ObservableScrollView)findViewById(R.id.h_scroll);
        huadong2=(MoveView)findViewById(R.id.huadong2);
        huadong=(MoveView)findViewById(R.id.huadong);
        draw1=(DrawView)findViewById(R.id.draw1);
        moveLayout=(RelativeLayout)findViewById(R.id.move_layout);
        repeatImage=(RepeatImageView)findViewById(R.id.image);

        leftX=0;
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);

        rightX= wm.getDefaultDisplay().getWidth();
        rootWidth=wm.getDefaultDisplay().getWidth();

        //调整顶部区域大小
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) repeatImage
                .getLayoutParams();
        linearParams.height = 17*rootWidth/21;
        linearParams.width = timeLength;
        repeatImage.setLayoutParams(linearParams);

        data_Hscroll=(com.ylsg365.pai.customview.ObservableScrollView)findViewById(R.id.data_Hscroll);
        grid=(GridView)findViewById(R.id.data_gridview);
        adapter=new ChangeMusicVolumeListAdapter(this);
        grid.setAdapter(adapter);


        _root = (ViewGroup) findViewById(R.id.roots);
        tag=(ImageView)findViewById(R.id.button1);
        int[] loc=new  int[2];
        tag.getLocationOnScreen(loc);
        difference=loc[0]-tag.getLeft();

        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.huadong);
        imageWidth=mBitmap.getWidth();
        //调整顶部区域大小
        linearParams = (LinearLayout.LayoutParams) grid
                .getLayoutParams();

        linearParams.width = volumeList.size()*(int)this.getResources().getDimension(R.dimen.music_change_volume_item_width);
        grid.setLayoutParams(linearParams);
        grid.setNumColumns( volumeList.size());

        playLayout=(RelativeLayout)findViewById(R.id.play_layout);
        finish =(RelativeLayout)findViewById(R.id.finish);
        cancel =(RelativeLayout)findViewById(R.id.cancel);
        playImg=(ImageView)findViewById(R.id.play_img);
    }

    public void setTimeText()
    {
        timeFrom.setText(TimeUtil.getTimeStr(leftTime));
        timeTo.setText(TimeUtil.getTimeStr(rightTime));
    }

    public void initListener()
    {
        hScrollView.setScrollViewListener(new ScrollViewListener()
        {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy)
            {
                firstX=x;
                leftTime=time*(firstX+leftX)/timeLength;
                rightTime=time*(firstX+rightX)/timeLength;

                setTimeText();
            }
        });

        moveLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mov_x=(int) event.getX();

                if (event.getAction()==MotionEvent.ACTION_MOVE) {//如果拖动
                    move(mov_x,event);

                }
                else if (event.getAction()==MotionEvent.ACTION_DOWN) {//如果拖动

                    if(Math.abs((mov_x-leftX))>Math.abs((mov_x-rightX)))
                    {
                        isMoveLeft=false;
                    }
                    else isMoveLeft=true;

                    move(mov_x, event);

                }
                else if (event.getAction()==MotionEvent.ACTION_UP) {//如果拖动
                    move(mov_x, event);
                    if(isMoveLeft==true)
                        leftX=mov_x;
                    else rightX=mov_x;



                }
                draw1.autoMouse(event);

                return true;
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                adapter.setSelect(arg2);
                volume=volumeList.get(arg2);

                int[] location = new int[2];
                arg1.getLocationOnScreen(location);
                Log.e("item",location[0]+":"+location[1]);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tag
                        .getLayoutParams();
                layoutParams.leftMargin = location[0]-difference+
                        ((int)getResources().getDimension(R.dimen.music_change_volume_item_width)-imageWidth)/2;

                tag.setLayoutParams(layoutParams);
                _root.invalidate();

            }});

        data_Hscroll.setScrollViewListener(new ScrollViewListener(){

            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x,
                                        int y, int oldx, int oldy) {
                scrollAfter=scrollView.getScrollX();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tag
                        .getLayoutParams();
                int oldTimeBarX=layoutParams.leftMargin;
                layoutParams.leftMargin = oldTimeBarX-(scrollAfter-scrollBefore);

                tag.setLayoutParams(layoutParams);
                _root.invalidate();
                scrollBefore=scrollAfter;
            }
        });

        playLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        if(isPlay==false)
                            isPlay=true;
                        else isPlay=false;

                        handler.sendEmptyMessage(3);
                        long i=0;
                        for( i=leftTime;i<(rightTime);i++)
                        {
                            if(!isPlay)
                                break;
                            try {
                                sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if(i==rightTime)
                            isPlay=false;
                        handler.sendEmptyMessage(4);

                    }
                }.start();
            }
        });

        finish.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                compose();//完成调音将先上传处理，下载完成后再跳转到音频处理界面
            }
        });

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {//注意：放弃调音直接跳转到录制界面
                NavHelper.finish(ChangeMusicVolumeActivity.this);
            }
        });
    }

    private void compose()
    {
        //get url of adding music

//        Intent i=new Intent(this, CompessDialog.class); //TODO   no use!!!!
//        startActivity(i);

        new Thread(){
            @Override
            public void run() {
                String url = "http://182.92.170.38:18082/Weitie/client/fileController/snsByFile";
                File file = new File(path);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Map<String, File> files = new HashMap<String, File>();
                Map<String, String> params = new HashMap<String, String>();
                //变音调参数
                params.put("startTime" , String.valueOf(leftTime));//音频调音高
                params.put("endTime" , String.valueOf(rightTime));
                params.put("tune" , String.valueOf(volume));

                try {
                    String str = YinApi.mediaUpload(url, params, files);
                    if (!StringUtil.isNull(str)) {
                        JSONObject json = null;
                        json = new JSONObject(str);
                        if (JsonUtil.getBoolean(json, "status")) {
                            JSONArray array = JsonUtil.getJSONArray(json, "fileName");  //得到返回的URL
                            if (array.length() > 0) {
                                path_filestore = (String)array.get(0);
                                path_url = "http://"+FileUtils.host +str;
                                Message msg = new Message();
                                msg.what = 11;
                                msg.obj = path_url;
                                handler.sendMessage(msg);
                            }

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    private void download(){
        new Thread(){
            @Override
            public void run() {
                HttpMethodHelper httpMethodHelper = new HttpMethodHelper();
                int result = -1;
                result = httpMethodHelper.downfile(path_url, "1pai",path_filestore);
                handler.sendEmptyMessage(result);
            }

        }.start();
    }

    public void setPlayButton()
    {
        if(isPlay==true)
        {
            playImg.setImageDrawable(getResources().getDrawable(R.drawable.img_stop_n));
        }
        else
        {
            playImg.setImageDrawable(getResources().getDrawable(R.drawable.bofang01));
        }

    }
    public void move(int x,MotionEvent event)
    {
        if(isMoveLeft==true) {
            if(x<rightX){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) huadong
                        .getLayoutParams();
                int oldTimeBarX=layoutParams.leftMargin;
                layoutParams.leftMargin = x-imageWidth/2;

                huadong.setLayoutParams(layoutParams);
                moveLayout.invalidate();

                leftTime=time*(firstX+x)/timeLength;
                setTimeText();
            }

        }
        else
        {
            if(x>leftX){
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) huadong2
                        .getLayoutParams();
                int oldTimeBarX=layoutParams.leftMargin;
                layoutParams.rightMargin = rootWidth-x-imageWidth/2;

                huadong2.setLayoutParams(layoutParams);
                moveLayout.invalidate();

                rightTime=time*(firstX+x)/timeLength;
                setTimeText();
            }

        }
    }

    public void setupToolbar() {
        View v = findViewById(R.id.toolbar);
        if (v != null) {
            toolbar = (Toolbar) v;
            setSupportActionBar(toolbar);
            toolbarTitle = (TextView) v.findViewById(R.id.toolbar_title);
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            toolbarTitle.setVisibility(View.VISIBLE);
            toolbarTitle.setText("调音准");

            leftTextView = (TextView) v.findViewById(R.id.text_toolbar_left);
            leftTextView.setVisibility(View.GONE);


            rightTextView=(TextView) v.findViewById(R.id.text_right);

            rightTextView.setVisibility(View.GONE);

        }
    }

    public final class ViewHolder {
        public TextView index;
        public LinearLayout item_layout;
    }

    class ChangeMusicVolumeListAdapter extends BaseAdapter {

        ViewHolder holder = null;
        private LayoutInflater mInflater;
        int selectPos=-1;
        private Activity act;
        public ChangeMusicVolumeListAdapter(Activity context) {
            act=context;
            this.mInflater = LayoutInflater.from(context);
            selectPos=-1;
        }

        @Override
        public int getCount() {
            return volumeList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public void setSelect(int pos)
        {
            selectPos=pos;
            this.notifyDataSetChanged();
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.music_change_volume_list_item_layout,null);
                holder.index = (TextView) convertView
                        .findViewById(R.id.textView);
                holder.item_layout = (LinearLayout) convertView
                        .findViewById(R.id.item_layout);
                ViewTreeObserver vto = holder.item_layout.getViewTreeObserver();
                vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {

                        return true;
                    }
                });
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.index.setText(volumeList.get(position)+"");

            if(position!=selectPos)
            {
                holder.index.setTextColor(act.getResources().getColor(R.color.text_item_grey));
            }
            else
            {
                holder.index.setTextColor(act.getResources().getColor(R.color.red));
            }
            return convertView;
        }
    }
}
