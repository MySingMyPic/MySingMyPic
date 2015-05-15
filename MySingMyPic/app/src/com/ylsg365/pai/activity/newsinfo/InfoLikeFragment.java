package com.ylsg365.pai.activity.newsinfo;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.Listener.OnItemClickListener;
import com.ylsg365.pai.activity.base.TabFragment;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class InfoLikeFragment extends TabFragment implements OnItemClickListener {
    private SuperRecyclerView recyclerView;
    private int newsInfoId;
    private int forwardCount;
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    private boolean isRefresh = false;
    private boolean isLoad=true;

    public static InfoLikeFragment newInstance(int newsInfoId, int likeCount) {
        InfoLikeFragment fragment = new InfoLikeFragment();
        Bundle args = new Bundle();
        args.putInt("newsInfoId", newsInfoId);
        args.putInt("likeCount", likeCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            newsInfoId = getArguments().getInt("newsInfoId");
            forwardCount = getArguments().getInt("likeCount");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fresh_news_like, container, false);

        recyclerView = (SuperRecyclerView) rootView.findViewById(R.id.recycler_like);

        infoLikeAdapter = new InfoLikeAdapter(R.layout.item_info_like, infoList);
        infoLikeAdapter.setOnItemClickListener(InfoLikeFragment.this);
        recyclerView.setAdapter(infoLikeAdapter);

//        recyclerView.setHasFixedSize(true);
        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                if(isLoad){
                isRefresh = false;
                getNewInfoNices(newsInfoId,currentPage++,rows);
                }
                else
                {
                    recyclerView.setLoadingMore(false);
                    recyclerView.hideMoreProgress();
                }
            }
        },1);

//        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                Log.e("", "refresh");
//                isLoad=true;
//                prePage=-1;
//                currentPage = 0;
//                recyclerView.setLoadingMore(true);
//                isRefresh = true;
//                getNewInfoNices(newsInfoId, currentPage, rows);
//            }
//        });
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            Paint paint = new Paint();

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
                super.onDrawOver(c, parent, state);
                paint.setColor(getResources().getColor(R.color.line_radio));
                for (int i = 0, size = parent.getChildCount(); i < size; i++) {
                    if(i % 5 ==0){
                        View child = parent.getChildAt(i);
                        c.drawLine(child.getLeft(), child.getBottom(), parent.getRight(), child.getBottom(), paint);
                    }
                }
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                if(isLoad){
                    isRefresh = false;
                    getNewInfoNices(newsInfoId,currentPage++,rows);
                }
                else
                {
                    recyclerView.setLoadingMore(false);
                    recyclerView.hideMoreProgress();
                }

            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        });

        getNewInfoNices(newsInfoId, currentPage, rows);
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
        return String.format("礼物", this.forwardCount);
    }

    private int currentPage = 0;
    private static final int rows = 10;
    private InfoLikeAdapter infoLikeAdapter;
    private int prePage=-1;
    private void getNewInfoNices(int newsInfoId, int currentPage, final int rows){
        YinApi.getNewInfoGifts(1,newsInfoId, currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getNewInfoGifts", response.toString());

                int page=JsonUtil.getInt(response,"page");

                if (JsonUtil.getBoolean(response, "status")) {

                    if(page!=prePage){
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "gifts");
                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }

                    if(isRefresh)
                    {
                        recyclerView.removeAllViews();
                        infoLikeAdapter = new InfoLikeAdapter(R.layout.item_info_like, infoList);
                        infoLikeAdapter.setOnItemClickListener(InfoLikeFragment.this);
                        recyclerView.setAdapter(infoLikeAdapter);
                        isRefresh=false;
                    }
                    else{
                        infoLikeAdapter.addData(infoList);
                        infoLikeAdapter.notifyDataSetChanged();
                    }
                    if(infoList.size() < rows){
                        isLoad=false;
                        recyclerView.setLoadingMore(false);
//                        Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                    prePage=page;
                    }
                }

                recyclerView.hideMoreProgress();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                infoLikeAdapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();
            }
        });
    }

    @Override
    public void onItemClick(View view, int postion) {
        infoLikeAdapter.notifyDataSetChanged();
        recyclerView.hideMoreProgress();
    }
}
