package com.ylsg365.pai;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.dialog.BuySuccessDailogFragment;
import com.ylsg365.pai.activity.dialog.NoticeConfirmFragment;
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
 * open VIP以及选择时间界面
 */
public class OpenVipSelectActivity extends BaseActivity {
    private ListView listView;
    private CommonAdapter adapter;
    private List<Map<String, String>> data;
    private BuySuccessDailogFragment vipFragment;
    private String money;
    private String nid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_vip);

        setupToolbar();
        setTitle("购买会员");
        TextView leftTextView = (TextView) findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);

        listView = (ListView) findViewById(R.id.recycler_song);
        data = new ArrayList<>();
        adapter = new CommonAdapter(OpenVipSelectActivity.this, data, R.layout.item_open_vip) {
            @Override
            public void convert(ViewHolder holder, Object item) {
                Map<String, String> mapItem = (Map<String, String>) item;
                holder.setText(R.id.vip_name, mapItem.get("name") + "个月");
                holder.setText(R.id.money_btn, mapItem.get("money") + "音乐币");
            }
        };

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                money = data.get(position).get("money");
                nid = data.get(position).get("id");

                NoticeConfirmFragment vipFragment = NoticeConfirmFragment.newInstance("确认花费" + money + "音乐币开通会员么？");
                vipFragment.show(OpenVipSelectActivity.this.getSupportFragmentManager(), "NoticeConfirmFragment");
                vipFragment.setOnMyClickListener(new NoticeConfirmFragment.OnMyClickListener() {
                    @Override
                    public void onMyOkClick() {
                        YinApi.openMeal(nid, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (JsonUtil.getBoolean(response, "status")) {
                                    BuySuccessDailogFragment vipFragment = BuySuccessDailogFragment.newInstance("已扣除" + money + "音乐币\n您可以进入个人中心查看直接购买的歌曲");
                                    vipFragment.show(OpenVipSelectActivity.this.getSupportFragmentManager(), "NoticeConfirmFragment");

                                    TimeCount timeCount = new TimeCount(3 * 1000, 1000, vipFragment);
                                    timeCount.start();
                                }else{
                                    NavHelper.showToast(OpenVipSelectActivity.this, JsonUtil.getString(response,"msg"));
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                NavHelper.showToast(OpenVipSelectActivity.this, "开通失败,请稍后重试!");
                            }
                        });

                    }

                    @Override
                    public void onMyCancelClick() {
//                        finish();
                    }
                });
            }
        });

        listView.setAdapter(adapter);
        initData();
    }

    private void initData() {
        YinApi.getMeals(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (JsonUtil.getBoolean(response, "status")) {

                    JSONArray array = JsonUtil.getJSONArray(response, "meals");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = JsonUtil.getJSONObject(array, i);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("money", JsonUtil.getString(json, "mealMoney"));
                        map.put("name", JsonUtil.getString(json, "mealTime"));
                        map.put("id", JsonUtil.getString(json, "nid"));
                        data.add(map);
                    }
                    adapter.setDatas(data);
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        private BuySuccessDailogFragment vipFragment;

        public TimeCount(long millisInFuture, long countDownInterval, BuySuccessDailogFragment fragment) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
            vipFragment = fragment;
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            vipFragment.dismiss();
            finish();
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示

        }
    }

}
