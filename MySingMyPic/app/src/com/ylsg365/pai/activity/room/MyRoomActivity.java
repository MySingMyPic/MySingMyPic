package com.ylsg365.pai.activity.room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.CommonAdapter;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.StringUtil;
import com.ylsg365.pai.util.ViewHolder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 我的包房界面
 */
public class MyRoomActivity extends BaseActivity {
    private PullToRefreshListView recyclerView;
    @SuppressWarnings("rawtypes")
    private CommonAdapter adapter ;
    private ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    private boolean isRefresh = false;

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karaoke);

        setupToolbar();


        setTitle("我的包房");

        final EditText searchEdit = (EditText) findViewById(R.id.edit_search);
        searchEdit.setHint("输入包房名进行搜索");
        ImageView search = (ImageView) findViewById(R.id.img_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchEdit.getText().toString();
                if (StringUtil.isNull(keyword)) {
                    Toast.makeText(MyRoomActivity.this, "请输入包房名进行搜索.", Toast.LENGTH_LONG).show();
                } else {
                    getMyHouse(keyword.trim(), 0, rows);
                }
            }
        });

        recyclerView = (PullToRefreshListView) findViewById(R.id.recycler_room);
        recyclerView.getRefreshableView().setDivider(null);
        recyclerView.getRefreshableView().setSelector(android.R.color.transparent);
        // set mode to BOTH
        recyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        ILoadingLayout startLabels = recyclerView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");
        startLabels.setRefreshingLabel("加载中...");
        startLabels.setReleaseLabel("释放刷新");
        /**
         * 设置自动加载更多
         */
        recyclerView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                isRefresh = false;
                getMyHouse(null, currentPage++, rows);
            }
        });
        /**
         * 下拉刷新监听
         */
        recyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                isRefresh = true;
                currentPage = 0;
                infoList.clear();
                adapter.notifyDataSetChanged();
                recyclerView.setIsLoadMore(true);
                getMyHouse(null, currentPage, rows);
            }
        });

        init();
        setupToolbar();
        TextView rightText = (TextView) findViewById(R.id.text_right);
        rightText.setText("创建包房");
        rightText.setVisibility(View.VISIBLE);
        rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHelper.toRoomCreateActivity(MyRoomActivity.this);
            }
        });

        adapter = new CommonAdapter(MyRoomActivity.this,infoList,R.layout.item_ktv_room) {
            @Override
            public void convert(ViewHolder holder, Object item) {

                ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN +JsonUtil.getString((JSONObject)item,"headImg"), (ImageView)holder.getView(R.id.img_roomImg));
                holder.setText(R.id.text_room_name,JsonUtil.getString((JSONObject)item,"nname"));
                holder.setText(R.id.text_room_author,JsonUtil.getString((JSONObject)item,"nickName"));
                holder.setText(R.id.text_room_hot,JsonUtil.getString((JSONObject)item,"singAuth"));
                holder.setText(R.id.text_room_user_count,JsonUtil.getString((JSONObject)item,"houseCount"));

            }
        };

        recyclerView.setAdapter(adapter);

        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavHelper.toRoomInfoActivity(MyRoomActivity.this, true, JsonUtil.getString(infoList.get(position-1),"nid"));
            }
        });

    }

    private  int currentPage = 0;
    private static final int rows = 10;

    /**
     * 获取我的包房列表
     *
     * @param gameName
     * @param currentPage
     * @param rows
     */
    private void getMyHouse(String gameName, int currentPage, final int rows) {
        YinApi.getMyHouse(gameName, currentPage + "", rows + "", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getMyHouse", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if(isRefresh){
                        adapter.clearData();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "houses");
//                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }
//                    adapter.addData(infoList);
                    if (infoList.size() < rows) {
                        recyclerView.setIsLoadMore(false);
//                        Toast.makeText(MyRoomActivity.this,"没有更多数据", Toast.LENGTH_LONG).show();
                    }

                }
                    adapter.notifyDataSetChanged();
                    recyclerView.onRefreshComplete();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                recyclerView.onRefreshComplete();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyHouse(null, currentPage, rows);
    }
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        currentPage = 0;
        infoList.clear();
        adapter.notifyDataSetChanged();
        getMyHouse(null, currentPage, rows);
    }
}
