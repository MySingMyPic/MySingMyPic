package com.ylsg365.pai.activity.singsong;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.base.TabFragment;
import com.ylsg365.pai.activity.view.SlidingTabLayout;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 歌星点歌页面
 */
public class SingerActivity extends BaseActivity {
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ArrayList<TabFragment> fragments ;
    private ViewPager_Adapter viewPager_Adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer);

        setupToolbar();

        setTitle("歌星点歌");

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.pager_singer);

        loadData();

        final EditText searchEdit = (EditText) findViewById(R.id.edit_search);
        ImageView search = (ImageView) findViewById(R.id.img_search);
        /**
         * 搜索框
         */
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchEdit.getText().toString();
                if (StringUtil.isNull(keyword)) {
                    Toast.makeText(SingerActivity.this, "请输入歌曲名进行搜索.", Toast.LENGTH_LONG).show();
                } else {
                    NavHelper.toSongActivity(SingerActivity.this, keyword);
                }
            }
        });
    }


    public class ViewPager_Adapter extends FragmentPagerAdapter {

        private ArrayList<TabFragment> fragments;

        public ViewPager_Adapter(FragmentManager fm, ArrayList<TabFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public TabFragment getItem(int pos) {
            return fragments.get(pos);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getItem(position).getTitle();
        }
    }


    private void loadData() {
        /**
         * 获取歌星类别
         */
        YinApi.getSingerType(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (JsonUtil.getBoolean(response, "status")) {
                    JSONArray array = JsonUtil.getJSONArray(response, "datas");
                    fragments = new ArrayList<TabFragment>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = JsonUtil.getJSONObject(array, i);
                        String name = JsonUtil.getString(obj, "typeName");
                        String code = JsonUtil.getString(obj, "typeId");
                        fragments.add(SingerFragment.newInstance(name, code));
                    }

                    // 设置ViewPager

                    viewPager_Adapter = new ViewPager_Adapter(getSupportFragmentManager(),
                            fragments);
                    viewPager.setOffscreenPageLimit(fragments.size());
                    viewPager.setAdapter(viewPager_Adapter);

                    // 设置SlidingTab
                    slidingTabLayout.setViewPager(viewPager);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}
