package com.ylsg365.pai.activity.newsinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.Listener.OnItemClickListener;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.base.TabFragment;
import com.ylsg365.pai.activity.view.SlidingTabLayout;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.face.FaceUtil;
import com.ylsg365.pai.imagedisplay.ImageDisplayActivity;
import com.ylsg365.pai.util.DateUtil;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsInfoDetalActivity extends BaseActivity implements OnItemClickListener, View.OnClickListener {
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ArrayList<TabFragment> fragments;
    private ViewPager_Adapter viewPager_Adapter;
    private JSONObject newsInfoObj;
    private TextView nickNameTextView;
    private TextView cTimeTextView;
    public ImageView userHeadImageview;
    public TextView infoContentTextView;
    public TextView attentionTextView;
    boolean isAttention=false;

    private int forwardCount;
    private int commentCount;
    private int likeCount;

    private SuperRecyclerView infoImageRecyclerView;
    private NewsInfoImgAdapter newsInfoImgAdapter;
    private int newsInfoId;
    private int userId;
    //底部按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fresh_news);

        newsInfoObj = JsonUtil.getJSONObject(getIntent().getStringExtra("newsInfo"));
        newsInfoId = JsonUtil.getInt(newsInfoObj, "nid");
        getNewInfo(newsInfoId);

    }

    public void getNewInfo(final int newsInfoId)
    {
        YinApi.getNewInfo(newsInfoId,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                newsInfoObj=response;
                userId = JsonUtil.getInt(newsInfoObj, "userId");


                forwardCount = JsonUtil.getInt(newsInfoObj, "forwardCount");
                commentCount = JsonUtil.getInt(newsInfoObj, "commentCount");
                likeCount = JsonUtil.getInt(newsInfoObj, "giftCount");
                isAttention=JsonUtil.getBoolean(newsInfoObj,"attention");
                slidingTabLayout = (SlidingTabLayout) findViewById(R.id.demo_tab);
                viewPager = (ViewPager) findViewById(R.id.pager_fresh);


                // 设置ViewPager
                fragments = new ArrayList<TabFragment>();
                fragments.add(NewsInfoForwardFragment.newInstance(newsInfoId, forwardCount));
                fragments.add(InfoCommentFragment.newInstance(newsInfoId, commentCount));
                fragments.add(InfoLikeFragment.newInstance(newsInfoId, likeCount));
                viewPager_Adapter = new ViewPager_Adapter(getSupportFragmentManager(),
                        fragments);
                viewPager.setOffscreenPageLimit(fragments.size());
                viewPager.setAdapter(viewPager_Adapter);

                // 设置SlidingTab
                slidingTabLayout.setViewPager(viewPager);


                //新鲜事图片
                infoImageRecyclerView = (SuperRecyclerView) findViewById(R.id.recycler_imgs);
//        infoImageRecyclerView.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(NewsInfoDetalActivity.this);
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                infoImageRecyclerView.setLayoutManager(layoutManager);

                setupToolbar();
                setTitle("新鲜事");

                init();

                getNewInfoImg(newsInfoId);
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    public void getNewInfoImg(int newsInfoId) {
        YinApi.getNewInfoImg(newsInfoId, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getNewInfoForwards", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "data");
                    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }
                    newsInfoImgAdapter = new NewsInfoImgAdapter(R.layout.item_info_img, infoList);
                    newsInfoImgAdapter.setOnItemClickListener(NewsInfoDetalActivity.this);
                    infoImageRecyclerView.setAdapter(newsInfoImgAdapter);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    protected void loadViews() {
        nickNameTextView = (TextView) findViewById(R.id.text_nickName);
        cTimeTextView = (TextView) findViewById(R.id.text_cTime);
        infoContentTextView = (TextView) findViewById(R.id.text_content);
        attentionTextView = (TextView) findViewById(R.id.text_attention);
        userHeadImageview = (ImageView) findViewById(R.id.img_headImg);
    }

    @Override
    protected void initViews() {
        nickNameTextView.setText(JsonUtil.getString(newsInfoObj, "nickName"));
        cTimeTextView.setText(DateUtil.getFriendlyDate(JsonUtil.getString(newsInfoObj, "cTime")));
        infoContentTextView.setText(FaceUtil.setText(this,JsonUtil.getString(newsInfoObj, "ntext")));
        if(isAttention)
            attentionTextView.setText("取消关注");
        else attentionTextView.setText("关注");
        ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + JsonUtil.getString(newsInfoObj, "headImg"), userHeadImageview);
    }

    @Override
    protected void setupListeners() {
        findViewById(R.id.layout_like).setOnClickListener(this);
        findViewById(R.id.layout_comment).setOnClickListener(this);
        findViewById(R.id.layout_forward).setOnClickListener(this);
        attentionTextView.setOnClickListener(this);
    }

    @Override
    public void onItemClick(View view, int postion) {
         JSONObject item=(JSONObject) newsInfoImgAdapter.getItem(postion);
         String path=JsonUtil.getString(item, "imgUrl");
         Intent it=new Intent(this, ImageDisplayActivity.class);
         it.putExtra(ImageDisplayActivity.PATH,path);
        startActivity(it);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_like:
                doNewsInfoLike();
                break;
            case R.id.layout_comment:
                toCommentSendPage();
                break;
            case R.id.layout_forward:
                toForwardSendPage();
                break;
            case R.id.text_attention:
                if(isAttention)
                {
                    unAttentionToUser();
                }
                else
                    attentionToUser();
        }
    }

    private void attentionToUser(){
        YinApi.attentionToUser(userId, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("attentionToUser", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    UIHelper.showToast("关注成功");

                    isAttention=true;
                    attentionTextView.setText("取消关注");
                }else {
                    UIHelper.showToast("操作失败");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UIHelper.showToast("操作失败");
            }
        });
    }

    private void unAttentionToUser(){
        YinApi.unAttentionToUser(userId+"", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("unAttentionToUser", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    UIHelper.showToast("取消关注成功");

                    isAttention=false;
                    attentionTextView.setText("关注");
                } else {
                    UIHelper.showToast("操作失败");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UIHelper.showToast("操作失败");
            }
        });
    }



    private void doNewsInfoLike(){

        NavHelper.toGiftListActivityForNewsInfo(NewsInfoDetalActivity.this,newsInfoId+"",1); //送礼物
    }

    private void toCommentSendPage(){
        NavHelper.toCommentSendPage(NewsInfoDetalActivity.this, newsInfoObj.toString());
    }
    private void toForwardSendPage(){
        NavHelper.toForwardSendPage(NewsInfoDetalActivity.this, newsInfoObj.toString());
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
            if (position == 0) {
                return String.format("%s%d", getItem(position).getTitle(), forwardCount);
            }
            if (position == 1) {
                return String.format("%s%d", getItem(position).getTitle(), commentCount);
            }
            if (position == 2) {
                return String.format("%s%d", getItem(position).getTitle(), likeCount);
            }
            return getItem(position).getTitle();
        }
    }
}
