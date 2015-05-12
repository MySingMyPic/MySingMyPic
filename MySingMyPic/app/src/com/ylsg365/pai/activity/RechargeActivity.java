package com.ylsg365.pai.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.model.User;
import com.ylsg365.pai.model.UserService;
import com.ylsg365.pai.util.CommonAdapter;
import com.ylsg365.pai.util.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 充值界面
 */
public class RechargeActivity extends FragmentActivity implements RechargeFragment.OnFragmentInteractionListener {

    private RadioGroup radioGroup;
    private RadioButton RBCz;
    private RadioButton RBTx;
    public static RelativeLayout titleLayout;
    public static ViewPager mViewPager;
    private ArrayList<Fragment> fragmentList;
    private MyViewPagerAdapter adapter;
    private RechargeFragment tab1Fragment, tab2Fragment;
    private GridView mGridView;
    private List<Map<String, String>> datas;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private TextView leftTextView;
    private Button pay;
    private int select = -1;
    private View lastView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        RBCz = (RadioButton) findViewById(R.id.tab_1);
        RBTx = (RadioButton) findViewById(R.id.tab_2);
        pay = (Button) findViewById(R.id.pay);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(select == -1){
                    NavHelper.showToast(RechargeActivity.this,"请选择充值数额!");
                }else{
                    //跳到支付宝
                }
            }
        });


        setupToolbar();

        User user = UserService.getUser();

        ((TextView)findViewById(R.id.money_txt)).setText(user.getBalance() + "(1元兑换1音乐币)");

        mGridView = (GridView) findViewById(R.id.grid);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                select = position;
                if(lastView != null){
                    lastView.setBackgroundColor(Color.parseColor("#00000000"));
                }
                view.setBackgroundColor(Color.parseColor("#50000000"));
                lastView = view;
            }
        });

        datas = new ArrayList<Map<String, String>>();

            Map map = new HashMap<String, String>();
            map.put("num", 5+ "");
            map.put("money", 5 + "元");
            datas.add(map);
         map = new HashMap<String, String>();
        map.put("num", 10 + "");
        map.put("money", 10 + "元");
        datas.add(map);
        map = new HashMap<String, String>();
        map.put("num", 20 + "");
        map.put("money", 20 + "元");
        datas.add(map);
         map = new HashMap<String, String>();
        map.put("num", 50 + "");
        map.put("money", 50 + "元(返50元)");
        datas.add(map);
         map = new HashMap<String, String>();
        map.put("num", 80 + "");
        map.put("money", 80 + "元(返80元)");
        datas.add(map);
         map = new HashMap<String, String>();
        map.put("num", 150 + "");
        map.put("money", 150 + "元(返150元)");
        datas.add(map);
         map = new HashMap<String, String>();
        map.put("num",300 + "");
        map.put("money",300 + "元(返300元)");
        datas.add(map);
         map = new HashMap<String, String>();
        map.put("num", 500 + "");
        map.put("money", 500 + "元(返500元)");
        datas.add(map);
         map = new HashMap<String, String>();
        map.put("num", 1000 + "");
        map.put("money", 1000 + "元(返1000元)");
        datas.add(map);

        mGridView.setAdapter(new CommonAdapter(RechargeActivity.this, datas, R.layout.recharge_grid_item) {

            @Override
            public void convert(ViewHolder holder, Object item) {
                Map<String, String> mapItem = (Map<String, String>) item;
                holder.setText(R.id.num, mapItem.get("num") + "音乐币");
                holder.setText(R.id.money, mapItem.get("money") + "");
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        fragmentList = new ArrayList<Fragment>();
        tab1Fragment = RechargeFragment.newInstance(2);
        fragmentList.add(tab1Fragment);
        tab2Fragment = RechargeFragment.newInstance(1);
        fragmentList.add(tab2Fragment);

        adapter = new MyViewPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(1);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                SetChecked(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        radioGroup = (RadioGroup) this.findViewById(R.id.roadbook_tab_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.tab_1:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.tab_2:
                        mViewPager.setCurrentItem(1);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void SetChecked(int position) {

        if (position == 0) {
            RBCz.performClick();
        } else {
            RBTx.performClick();
        }

    }


    @Override
    public void onFragmentInteraction(String id) {

    }

    public class MyViewPagerAdapter extends FragmentPagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

    }
    public void setupToolbar() {
        View v = findViewById(R.id.toolbar);
        if (v != null) {
            toolbar = (Toolbar) v;
            toolbarTitle = (TextView) v.findViewById(R.id.toolbar_title);

            leftTextView = (TextView) v.findViewById(R.id.text_toolbar_left);
            toolbarTitle.setText("充值");

            leftTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavHelper.finish(RechargeActivity.this);
                }
            });
        }
    }

}
