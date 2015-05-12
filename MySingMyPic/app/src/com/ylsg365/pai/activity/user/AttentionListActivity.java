package com.ylsg365.pai.activity.user;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.Listener.OnViewClickListener;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.app.YinApplication;
import com.ylsg365.pai.util.DensityUtil;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AttentionListActivity extends BaseActivity implements OnViewClickListener{
    private SuperRecyclerView recyclerView;
    private AttentionsAdapter attentionsAdapter;
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    private boolean isRefresh = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_priority);

        setupToolbar();

        setTitle("关注");

        recyclerView = (SuperRecyclerView) findViewById(R.id.recycler_singer);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            Paint paint = new Paint();

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
                paint.setColor(getResources().getColor(R.color.line_radio));
                for (int i = 0, size = parent.getChildCount(); i < size; i++) {
                    View child = parent.getChildAt(i);
                    c.drawLine(child.getLeft() + DensityUtil.dip2px(YinApplication.getInstance(), 15), child.getBottom(), child.getRight(), child.getBottom(), paint);
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
//        recyclerView.setAdapter(new NumberedAdapter(R.layout.item_user, 30));
        attentionsAdapter = new AttentionsAdapter(R.layout.item_user_attention, infoList);
        recyclerView.setAdapter(attentionsAdapter);
        attentionsAdapter.setOnViewClickListener(AttentionListActivity.this);
        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                isRefresh = false;
                getAttentions(currentPage++, rows);
            }
        },1);

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 0;
                recyclerView.setLoadingMore(true);
                isRefresh = true;
                getAttentions(currentPage, rows);
            }
        });

        getAttentions(currentPage, rows);
    }

    private  int currentPage = 0;
    private static final int rows = 10;

    private void getAttentions(int currentPage, final int rows){
        YinApi.getAttentions(currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getAttentions", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if(isRefresh){
                        attentionsAdapter.clear();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "data");
                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }

                    attentionsAdapter.addData(infoList);
                    if(infoList.size() < rows){

                    }else{
                        recyclerView.setLoadingMore(false);
                        Toast.makeText(AttentionListActivity.this, "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                }
                attentionsAdapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                recyclerView.hideMoreProgress();
            }
        });
    }

    @Override
    public void onViewClick(View view, int postion) {
        JSONObject jsonObject = attentionsAdapter.getItem(postion);
        boolean isAttention = JsonUtil.getBoolean(jsonObject, "attention");
        int userId = JsonUtil.getInt(jsonObject, "userId");
        if(isAttention){
            unAttentionToUser(userId, postion, jsonObject);
        }else {
            attentionToUser(userId, postion, jsonObject);
        }

    }

    private void attentionToUser(int userId,final int postion, final JSONObject jsonObject){
        YinApi.attentionToUser(userId, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("attentionToUser", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    UIHelper.showToast("关注成功");
                    attentionsAdapter.notifyItemChanged(postion);
                    JsonUtil.put(jsonObject, "attention", true);
                } else {
                    UIHelper.showToast("操作失败");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UIHelper.showToast("操作失败");
            }
        });
    }

    private void unAttentionToUser(int userId, final int postion, final JSONObject jsonObject){
        YinApi.unAttentionToUser(userId+"", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("unAttentionToUser", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    UIHelper.showToast("取消关注成功");
                    attentionsAdapter.notifyItemChanged(postion);
                    JsonUtil.put(jsonObject, "attention", false);
                } else {
                    UIHelper.showToast("操作失败");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UIHelper.showToast("操作失败");
            }
        });
    }

}
