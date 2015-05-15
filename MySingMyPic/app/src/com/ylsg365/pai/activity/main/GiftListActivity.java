package com.ylsg365.pai.activity.main;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.CommonAdapter;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.ViewHolder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 礼物列表
 */
@SuppressWarnings("rawtypes")
public class GiftListActivity extends BaseActivity implements
        PullToRefreshBase.OnRefreshListener {
    private PullToRefreshGridView gridView;
    private CommonAdapter adapter;
    private List<Map<String, String>> giftList = new ArrayList<Map<String, String>>();
    private int page = 0;
    private final int rows = 10;
    private boolean isRefresh = false;
    private boolean isLoad=true;
    private String houseId;
    private String receiveUserId;
    private int type = 0;

    @SuppressWarnings({
        "unchecked"
    })
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gift);

        setupToolbar();
        TextView textRight = (TextView) findViewById(R.id.text_right);
        textRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHelper.toMyGiftActivity(GiftListActivity.this);
            }
        });

        setTitle("所有礼物");

        gridView = (PullToRefreshGridView) findViewById(R.id.grid);

        type = getIntent().getIntExtra("type", 0);
        houseId = getIntent().getStringExtra("houseId");
        receiveUserId = getIntent().getStringExtra("receiveUserId");

        adapter = new CommonAdapter(GiftListActivity.this, giftList,
                R.layout.item_gift) {
            @Override
            public void convert(ViewHolder holder, Object item) {

                Map<String, String> data = ((Map<String, String>) item);
                ImageLoader.getInstance().displayImage(
                        Constants.WEB_IMG_DOMIN + data.get("img").toString(),
                        (ImageView) holder.getView(R.id.img_user_avatar));
                holder.setText(R.id.song_name, data.get("name"));
                holder.setText(R.id.money, data.get("money"));
            }
        };
        gridView.setAdapter(adapter);

        gridView.getRefreshableView().setSelector(android.R.color.transparent);
        gridView.setOnRefreshListener(this);
        // set mode to BOTH
        gridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        ILoadingLayout startLabels = gridView
                .getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");
        startLabels.setRefreshingLabel("加载中...");
        startLabels.setReleaseLabel("释放刷新");

        gridView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if(isLoad){
                isRefresh = false;
                page++;
                getGiftList();
                }
            }
        });
        /**
         * 点击跳到礼物详情
         */
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                String count = "1";
                String[] datas = new String[] {
                        giftList.get(position).get("name"),
                        giftList.get(position).get("img"),
                        giftList.get(position).get("money"),
                        giftList.get(position).get("nid")
                };

                if (type == 0)
                    NavHelper.toGiftInfoActivity(GiftListActivity.this, datas,
                            houseId, receiveUserId, count);
                else if (type == 1)
                    NavHelper.toGiftInfoActivityForNewsInfo(
                            GiftListActivity.this, datas, type, houseId, count);
            }
        });
        getGiftList();
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        isRefresh = true;
        isLoad=true;
        page = 0;
        gridView.setIsLoadMore(true);
        getGiftList();
    }

    /**
     * 获取礼物数据
     */
    private void getGiftList() {
        YinApi.getGiftList(page + "", rows + "",
                new Response.Listener<JSONObject>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("giftListD",response.toString());
                        if (JsonUtil.getBoolean(response, "status")) {
                            if (isRefresh) {
                                adapter.clearData();
                            }
                            JSONArray array = JsonUtil.getJSONArray(response,
                                    "gifts");
                            giftList.clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject json = JsonUtil.getJSONObject(array,
                                        i);
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("img",
                                        JsonUtil.getString(json, "imgUrl"));
                                map.put("name",
                                        JsonUtil.getString(json, "nname"));
                                map.put("money",
                                        JsonUtil.getString(json, "money"));
                                map.put("nid", JsonUtil.getString(json, "nid"));
                                giftList.add(map);
                            }

                            adapter.addData(giftList);

                            if (giftList.size() <= 0) {
                                isLoad=false;
                                gridView.setIsLoadMore(false);
                                Toast.makeText(GiftListActivity.this,
                                        getString(R.string.no_more_toast),
                                        Toast.LENGTH_LONG).show();
                            }

                        }

                        adapter.notifyDataSetChanged();
                        gridView.onRefreshComplete();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        adapter.notifyDataSetChanged();
                        gridView.onRefreshComplete();
                    }
                });
    }
}
