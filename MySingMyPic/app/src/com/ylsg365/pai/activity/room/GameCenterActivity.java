package com.ylsg365.pai.activity.room;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import com.ylsg365.pai.util.DateUtil;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.ViewHolder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 比赛中心界面
 */
public class GameCenterActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener{
    private PullToRefreshListView listView;
    private CommonAdapter adapter;
    private ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    private int currentPage = 0;
    private static final int rows = 10;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karaoke);

        setupToolbar();
        TextView rightTextView = (TextView) findViewById(R.id.text_right);
        rightTextView.setVisibility(View.VISIBLE);
        rightTextView.setText("我参加的比赛");
        rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    NavHelper.toMyGameActivity(GameCenterActivity.this);
            }
        });

        setTitle("比赛中心");

        listView = (PullToRefreshListView) findViewById(R.id.recycler_room);
        /**
         * 自动加载更多
         */
        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                getGameCenters(null, currentPage++, rows);
            }
        });
        listView.getRefreshableView().setDivider(null);
        listView.getRefreshableView().setSelector(android.R.color.transparent);
        listView.setOnRefreshListener(this);
        // set mode to BOTH
        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        ILoadingLayout startLabels = listView.getLoadingLayoutProxy( true,false);
        startLabels.setPullLabel("下拉刷新");
        startLabels.setRefreshingLabel("加载中...");
        startLabels.setReleaseLabel("释放刷新");
        /**
         * 比赛中心adapter
         */
        adapter = new CommonAdapter(GameCenterActivity.this,infoList,R.layout.item_game_center) {
            @Override
            public void convert(ViewHolder holder, Object item) {
                String startDate,endDate;
                startDate = JsonUtil.getString((JSONObject)item,"startDate");
                endDate = JsonUtil.getString((JSONObject)item,"endDate");
                startDate = DateUtil.format(DateUtil.parseDate(startDate), "MM.dd");
                endDate =  DateUtil.format(DateUtil.parseDate(endDate),"MM.dd");

                ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + JsonUtil.getString((JSONObject) item, "imagerUrl"), (ImageView) holder.getView(R.id.img_gameImg));
                holder.setText(R.id.text_game_name,JsonUtil.getString((JSONObject)item,"nname"));
                holder.setText(R.id.text_game_time,startDate+"--"+endDate);
                holder.setText(R.id.text_works_count,JsonUtil.getString((JSONObject)item,"entriesNumber"));

            }
        };

        listView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                isRefresh = false;
                getGameCenters(null, currentPage++, rows);
            }
        });

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavHelper.toGameInfoActivity(GameCenterActivity.this, JsonUtil.getString((infoList.get(position)), "nid"));
            }
        });

        init();

        getGameCenters(null, currentPage, rows);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * 获取比赛中心列表，搜索
     * @param gameName
     * @param currentPage
     * @param rows
     */
    private void getGameCenters(String gameName, int currentPage, final int rows) {
        YinApi.getGameCenters(gameName, currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getGameCenters", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if(isRefresh){
                        adapter.clearData();
                    }
                    infoList.clear();
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "games");
                    if(infoJsonArray == null){
                        return;
                    }
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }

                    if(infoList != null && infoList.size() >0){
                        if(infoList.size() < rows){
                            listView.setIsLoadMore(false);
                            Toast.makeText(GameCenterActivity.this,getString(R.string.no_more_toast), Toast.LENGTH_LONG).show();
                        }
                        adapter.addData(infoList);
                    }
                }
                listView. onRefreshComplete();
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listView. onRefreshComplete();
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 下拉刷新
     * @param refreshView
     */
    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        currentPage = 0;
        listView.setIsLoadMore(true);
        isRefresh = true;
        getGameCenters(null, currentPage, rows);
    }
}
