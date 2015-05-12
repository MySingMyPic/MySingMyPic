package com.ylsg365.pai.activity.user;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

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
public class WorksListActivity extends BaseActivity {
    private SuperRecyclerView recyclerView;
    private int pageType;
    private int userId;
    private NewInfoAdapter adapter;
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_works);

        setupToolbar();


        recyclerView = (SuperRecyclerView) findViewById(R.id.recycler);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                isRefresh = false;
                pageType = getIntent().getIntExtra(NavHelper.REQUEST_CODE, 0);
                if (pageType == NavHelper.REQUEST_MY_WORKS) {
                    getMyWorks(currentPage++, rows);
                } else if (pageType == NavHelper.REQUEST_OTHER_WORKS) {
                    userId = getIntent().getIntExtra("userId", 0);
                    getOtherWorks(userId, currentPage++, rows);
                }
            }
        }, 3);

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 0;
                recyclerView.setLoadingMore(true);
                isRefresh = true;
                pageType = getIntent().getIntExtra(NavHelper.REQUEST_CODE, 0);
                if (pageType == NavHelper.REQUEST_MY_WORKS) {
                    getMyWorks(currentPage++, rows);
                } else if (pageType == NavHelper.REQUEST_OTHER_WORKS) {
                    userId = getIntent().getIntExtra("userId", 0);
                    getOtherWorks(userId, currentPage++, rows);
                }
            }
        });

        init();
        adapter = new NewInfoAdapter(this,R.layout.item_newsinfo, infoList);
        recyclerView.setAdapter(adapter);

        pageType = getIntent().getIntExtra(NavHelper.REQUEST_CODE, 0);
        if (pageType == NavHelper.REQUEST_MY_WORKS) {
            setTitle("我的作品");
            getMyWorks(currentPage, rows);
        } else if (pageType == NavHelper.REQUEST_OTHER_WORKS) {
            setTitle("他的作品");
            userId = getIntent().getIntExtra("userId", 0);
            getOtherWorks(userId, currentPage, rows);
        }
    }

    private int currentPage = 0;
    private static final int rows = 10;

    private void getMyWorks(int currentPage, final int rows) {
        YinApi.getMyWorks(currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getMyWorks", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if (isRefresh) {
                        adapter.clearData();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "records");
                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }

                    adapter.addData(infoList);
                    if (infoList.size() < rows) {
                        recyclerView.setLoadingMore(false);
                        Toast.makeText(WorksListActivity.this, "没有更多数据", Toast.LENGTH_LONG).show();
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

    private void getOtherWorks(int userId, int currentPage, int rows) {
        YinApi.getOhterOriginal(userId, currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getOtherWorks", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "records");
                    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }

                    recyclerView.setAdapter(new NewInfoAdapter(WorksListActivity.this,R.layout.item_newsinfo, infoList));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}
