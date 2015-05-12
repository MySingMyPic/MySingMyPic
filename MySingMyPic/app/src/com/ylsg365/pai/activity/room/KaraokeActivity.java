package com.ylsg365.pai.activity.room;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.singsong.SongActivity;
import com.ylsg365.pai.activity.singsong.SongCategoryActivity;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.UIHelper;
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
 * 包房K歌界面
 */
public class KaraokeActivity extends BaseActivity implements View.OnClickListener{
    private PullToRefreshListView recyclerView;
    private boolean isHaveRoom = false;
    private CommonAdapter adapter ;
    private ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();;
    private int currentPage = 0;
    private static final int rows = 10;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karaoke);

        setupToolbar();
        setTitle("包房K歌");

        TextView rightTextView = (TextView) findViewById(R.id.text_right);
        rightTextView.setVisibility(View.VISIBLE);
        rightTextView.setText("我的包房");
        rightTextView.setOnClickListener(this);

        recyclerView = (PullToRefreshListView) findViewById(R.id.recycler_room);

        recyclerView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                isRefresh = false;
                getKtvRoomList(null, currentPage++, rows);
            }
        });
        /**
         * 设置下拉刷新监听
         */
        recyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isRefresh = true;
                currentPage = 0;
                recyclerView.setIsLoadMore(true);
                getKtvRoomList(null, currentPage, rows);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });

        final EditText searchEdit = (EditText) findViewById(R.id.edit_search);
        searchEdit.setHint("输入包房名进行搜索");
        ImageView search = (ImageView) findViewById(R.id.img_search);
        /**
         * 通过名字搜索
         */
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchEdit.getText().toString();
                if (StringUtil.isNull(keyword)) {
                    Toast.makeText(KaraokeActivity.this, "请输入包房名进行搜索.", Toast.LENGTH_LONG).show();
                } else {
                    getKtvRoomList(keyword.trim(), 0, rows);
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        init();

        adapter = new CommonAdapter(KaraokeActivity.this,infoList,R.layout.item_ktv_room) {
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
                NavHelper.toRoomMainPage(KaraokeActivity.this);
            }
        });
        getKtvRoomList(null, currentPage, rows);
        getMyHouse("","1","1");
    }

    /**
     * 获得包房列表
     * @param gameName
     * @param currentPage
     * @param rows
     */
    private void getKtvRoomList(String gameName, int currentPage, final int rows){
        YinApi.getKtvRoomList(gameName, currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getKtvRoomList", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if(isRefresh){
                        adapter.clearData();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "houses");
                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }

                    adapter.addData(infoList);

                    if(infoList.size() < rows){
                        recyclerView.setIsLoadMore(false);
                        Toast.makeText(KaraokeActivity.this, getString(R.string.no_more_toast), Toast.LENGTH_LONG).show();
                    }

                }else {
                    String msg = JsonUtil.getString(response, "msg");
                    UIHelper.showToast(msg);
                }
                recyclerView.onRefreshComplete();
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                recyclerView.onRefreshComplete();
            }
        });
    }

    /**
     * 获取我的包房列表
     * @param gameName
     * @param currentPage
     * @param rows
     */
    private void getMyHouse(String gameName, String currentPage, String rows){
        YinApi.getMyHouse(gameName, currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getMyHouse", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "houses");

                    if(infoJsonArray.length()>0){
                        isHaveRoom =true;
                    }else {
                        isHaveRoom =false;
                    }
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_right:

                if(isHaveRoom){
                    NavHelper.toMyRoomActivity(KaraokeActivity.this);
                }else{
                    NavHelper.toRoomCreateActivity(KaraokeActivity.this);
                }
                break;

        }
    }
}
