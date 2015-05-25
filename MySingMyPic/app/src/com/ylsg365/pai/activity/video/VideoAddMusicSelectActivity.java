package com.ylsg365.pai.activity.video;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.ylsg365.pai.R;
import com.ylsg365.pai.adapter.VideoAddMusicListViewAdapter;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.customview.CompessDialog;
import com.ylsg365.pai.util.FileUtils;
import com.ylsg365.pai.util.HttpMethodHelper;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoAddMusicSelectActivity extends ActionBarActivity {

    private int way=0;// 0 是30秒视频，1是3分钟和5分钟视频

    VideoAddMusicListViewAdapter myMusicAdapter,otherMusicAdapter;
    ListView myMusicListView,otherMusicListView;
    TextView myMusicTag,otherMusicTag;

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private TextView leftTextView;
    private TextView rightTextView;
    private String path_filestore,path_url,music_url;
    private int request_result;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg){
            switch (msg.what){
                case 11:
                    Toast.makeText(VideoAddMusicSelectActivity.this, "get url success!--->" + msg.obj, Toast.LENGTH_SHORT);
                    download();
                    break;
                case 0:
                    Toast.makeText(VideoAddMusicSelectActivity.this,"完成!",Toast.LENGTH_SHORT);
                    break;
                case 1:
                    Toast.makeText(VideoAddMusicSelectActivity.this,"已存在!",Toast.LENGTH_SHORT);
                    break;
                case -1:
                    Toast.makeText(VideoAddMusicSelectActivity.this,"失败，请重试!",Toast.LENGTH_SHORT);
                    break;
            }
        }

    };

    //数据
    List<Map<String,Object>> myMusicList=new ArrayList<Map<String,Object>>();
    List<Map<String,Object>> otherMusicList=new ArrayList<Map<String,Object>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_add_music_select);

        way=getIntent().getIntExtra("choice",0);
        path_filestore = "test_addback.mp3";
        setupToolbar();
        initWidget();
        getData();
        initListener();
    }

    private void initWidget()
    {
        myMusicListView=(ListView)findViewById(R.id. my_music_listview);
        otherMusicListView =(ListView)findViewById(R.id. other_music_listview);

        myMusicTag =(TextView)findViewById(R.id. my_music_tag);
        otherMusicTag =(TextView)findViewById(R.id. other_music_tag);
    }

    private void setupToolbar() {
        View v = findViewById(R.id.toolbar);
        if (v != null) {
            toolbar = (Toolbar) v;
            setSupportActionBar(toolbar);
            toolbarTitle = (TextView) v.findViewById(R.id.toolbar_title);
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }

            toolbarTitle.setText("选择音乐");
            leftTextView = (TextView) v.findViewById(R.id.text_toolbar_left);

            leftTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavHelper.finish(VideoAddMusicSelectActivity.this);
                }
            });

            rightTextView=(TextView) v.findViewById(R.id.text_right);
            rightTextView.setText("完成添加");
            rightTextView.setVisibility(View.VISIBLE);
            rightTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (request_result==0){
                        //TODO 点击右上角返回给上一页面音乐文件,要添加音乐文件路径参数
                        //NavHelper.toVideoAddEffectActivity(VideoAddMusicSelectActivity.this,null,Environment.getExternalStorageDirectory()+"/ipai/"+music_url.split("//")[1]);
                        NavHelper.toVideoAddEffectActivity(VideoAddMusicSelectActivity.this,null,Environment.getExternalStorageDirectory()+"/ipai/"+path_filestore);
                        NavHelper.finish(VideoAddMusicSelectActivity.this);
                    }
                }
            });
        }
    }

    private void initListener()
    {
        myMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                music_url = (String) myMusicList.get(position).get("songUrl");
                //compose();
                download();
            }
        });

        otherMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                music_url = (String) otherMusicList.get(position).get("songUrl");
                //compose();
                download();
            }
        });
    }

    /**
     * 从后台获取歌曲列表
     */
    private void getData(){
        //根据上传返回的json数据显示在列表中
        myMusicList.clear();
        otherMusicList.clear();
        /**
         * 获取歌曲数据
         */
        YinApi.getSongs30(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (JsonUtil.getBoolean(response, "status")) {
                            JSONArray array = JsonUtil.getJSONArray(response, "datas");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject json = JsonUtil.getJSONObject(array, i);
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("songId", JsonUtil.getString(json, "songId"));
                                map.put("songName", JsonUtil.getString(json, "songName"));
                                map.put("songUrl", JsonUtil.getString(json, "songUrl"));  //TODO 疑问：是URL还是仅是文件名

                                otherMusicList.add(map);
                                fillData();
                            }
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

    }

    /**
     * 将获取的数据显示在listview
     */
    private void fillData()
    {
        otherMusicAdapter = new VideoAddMusicListViewAdapter(this, otherMusicList);
        otherMusicListView.setAdapter(otherMusicAdapter);
        setListViewHeight(otherMusicListView);

        if(way==0) {
            myMusicAdapter=new VideoAddMusicListViewAdapter(this,myMusicList);
            myMusicListView.setAdapter(myMusicAdapter);
            setListViewHeight(myMusicListView);
        }
        else{
            myMusicTag.setVisibility(View.GONE);
            myMusicListView.setVisibility(View.GONE);
        }
    }

    private void setListViewHeight(ListView listView) {

        int totalHeight = 0;
        for (int i = 0; i < listView.getCount(); i++) {

            totalHeight +=(int)getResources().getDimension(R.dimen.video_add_music_item_width);
        }

        totalHeight+=listView.getDividerHeight()*(listView.getCount());
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;

        listView.setLayoutParams(params);
    }

    private void compose()
    {
        //get url of adding music

//        Intent i=new Intent(this, CompessDialog.class); //TODO   no use!!!!
//        startActivity(i);

        new Thread(){
            @Override
            public void run() {
                String url = Constants.WEB_SERVER_DOMAIN + "   ";
                Map<String, File> files = new HashMap<String, File>();
                Map<String, String> params = new HashMap<String, String>();

                try {
                    String str = YinApi.mediaUpload(url, params, files);
                    if (!StringUtil.isNull(str)) {
                        JSONObject json = null;
                        json = new JSONObject(str);
                        if (JsonUtil.getBoolean(json, "status")) {
                            String purl = JsonUtil.getString(json, "songUrl");
                            if (purl!=null) {
                                path_filestore = purl;
                                str = FileUtils.host + purl;
                                path_url = "http://"+str;
                                Message msg = new Message();
                                msg.what = 11;
                                msg.obj = path_url;
                                mHandler.sendMessage(msg);
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
        Toast.makeText(VideoAddMusicSelectActivity.this,"开始下载!",Toast.LENGTH_SHORT);
        new Thread(){
            @Override
            public void run() {
                HttpMethodHelper httpMethodHelper = new HttpMethodHelper();
                int result = -1;
                //result = httpMethodHelper.downfile("http://182.92.170.38:18080" + music_url, "1pai/", music_url.split("//")[1]);
                result = httpMethodHelper.downfile("http://182.92.170.38:18080" + music_url, "1pai/", path_filestore);
                mHandler.sendEmptyMessage(result);
            }

        }.start();
    }

}
