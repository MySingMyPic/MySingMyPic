package com.ylsg365.pai.activity.user;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.NewInfoAdapter;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ylsg365 on 2015-03-23.
 */
public class NewsInfoListActivity extends BaseActivity{
    private SuperRecyclerView recyclerView;
    private int pageType;
    private int userId;
    private NewInfoAdapter adapter;
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    private boolean isRefresh = false;
    private boolean isLoad=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsinfo_list);

        setupToolbar();

        recyclerView = (SuperRecyclerView) findViewById(R.id.recycler_newsInfo);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        init();

        adapter = new NewInfoAdapter(this,R.layout.item_newsinfo, infoList);
        recyclerView.setAdapter(adapter);

        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

                if(isLoad) {
                    pageType = getIntent().getIntExtra(NavHelper.REQUEST_CODE, 0);
                    if (pageType == NavHelper.REQUEST_MY_NEWSINFO) {
                        getMyNewsInfos(currentPage++, rows);
                    } else if (pageType == NavHelper.REQUEST_OTHER_NEWSINFO) {
                        userId = getIntent().getIntExtra("userId", 0);
                        getOtherNewsInfos(userId, currentPage++, rows);
                    } else if (pageType == NavHelper.REQUEST_NICE_NEWSINFO) {
                        getNiceNewsInfos(currentPage++, rows);
                    }
                }
                else
                {
                    recyclerView.setLoadingMore(false);
                    recyclerView.hideMoreProgress();
                }
            }
        },3);

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoad=true;
                currentPage = 0;
                recyclerView.setLoadingMore(true);
                isRefresh = true;
                pageType = getIntent().getIntExtra(NavHelper.REQUEST_CODE, 0);
                if(pageType == NavHelper.REQUEST_MY_NEWSINFO){
                    getMyNewsInfos(currentPage, rows);
                }else if(pageType == NavHelper.REQUEST_OTHER_NEWSINFO){
                    userId = getIntent().getIntExtra("userId", 0);
                    getOtherNewsInfos(userId, currentPage, rows);
                }else if(pageType == NavHelper.REQUEST_NICE_NEWSINFO) {
                    getNiceNewsInfos(currentPage, rows);
                }
            }
        });

        pageType = getIntent().getIntExtra(NavHelper.REQUEST_CODE, 0);
        if(pageType == NavHelper.REQUEST_MY_NEWSINFO){
            setTitle("我的新鲜事");
            getMyNewsInfos(currentPage, rows);
        }else if(pageType == NavHelper.REQUEST_OTHER_NEWSINFO){
            setTitle("他的新鲜事");
            userId = getIntent().getIntExtra("userId", 0);
            getOtherNewsInfos(userId, currentPage, rows);
        }else if(pageType == NavHelper.REQUEST_NICE_NEWSINFO) {
            setTitle("赞");
            getNiceNewsInfos(currentPage, rows);
        }

    }

    private int currentPage = 0;
    private static final int rows = 10;

    private void getMyNewsInfos(int currentPage, final int rows){
        YinApi.getMyNewsInfos(currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getMyNewsInfos", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if(isRefresh){
                        adapter.clearData();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "data");
                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }

                    adapter.addData(infoList);
                    checkAdapterIsEmpty(adapter);
                    if(infoList.size() < rows){
                        isLoad=false;
                        recyclerView.setLoadingMore(false);
//                        Toast.makeText(NewsInfoListActivity.this, "没有更多数据", Toast.LENGTH_LONG).show();
                    }

                }
                adapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                adapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();
            }
        });
    }

    private void getNiceNewsInfos(int currentPage, int rows){
        YinApi.getNiceNewInfos(currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getNiceNewInfos", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "data");
                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }

                    if(infoList.size() > 0){
                        adapter.addData(infoList);
                        checkAdapterIsEmpty(adapter);
                        adapter.notifyDataSetChanged();
                        recyclerView.hideMoreProgress();
                    }else{
                        recyclerView.setLoadingMore(false);
                        recyclerView.hideMoreProgress();
//                        Toast.makeText(NewsInfoListActivity.this, "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void getOtherNewsInfos(int userId, int currentPage, int rows){
        YinApi.getOtherNewsInfos(userId, currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getOtherNewsInfos", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "data");
                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }

                    if(infoList.size() > 0){
                        adapter.addData(infoList);
                        checkAdapterIsEmpty(adapter);
                        adapter.notifyDataSetChanged();
                        recyclerView.hideMoreProgress();
                    }else{
                        recyclerView.setLoadingMore(false);
                        recyclerView.hideMoreProgress();
//                        Toast.makeText(NewsInfoListActivity.this, "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}
