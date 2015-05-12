package com.ylsg365.pai.activity.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.OriginalAdapter;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 原创基地页面
 */
public class OriginalBaseActivity extends BaseActivity {
    private SuperRecyclerView recyclerView;
    int pageType;
    int userId;
    private int currentPage = 0;
    private static final int rows = 10;
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    OriginalAdapter originalAdapter;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_original_base);

        setupToolbar();


        recyclerView = (SuperRecyclerView) findViewById(R.id.recycler_original);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                if (pageType == NavHelper.REQUEST_ALL_ORIGINAL) {
                    getALLOriginal(currentPage++, rows);
                } else if (pageType == NavHelper.REQUEST_MY_ORIGINAL) {
                    getMyOriginal(currentPage++, rows);

                } else if (pageType == NavHelper.REQUEST_OTHER_ORIGINAL) {
                    userId = getIntent().getIntExtra("userId", 0);
                    getOtherOriginal(userId, currentPage++, rows);
                }
                isRefresh = false;
            }
        },3);

        recyclerView.setOnMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                isRefresh = true;
                currentPage = 0;
                recyclerView.setLoadingMore(true);
                if (pageType == NavHelper.REQUEST_ALL_ORIGINAL) {
                    getALLOriginal(currentPage, rows);
                } else if (pageType == NavHelper.REQUEST_MY_ORIGINAL) {
                    getMyOriginal(currentPage, rows);

                } else if (pageType == NavHelper.REQUEST_OTHER_ORIGINAL) {
                    userId = getIntent().getIntExtra("userId", 0);
                    getOtherOriginal(userId, currentPage, rows);
                }
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        init();

        pageType = getIntent().getIntExtra(NavHelper.REQUEST_CODE, 0);

        originalAdapter = new OriginalAdapter(R.layout.item_original, infoList);
        recyclerView.setAdapter(originalAdapter);
        checkAdapterIsEmpty(originalAdapter);
        originalAdapter.setOnItemClickListener(new OriginalAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                NavHelper.toBuyTheCopyrightActivity(OriginalBaseActivity.this,JsonUtil.getString(((JSONObject)infoList.get(position)),"recordId"),JsonUtil.getString(((JSONObject)infoList.get(position)),"price")
                ,JsonUtil.getString(((JSONObject)infoList.get(position)),"nickName"),JsonUtil.getString(((JSONObject)infoList.get(position)),"songName"));
            }
        });

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        if (pageType == NavHelper.REQUEST_ALL_ORIGINAL) {
            setTitle("原创基地");
            getALLOriginal(currentPage, rows);
        } else if (pageType == NavHelper.REQUEST_MY_ORIGINAL) {
            setTitle("我的原创基地");
            getMyOriginal(currentPage, rows);

        } else if (pageType == NavHelper.REQUEST_OTHER_ORIGINAL) {
            userId = getIntent().getIntExtra("userId", 0);
            setTitle("他的原创基地");
            getOtherOriginal(userId, currentPage, rows);
        }


    }


    private void getMyOriginal(int currentPage, int row) {
        YinApi.getMyOriginal(currentPage, row, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getMyOriginal", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if(isRefresh){
                        originalAdapter.clear();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "records");
                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }
                    originalAdapter.addData(infoList);
                    checkAdapterIsEmpty(originalAdapter);
                    if(infoList.size() < rows){
                        recyclerView.setLoadingMore(false);
                        Toast.makeText(OriginalBaseActivity.this, "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                }
                originalAdapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                originalAdapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();
            }
        });
    }

    private void getALLOriginal(int currentPage, int row) {
        YinApi.getALLOriginal(currentPage, row, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getALLOriginal", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if(isRefresh){
                        originalAdapter.clear();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "records");
                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }
                    originalAdapter.addData(infoList);
                    checkAdapterIsEmpty(originalAdapter);
                    if(infoList.size() < rows){
                        recyclerView.setLoadingMore(false);
                        Toast.makeText(OriginalBaseActivity.this, "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                }
                originalAdapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                originalAdapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();
            }
        });
    }


    private void getOtherOriginal(int userId, int currentPage, final int row) {
        YinApi.getOhterOriginal(userId, currentPage, row, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getALLOriginal", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if(isRefresh){
                        originalAdapter.clear();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "records");
                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }
                    originalAdapter.addData(infoList);
                    checkAdapterIsEmpty(originalAdapter);
                    if(infoList.size() < rows){
                        recyclerView.setLoadingMore(false);
                        Toast.makeText(OriginalBaseActivity.this, "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                }
                originalAdapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                originalAdapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();
            }
        });
    }
}
