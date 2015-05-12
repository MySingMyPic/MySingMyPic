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

public class InfoCommentFragment extends TabFragment implements OnItemClickListener {
    private SuperRecyclerView recyclerView;
    private int newsInfoId;
    private int forwardCount;
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    private boolean isRefresh = false;

    public static InfoCommentFragment newInstance(int newsInfoId, int commentCount) {
        InfoCommentFragment fragment = new InfoCommentFragment();
        Bundle args = new Bundle();
        args.putInt("newsInfoId", newsInfoId);
        args.putInt("commentCount", commentCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            newsInfoId = getArguments().getInt("newsInfoId");
            forwardCount = getArguments().getInt("commentCount");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fresh_news_comment, container, false);

        recyclerView = (SuperRecyclerView) rootView.findViewById(R.id.recycler_comment);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                isRefresh = false;
                getNewInfoComments(newsInfoId, currentPage++, rows);
            }
        },1);

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 0;
                recyclerView.setLoadingMore(true);
                isRefresh = true;
                getNewInfoComments(newsInfoId, currentPage, rows);
            }
        });



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
        commentAdapter = new CommentAdapter(getActivity(),R.layout.item_info_comment, infoList);
        commentAdapter.setOnItemClickListener(InfoCommentFragment.this);
        recyclerView.setAdapter(commentAdapter);
        getNewInfoComments(newsInfoId, currentPage, rows);
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
        return String.format("评论", this.forwardCount);
    }

    private int currentPage = 0;
    private static final int rows = 10;
    private CommentAdapter commentAdapter;

    private void getNewInfoComments(int newsInfoId, int currentPage, final int rows){
        YinApi.getNewInfoComments(newsInfoId, currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getNewInfoForwards", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if(isRefresh){
                        commentAdapter.clearData();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "comments");
                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }
                    commentAdapter.addData(infoList);
                    if(infoList.size() < rows){
                        recyclerView.setLoadingMore(false);
//                        Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                }
                commentAdapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                commentAdapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();
            }
        });
    }

    @Override
    public void onItemClick(View view, int postion) {

    }


}
