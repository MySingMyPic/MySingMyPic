package com.ylsg365.pai.activity.message;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.NewInfoAdapter;
import com.ylsg365.pai.activity.NumberedAdapter;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class CommentActivity extends BaseActivity {
    private SuperRecyclerView recyclerView;
    private NumberedAdapter adapter;
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        setupToolbar();

        setTitle("评论");

        recyclerView = (SuperRecyclerView) findViewById(R.id.recycler_comment);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                isRefresh= false;
                getMyComments(currentPage++,rows);
            }
        },1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NumberedAdapter(R.layout.item_comment, 30);
        recyclerView.setAdapter(adapter);

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 0;
                isRefresh = true;
                recyclerView.setLoadingMore(true);
                getMyComments(currentPage, rows);
            }
        });

        getMyComments(currentPage, rows);
    }

    private int currentPage = 0;
    private static final int rows = 10;

    private void getMyComments(int currentPage, int row){
        YinApi.getMyComments(currentPage, row, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getMyComments", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if(isRefresh){
                        //adapter.clear();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "data");
//                    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
//                    for (int i = 0; i < infoJsonArray.length(); i++) {
//                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
//                    }

//                    recyclerView.setAdapter(new NewInfoAdapter(R.layout.item_newsinfo, infoList));
//                    adapter.addData(infoList);

//                    if(infoList.size() < rows){
//                        recyclerView.setLoadingMore(false);
//                        Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_LONG).show();
//                    }
//                    messageAdapter.notifyDataSetChanged();

                }
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
