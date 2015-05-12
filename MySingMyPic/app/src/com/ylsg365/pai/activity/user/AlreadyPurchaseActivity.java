package com.ylsg365.pai.activity.user;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.singsong.SongActivity;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.model.UserService;
import com.ylsg365.pai.util.CommonAdapter;
import com.ylsg365.pai.util.FileUtils;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.StringUtil;
import com.ylsg365.pai.util.ViewHolder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 已购列表
 */

public class AlreadyPurchaseActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {
    private ArrayList<Map<String, String>> songs = new ArrayList<Map<String, String>>();
    private Intent intent;
    private int page = 0;
    private final int rows = 10;
    private PullToRefreshListView listView;
    private CommonAdapter adapter;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        setupToolbar();

        setTitle("已购单曲");

        listView = (PullToRefreshListView) findViewById(R.id.recycler_song);
        listView.getRefreshableView().setDivider(null);
        listView.getRefreshableView().setSelector(android.R.color.transparent);
        listView.setOnRefreshListener(this);


        // set mode to BOTH
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        ILoadingLayout startLabels = listView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");
        startLabels.setRefreshingLabel("加载中...");
        startLabels.setReleaseLabel("释放刷新");
        /**
         * 自动加载更多数据
         */
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                isRefresh = false;
                page++;
                initData( page, rows);
            }
        });


        initData( page, rows);
        /**
         * adapter实例化
         */
        adapter = new CommonAdapter(AlreadyPurchaseActivity.this, songs, R.layout.item_already_purchase) {

            @Override
            public void convert(final ViewHolder holder, final Object item) {
                holder.setText(R.id.song_name, ((HashMap) item).get("songName").toString());
                holder.setText(R.id.song_time, ((HashMap) item).get("cTime").toString());
                holder.setText(R.id.song_length, ((HashMap) item).get("recordTime").toString());

                if(StringUtil.isNull(holder.getView(R.id.downloading).getTag()+"") ){    //默认未下载状态
                    holder.setBackgroundResource(R.id.song_btn,R.drawable.xiazai);
                    holder.setVisibility(R.id.downloading, View.GONE);
                }else if("1".equals(holder.getView(R.id.downloading).getTag().toString())){   //下载中
                    holder.setVisibility(R.id.downloading, View.VISIBLE);
                    holder.setVisibility(R.id.song_btn, View.GONE);
                }else if("2".equals(holder.getView(R.id.downloading).getTag().toString())){   //下载完成
                    holder.setVisibility(R.id.downloading, View.GONE);
                    holder.setVisibility(R.id.song_btn, View.VISIBLE);
                    holder.setBackgroundResource(R.id.song_btn,R.drawable.duoxuanxuanzhong);
                }

                holder.setOnClickListener(R.id.song_btn,new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if("2".equals(holder.getView(R.id.downloading).getTag().toString())){
                            NavHelper.showToast(AlreadyPurchaseActivity.this,"已经下载过了哦!");
                        }else if(StringUtil.isNull(holder.getView(R.id.downloading).getTag()+"")){
                            download(Constants.WEB_IMG_DOMIN+((HashMap) item).get("recordUrl"),((HashMap) item).get("songName").toString(),(TextView)holder.getView(R.id.downloading),(ImageButton)holder.getView(R.id.song_btn));
                        }
                    }
                });

            }
        };
        listView.setAdapter(adapter);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * 初始化歌曲数据
     *
     * @param page
     * @param rows
     */
    private void initData( final int page, final int rows) {
        YinApi.getPayRecords(page + "", rows + "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (JsonUtil.getBoolean(response, "status")) {
                            if (isRefresh) {
                                adapter.clearData();
                            }
                            JSONArray array = JsonUtil.getJSONArray(response, "records");
                            songs.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject json = JsonUtil.getJSONObject(array, i);
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("songName", JsonUtil.getString(json, "songName"));
                                map.put("recordId", JsonUtil.getString(json, "recordId"));
                                map.put("recordUrl", JsonUtil.getString(json, "recordUrl"));
                                map.put("recordTime", JsonUtil.getString(json, "recordTime"));
                                map.put("cTime", JsonUtil.getString(json, "cTime"));
                                songs.add(map);
                            }
                            if (page == 0) {
                                adapter.setDatas(songs);
                            } else {
                                adapter.addData(songs);
                            }
                        } else {
                            listView.setIsLoadMore(false);
                            Toast.makeText(AlreadyPurchaseActivity.this, getString(R.string.no_more_toast), Toast.LENGTH_LONG).show();
                        }
                        listView.onRefreshComplete();
                        adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listView.onRefreshComplete();
                    }
                });
    }


    /**
     * 下拉刷新事件
     *
     * @param refreshView
     */
    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        page = 0;
        isRefresh = true;
        listView.setIsLoadMore(true);
        initData( page, rows);
    }

    public void download(String url,String name,final TextView downloading, final ImageButton view){
       int uid = UserService.getUser().getUserId();

        HttpUtils http = new HttpUtils();
        http.download(url, FileUtils.path+FileUtils.mp3+uid+"/"+name+".mp3", true, true, new RequestCallBack() {

            @Override
            public void onStart() {
                NavHelper.showToast(AlreadyPurchaseActivity.this,"正在连接...");
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                downloading.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);

                downloading.setTag(R.id.downloading,1);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                NavHelper.showToast(AlreadyPurchaseActivity.this,msg);
                downloading.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);

                downloading.setTag(R.id.downloading,"");
            }

            @Override
            public void onSuccess(ResponseInfo responseInfo) {
                NavHelper.showToast(AlreadyPurchaseActivity.this, "下载成功!");
                downloading.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.duoxuanxuanzhong));

                downloading.setTag(R.id.downloading,2);
            }
        });

    }

}

