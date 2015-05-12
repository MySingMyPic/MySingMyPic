package com.ylsg365.pai.activity.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.Listener.OnItemClickListener;
import com.ylsg365.pai.activity.Listener.OnMyItemClickListener;
import com.ylsg365.pai.activity.Listener.OnUserHeadClickListener;
import com.ylsg365.pai.activity.NewInfoAdapter;
import com.ylsg365.pai.activity.base.BaseFragment;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.SystemBarTintManager;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends BaseFragment implements OnItemClickListener, OnUserHeadClickListener,OnMyItemClickListener{
    private SuperRecyclerView recyclerView;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    private int currentPage = 0;
    private final int rows = 10;
    private NewInfoAdapter newInfoAdapter;
    private boolean isRefresh = false;

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }


//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//
//        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            ((BaseActivity)getActivity()).setTranslucentStatus(true);
//        }


        SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.purple);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView titileTextView = (TextView)rootView.findViewById(R.id.toolbar_title);
        titileTextView.setText("新鲜事");
        rootView.findViewById(R.id.text_toolbar_left).setVisibility(View.GONE);
        rootView.findViewById(R.id.text_right).setVisibility(View.VISIBLE);

        recyclerView = (SuperRecyclerView) rootView.findViewById(R.id.recycler_song_category);
//        recyclerView.setHasFixedSize(true);

        newInfoAdapter = new NewInfoAdapter(getActivity(),R.layout.item_home, infoList);
        newInfoAdapter.setOnItemClickListener(HomeFragment.this);
        newInfoAdapter.setOnUserHeadClickListener(HomeFragment.this);
        newInfoAdapter.setOnMyItemClickListener(HomeFragment.this);
        recyclerView.setAdapter(newInfoAdapter);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                isRefresh = false;
                    getNewInfos(currentPage++, rows);
            }
        }, 3);

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 0;
                recyclerView.setLoadingMore(true);
                isRefresh = true;
                getNewInfos(currentPage, rows);
            }
        });

        init();

        getNewInfos(currentPage, rows);
        return rootView;
    }


    private void getNewInfos(final int currentPage, final int rows){
        YinApi.getNewInfos(currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getNewInfos", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "data");
                    if (isRefresh) {
                        newInfoAdapter.clearData();
                    }
                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }
                    newInfoAdapter.addData(infoList);
                    if (infoList.size() < rows) {
                        recyclerView.setLoadingMore(false);
                        Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                    newInfoAdapter.notifyDataSetChanged();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onItemClick(View view, int postion) {
        String newsInfoStr = newInfoAdapter.getItem(postion).toString();

        JSONObject infoObj = JsonUtil.getJSONObject(newsInfoStr);
        int infoType = JsonUtil.getInt(infoObj, "ntype");

        switch (infoType){
            case 0:
            case 1:
            case 2:
            case 3:
                NavHelper.toVideoDetailPage(getActivity(), newsInfoStr);
                break;
            case 4:
                NavHelper.toNewsInfoDetailPage(getActivity(), newsInfoStr);
                break;

        }

    }


    /**
     * 点解列表中头像回调方法
     * @param view
     * @param postion
     */
    @Override
    public void onUserHeadClick(View view, int postion) {
        String newsInfoStr = newInfoAdapter.getItem(postion).toString();

        JSONObject infoObj = JsonUtil.getJSONObject(newsInfoStr);
        int userId = JsonUtil.getInt(infoObj, "userId");

        NavHelper.toUserDetailPage(getActivity(), userId);
    }
    /**
     * item 收藏点击
     * @param view
     * @param postion
     */
    @Override
    public void OnCollectClick(View view, int postion) {
        String newsInfoStr = newInfoAdapter.getItem(postion).toString();
        JSONObject infoObj = JsonUtil.getJSONObject(newsInfoStr);
        int nid = JsonUtil.getInt(infoObj, "nid");
        collectNewsInfo(nid);
    }
    /**
     * item 点击
     * @param view
     * @param postion
     */
    @Override
    public void OnShareClick(View view, int postion) {

    }
    /**
     * item 点击
     * @param view
     * @param postion
     */
    @Override
    public void OnMsgClick(View view, int postion) {

    }

    /**
     * item 送礼物点击
     * @param view
     * @param postion
     */
    @Override
    public void OnGiftClick(View view, int postion) {

        String newsInfoStr = newInfoAdapter.getItem(postion).toString();
        JSONObject infoObj = JsonUtil.getJSONObject(newsInfoStr);
        int userId = JsonUtil.getInt(infoObj, "userId");

        NavHelper.toGiftListActivity(HomeFragment.this.getActivity(),"",userId+"");
    }


    private void collectNewsInfo(int nid){
        YinApi.collectNewsInfo(nid, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("collectNewsInfo", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    UIHelper.showToast("收藏成功");

//                    attentionTextView.setText("取消关注");
                } else {
                    UIHelper.showToast("收藏失败");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UIHelper.showToast("收藏失败");
            }
        });
    }

    private void unCollectNewsInfo(){
//        YinApi.unAttentionToUser(userId + "", new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                LogUtil.logd("unAttentionToUser", response.toString());
//
//                if (JsonUtil.getBoolean(response, "status")) {
//                    UIHelper.showToast("取消关注成功");
//
//                    attentionTextView.setText("关注");
//                } else {
//                    UIHelper.showToast("操作失败");
//                }
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                UIHelper.showToast("操作失败");
//            }
//        });
    }
}
