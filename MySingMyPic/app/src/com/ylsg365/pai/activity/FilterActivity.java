package com.ylsg365.pai.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.util.CommonAdapter;
import com.ylsg365.pai.util.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 添加滤镜界面
 */
public class FilterActivity extends BaseActivity {

    private GridView mGridView;
    private List<Map<String,String>> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        setupToolbar();

        mGridView = (GridView)findViewById(R.id.grid);

        datas = new ArrayList<Map<String, String>>();
        datas.add(new HashMap<String, String>());
        datas.add(new HashMap<String, String>());
        datas.add(new HashMap<String, String>());
        datas.add(new HashMap<String, String>());

        int size = datas.size();
        int length = 100;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        mGridView.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        mGridView.setColumnWidth(itemWidth); // 设置列表项宽
        mGridView.setHorizontalSpacing(5); // 设置列表项水平间距
        mGridView.setStretchMode(GridView.NO_STRETCH);
        mGridView.setNumColumns(size); // 设置列数量=列表集合数

        mGridView.setAdapter(new CommonAdapter(FilterActivity.this,datas,R.layout.item_music_intervertion) {

            @Override
            public void convert(ViewHolder holder, Object item) {

            }
        });



    }



}
