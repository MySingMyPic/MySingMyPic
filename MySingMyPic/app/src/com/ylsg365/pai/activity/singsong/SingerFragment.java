package com.ylsg365.pai.activity.singsong;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.TabFragment;
import com.ylsg365.pai.activity.dialog.MVUploadFragment;
import com.ylsg365.pai.activity.room.GameCenterActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.CommonAdapter;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.ViewHolder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingerFragment extends TabFragment implements AbsListView.OnItemClickListener, PullToRefreshBase.OnRefreshListener {
    private String mTitle;
    private String mCode;
    private int page = 0;
    private int rows = 10;
    private PullToRefreshListView listView;
    private CommonAdapter adapter;
    private ArrayList<Map<String, String>> songs = new ArrayList<Map<String, String>>();
    private boolean isRefresh = false;

    public static SingerFragment newInstance(String title,String code) {

        SingerFragment f = new SingerFragment();
        Bundle args = new Bundle();
        args.putString("title",title);
        args.putString("code",code);
        f.setArguments(args);

        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle =  getArguments().getString("title");
        mCode =  getArguments().getString("code");
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_singer, container, false);

        listView = (PullToRefreshListView) rootView.findViewById(R.id.recycler_singer);
        listView.getRefreshableView().setDivider(null);
        listView.getRefreshableView().setSelector(android.R.color.transparent);
        listView.setOnRefreshListener(this);
        // set mode to BOTH
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        ILoadingLayout startLabels = listView.getLoadingLayoutProxy( true,false);
        startLabels.setPullLabel("下拉刷新");
        startLabels.setRefreshingLabel("加载中...");
        startLabels.setReleaseLabel("释放刷新");

        adapter = new CommonAdapter(getActivity(), songs, R.layout.item_song) {

            @Override
            public void convert(ViewHolder holder, Object item) {
                holder.setText(R.id.song_name, ((HashMap) item).get("name").toString());
                holder.setText(R.id.song_time, ((HashMap) item).get("sing").toString());
                final String url = ((HashMap) item).get("url").toString();
                Button btn = (Button)(holder.getView(R.id.song_btn));
                btn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavHelper.toCappellaRecordPage(getActivity(), url);
                    }
                });
            }
        };
        /**
         * 自动加载更多数据
         */
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                isRefresh = false;
                page++;
                loadListData(mCode);
            }
        });
        listView.setAdapter(adapter);
        getActivity().getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadListData(mCode);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    private void loadListData(String code) {
        /**
         * 获取歌曲数据
         */
        YinApi.getSongs("", code, "", page, rows,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (JsonUtil.getBoolean(response, "status")) {
                            if(isRefresh){
                                adapter.clearData();
                            }
                            JSONArray array = JsonUtil.getJSONArray(response, "datas");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject json = JsonUtil.getJSONObject(array, i);
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("sing", JsonUtil.getString(json, "singerName"));
                                map.put("name", JsonUtil.getString(json, "songName"));
                                map.put("url", JsonUtil.getString(json, "songUrl"));
                                songs.add(map);
                            }
                            if (page == 1) {
                                adapter.setDatas(songs);
                            } else {
                                adapter.addData(songs);
                            }

                        }else{
                            listView.setIsLoadMore(false);
                            Toast.makeText(SingerFragment.this.getActivity(), getString(R.string.no_more_toast), Toast.LENGTH_LONG).show();
                        }
                        listView.onRefreshComplete();
                        adapter.notifyDataSetChanged();

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listView.onRefreshComplete();
                    }
                });
    }

    /**
     * 下拉刷新
     * @param refreshView
     */
    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        page = 0;
        listView.setIsLoadMore(true);
        isRefresh= true;
        loadListData(mCode);
    }
}