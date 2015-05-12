package com.ylsg365.pai.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.CommonAdapter;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.ViewHolder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 悄悄关注
 */
public class MyAttentionsActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener {
    private ArrayList<Map<String, String>> songs = new ArrayList<Map<String, String>>();
    private Intent intent;
    private int page = 0;
    private final int rows = 10;
    private PullToRefreshListView listView;
    private CommonAdapter adapter;
    private boolean isRefresh = false;
    private int type = 0;  //0 悄悄关注      1 关注

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        setupToolbar();

        setTitle("悄悄关注");

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
                initData(page, rows);
            }
        });


        initData(page, rows);
        /**
         * adapter实例化
         */
        adapter = new CommonAdapter(MyAttentionsActivity.this, songs, R.layout.item_user_attention) {

            @Override
            public void convert(ViewHolder holder, Object item) {

                holder.setText(R.id.text_nickName, ((HashMap) item).get("nickName").toString());
                holder.setText(R.id.btn_attention, "取消关注");

                if(((HashMap) item).get("headImg") != null){
                    ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + ((HashMap) item).get("headImg").toString(), (ImageView) holder.getView(R.id.img_headImg));
                }

            }
        };
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (songs.size() <= position) {
                    return;
                }
                YinApi.unAttentionToUser(songs.get(position).get("userId"), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (JsonUtil.getBoolean(response, "status")) {
                            NavHelper.showToast(MyAttentionsActivity.this, "取消关注成功!");
                            songs.remove(position);
                            adapter.notifyDataSetChanged();
                            listView.onRefreshComplete();
                        } else {
                            NavHelper.showToast(MyAttentionsActivity.this, "取消关注失败,请稍后重试!");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NavHelper.showToast(MyAttentionsActivity.this, "取消关注失败,请稍后重试!");
                        adapter.notifyDataSetChanged();
                        listView.onRefreshComplete();
                    }
                });
            }
        });
        listView.setAdapter(adapter);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * 初始关注数据
     *
     * @param page
     * @param rows
     */
    private void initData(final int page, final int rows) {
        YinApi.getAttentions(page, rows,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (JsonUtil.getBoolean(response, "status")) {
                            if (isRefresh) {
                                adapter.clearData();
                            }
                            JSONArray array = JsonUtil.getJSONArray(response, "data");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject json = JsonUtil.getJSONObject(array, i);
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("headImg", JsonUtil.getString(json, "headImg"));
                                map.put("userId", JsonUtil.getString(json, "userId"));
                                map.put("nickName", JsonUtil.getString(json, "nickName"));

                                if (JsonUtil.getInt(json, "type") == type) {
                                    songs.add(map);
                                }
                            }
                            if (page == 1) {
                                adapter.setDatas(songs);
                            } else {
                                adapter.addData(songs);
                            }
                        } else {
                            listView.setIsLoadMore(false);
                            Toast.makeText(MyAttentionsActivity.this, getString(R.string.no_more_toast), Toast.LENGTH_LONG).show();
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
        initData(page, rows);
    }
}

