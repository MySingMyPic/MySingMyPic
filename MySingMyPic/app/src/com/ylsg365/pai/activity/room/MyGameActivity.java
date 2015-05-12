package com.ylsg365.pai.activity.room;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.CommonAdapter;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.ViewHolder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 已经参加的比赛
 */
public class MyGameActivity extends BaseActivity {
    private PullToRefreshListView recyclerView;
    private CommonAdapter adapter ;
    private ArrayList<JSONObject> infoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karaoke);

        setupToolbar();


        setTitle("已参加的比赛");

        recyclerView = (PullToRefreshListView) findViewById(R.id.recycler_room);

        init();

        getMyGames(null, currentPage, rows);

    }

    private static final int currentPage = 1;
    private static final int rows = 30;

    /**
     * 获取已参加的比赛
     *
     * @param gameName
     * @param currentPage
     * @param rows
     */
    private void getMyGames(String gameName, int currentPage, int rows) {
        YinApi.getGameCenterByUsers( currentPage + "", rows + "", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getMyGames", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "games");
                    if(infoJsonArray !=null){

                    infoList = new ArrayList<JSONObject>();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }

                    if (infoList.size() > 0) {

                        adapter = new CommonAdapter(MyGameActivity.this,infoList,R.layout.item_game_center) {
                            @Override
                            public void convert(ViewHolder holder, Object item) {

                                ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + JsonUtil.getString((JSONObject) item, "imagerUrl"), (ImageView) holder.getView(R.id.img_gameImg));
                                holder.setText(R.id.text_game_name,JsonUtil.getString((JSONObject)item,"nname"));
                                holder.setText(R.id.text_game_time,JsonUtil.getString((JSONObject)item,"startDate")+"--"+JsonUtil.getString((JSONObject)item,"endDate"));
                                holder.setText(R.id.text_works_count,JsonUtil.getString((JSONObject)item,"entriesNumber"));

                            }
                        };

                        recyclerView.setAdapter(adapter);

                        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    NavHelper.toGameCenterPage(MyGameActivity.this);
                            }
                        });
                    }
                    }
                }else {
                    String msg = JsonUtil.getString(response, "msg");
                    UIHelper.showToast(msg);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }


}
