package com.ylsg365.pai.activity.message;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SystemMessageActivity extends BaseActivity {
    private SuperRecyclerView recyclerView;
    private int currentPage = 0;
    private static final int rows = 10;
    private SystemMessageAdapter adapter;
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    private boolean isRefresh = false;
    private boolean isEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_message);

        setupToolbar();

        setTitle("系统消息");

        recyclerView = (SuperRecyclerView) findViewById(R.id.recycler_message);
        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                if(isEnd){
                    return;
                }
                isRefresh = false;
                getSystemMessageList(currentPage++,rows);
            }
        },3);
        adapter = new SystemMessageAdapter(R.layout.item_message_system, infoList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 0;
                recyclerView.setLoadingMore(true);
                isRefresh = true;
                getSystemMessageList(currentPage, rows);
            }
        });

        getSystemMessageList(currentPage, rows);

    }

    private void getSystemMessageList(int currentPage, int row) {
        YinApi.getSystemMessageList(currentPage, row, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getSystemMessageList", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if (isRefresh) {
                        adapter.clear();
                        infoList.clear();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "msgs");

                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }
                    adapter.addData(infoList);

                if(infoList.size() < rows){
                    isEnd = true;
                    recyclerView.setLoadingMore(false);
//                    Toast.makeText(SystemMessageActivity.this, "没有更多数据", Toast.LENGTH_LONG).show();
                }
                    adapter.notifyDataSetChanged();

                }
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

}
