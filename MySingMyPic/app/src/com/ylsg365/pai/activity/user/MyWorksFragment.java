package com.ylsg365.pai.activity.user;

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
import com.ylsg365.pai.activity.NewInfoAdapter;
import com.ylsg365.pai.activity.base.BaseFragment;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyWorksFragment extends BaseFragment implements OnItemClickListener {
    private SuperRecyclerView recyclerView;
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    private boolean isRefresh = false;


    public static MyWorksFragment newInstance(String param1, String param2) {
        MyWorksFragment fragment = new MyWorksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MyWorksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_works, container, false);
        TextView titileTextView = (TextView)rootView.findViewById(R.id.toolbar_title);
        titileTextView.setText("我的作品");
        rootView.findViewById(R.id.text_toolbar_left).setVisibility(View.GONE);
        rootView.findViewById(R.id.text_right).setVisibility(View.VISIBLE);

        recyclerView = (SuperRecyclerView) rootView.findViewById(R.id.recycler);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                isRefresh = false;
                getNewInfos(currentPage, rows);
            }
        },1);
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

        newInfoAdapter = new NewInfoAdapter(getActivity(),R.layout.item_home, infoList);
        newInfoAdapter.setOnItemClickListener(MyWorksFragment.this);
        recyclerView.setAdapter(newInfoAdapter);

        getNewInfos(currentPage++, rows);
        return rootView;
    }

    private  int currentPage = 0;
    private static final int rows = 10;
    private NewInfoAdapter newInfoAdapter;

    private void getNewInfos(int currentPage, final int rows){
        YinApi.getNewInfos(currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getNewInfos", response.toString());

                if(JsonUtil.getBoolean(response, "status")){
                    if(isRefresh){
                        newInfoAdapter.clearData();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "data");
                    infoList.clear();
                    for(int i=0; i<infoJsonArray.length(); i++){
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }

                    newInfoAdapter.addData(infoList);
                    if(infoList.size()< rows){
                        recyclerView.setLoadingMore(false);
                        Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                }
                newInfoAdapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                newInfoAdapter.notifyDataSetChanged();
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
            case 4:
                NavHelper.toNewsInfoDetailPage(getActivity(), newsInfoStr);
                break;
        }

    }
}
