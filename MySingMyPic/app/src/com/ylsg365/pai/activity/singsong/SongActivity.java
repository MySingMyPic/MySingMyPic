package com.ylsg365.pai.activity.singsong;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.CommonAdapter;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.StringUtil;
import com.ylsg365.pai.util.ViewHolder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 歌曲界面
 */
public class SongActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {
    private ArrayList<Map<String, String>> songs = new ArrayList<Map<String, String>>();
    private Intent intent;
    private int page = 0;
    private final int rows = 10;
    private PullToRefreshListView listView;
    private CommonAdapter adapter;
    private boolean isRefresh = false;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        intent = getIntent();
        setupToolbar();

        setTitle("歌曲");
        String title = intent.getStringExtra("typename");
        if (!StringUtil.isNull(title)) {
            setTitle(title);
        }

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
                initData(intent.getStringExtra("type"), intent.getStringExtra("singid"), intent.getStringExtra("songname"), page, rows);
            }
        });


        initData(intent.getStringExtra("type"), intent.getStringExtra("singid"), intent.getStringExtra("songname"), page, rows);
        /**
         * adapter实例化
         */
        adapter = new CommonAdapter(SongActivity.this, songs, R.layout.item_song) {

            @Override
            public void convert(ViewHolder holder, Object item) {
                holder.setText(R.id.song_name, ((HashMap) item).get("name").toString());
                holder.setText(R.id.song_time, ((HashMap) item).get("sing").toString());
                final String url = ((HashMap) item).get("url").toString();
                Button btn = (Button)(holder.getView(R.id.song_btn));
                btn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavHelper.toCappellaRecordPage(SongActivity.this, url);
                    }
                });
            }
        };
        listView.setAdapter(adapter);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * 初始化歌曲数据
     * @param type
     * @param singid
     * @param songname
     * @param page
     * @param rows
     */
    private void initData(String type, String singid, String songname, final int page, final int rows) {
        YinApi.getSongs(type, singid, songname, page, rows,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (JsonUtil.getBoolean(response, "status")) {
                            if(isRefresh){
                                adapter.clearData();
                            }
                            JSONArray array = JsonUtil.getJSONArray(response, "datas");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject json = JsonUtil.getJSONObject(array, i);
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("sing", JsonUtil.getString(json, "singerName"));
                                map.put("name", JsonUtil.getString(json, "songName"));
                                map.put("url", JsonUtil.getString(json, "songUrl"));
                                songs.add(map);
                            }
//                            if (page == 1) {
//                                adapter.setDatas(songs);
//                            } else {
//                                adapter.addData(songs);
//                            }
                        }else{
                            listView.setIsLoadMore(false);
                            Toast.makeText(SongActivity.this, getString(R.string.no_more_toast), Toast.LENGTH_LONG).show();
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
     * @param refreshView
     */
    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        page = 0;
        isRefresh = true;
        listView.setIsLoadMore(true);
        initData(intent.getStringExtra("type"), intent.getStringExtra("singid"), intent.getStringExtra("songname"), page, rows);
    }
}
