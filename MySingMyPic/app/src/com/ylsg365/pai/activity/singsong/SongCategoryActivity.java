package com.ylsg365.pai.activity.singsong;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分类点歌界面
 */
public class SongCategoryActivity extends BaseActivity {

    private List<Map<String, String>> datas;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_category);

        setupToolbar();

        setTitle("分类点歌");

        listView = (ListView) findViewById(R.id.recycler_song_category);

        loadData();

        final EditText searchEdit = (EditText) findViewById(R.id.edit_search);
        ImageView search = (ImageView) findViewById(R.id.img_search);
        /**
         * 搜索歌曲
         */
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchEdit.getText().toString();
                if (StringUtil.isNull(keyword)) {
                    Toast.makeText(SongCategoryActivity.this, "请输入歌曲名进行搜索.", Toast.LENGTH_LONG).show();
                } else {
                    NavHelper.toSongActivity(SongCategoryActivity.this,keyword);
                }
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * 加载歌曲数据
     */
    private void loadData() {
        datas = new ArrayList<Map<String, String>>();
        YinApi.getSongType(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (JsonUtil.getBoolean(response, "status")) {
                    JSONArray array = JsonUtil.getJSONArray(response, "datas");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = JsonUtil.getJSONObject(array, i);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("name", JsonUtil.getString(obj, "typeName"));
                        map.put("code", JsonUtil.getString(obj, "typeId"));
                        datas.add(map);
                    }
                    CommonAdapter adapter = new CommonAdapter(SongCategoryActivity.this, datas, R.layout.item_song_list) {
                        @Override
                        public void convert(ViewHolder holder, Object item) {
                            holder.setText(R.id.song_name, ((Map) item).get("name").toString());
                        }
                    };

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            NavHelper.toSongActivity(SongCategoryActivity.this, datas.get(position).get("code"), datas.get(position).get("name"));
                        }
                    });
                    //实例化adapter并且设置给listview
                    listView.setAdapter(adapter);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

}
