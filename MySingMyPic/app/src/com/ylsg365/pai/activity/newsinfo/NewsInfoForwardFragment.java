package com.ylsg365.pai.activity.newsinfo;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ylsg365.pai.R;

import com.ylsg365.pai.activity.Listener.OnItemClickListener;
import com.ylsg365.pai.activity.base.TabFragment;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.app.YinApplication;
import com.ylsg365.pai.util.DensityUtil;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsInfoForwardFragment extends TabFragment implements OnItemClickListener {
    private SuperRecyclerView recyclerView;
    private int newsInfoId;
    private int forwardCount;
    private boolean isRefresh = false;
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();

    public static NewsInfoForwardFragment newInstance(int newsInfoId, int forwardCount) {
        NewsInfoForwardFragment fragment = new NewsInfoForwardFragment();
        Bundle args = new Bundle();
        args.putInt("newsInfoId", newsInfoId);
        args.putInt("forwardCount", forwardCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            newsInfoId = getArguments().getInt("newsInfoId");
            forwardCount = getArguments().getInt("forwardCount");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fresh_news_forword, container, false);

        recyclerView = (SuperRecyclerView) rootView.findViewById(R.id.recycler_forward);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                isRefresh = false;
                Toast.makeText(getActivity(),"setupMoreListener", Toast.LENGTH_LONG).show();
            }
        },5);
        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 0;
                recyclerView.setLoadingMore(true);
                isRefresh = true;
                getNewInfoForwards(newsInfoId, currentPage, rows);
            }
        });

        forwardAdapter = new ForwardAdapter(getActivity(),R.layout.item_forward, infoList);
        forwardAdapter.setOnItemClickListener(NewsInfoForwardFragment.this);
        recyclerView.setAdapter(forwardAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            Paint paint = new Paint();

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
                super.onDrawOver(c, parent, state);
                paint.setColor(getResources().getColor(R.color.line_radio));
                for (int i = 0, size = parent.getChildCount(); i < size; i++) {
                    View child = parent.getChildAt(i);
                    c.drawLine(child.getLeft() + DensityUtil.dip2px(YinApplication.getInstance(), 15), child.getBottom(), child.getRight(), child.getBottom(), paint);
                }
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        });

        getNewInfoForwards(newsInfoId, currentPage, rows);
        return rootView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public String getTitle() {
        return String.format("转发", this.forwardCount);
    }

    private int currentPage = 0;
    private static final int rows = 10;
    private ForwardAdapter forwardAdapter;

    private void getNewInfoForwards(int newsInfoId, int currentPage, final int rows){
        YinApi.getNewInfoForwards(newsInfoId, currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getNewInfoForwards", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if(isRefresh){
                        forwardAdapter.clear();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "forwards");
                   infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }

                    forwardAdapter.addData(infoList);
                    if(infoList.size() < rows){
                        recyclerView.setLoadingMore(false);
//                        Toast.makeText(getActivity(),"没有更多数据", Toast.LENGTH_LONG).show();
                    }
                    forwardAdapter.notifyDataSetChanged();
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

    @Override
    public void onItemClick(View view, int postion) {

    }
}
