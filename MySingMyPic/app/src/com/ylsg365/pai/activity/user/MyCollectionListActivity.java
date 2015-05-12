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
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ylsg365 on 2015-03-23.
 */
public class MyCollectionListActivity extends BaseActivity{
    private SuperRecyclerView recyclerView;
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    private NewInfoAdapter adapter;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection_list);

        setupToolbar();

        setTitle("我的收藏");

        recyclerView = (SuperRecyclerView) findViewById(R.id.recycler_newsInfo);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        init();
        adapter = new NewInfoAdapter(this,R.layout.item_newsinfo, infoList);
        recyclerView.setAdapter(adapter );
        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                isRefresh = false;
                getMyCollection(currentPage++, rows);
            }
        },3);
        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 0;
                recyclerView.setLoadingMore(true);
                isRefresh = true;
                getMyCollection(currentPage, rows);
            }
        });
        getMyCollection(currentPage, rows);
    }

    private  int currentPage = 0;
    private static final int rows = 10;

    private void getMyCollection(int currentPage, final int rows){
        YinApi.getMyCollection(currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getMyCollection", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if(isRefresh){
                    adapter.clearData();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "records");
                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }
                    adapter.addData(infoList);
                    if(infoList.size()< rows){
                        recyclerView.setLoadingMore(false);
//                        Toast.makeText(MyCollectionListActivity.this, "没有更多数据", Toast.LENGTH_LONG).show();
                    }

                }
                adapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                recyclerView.hideMoreProgress();
            }
        });
    }
}
