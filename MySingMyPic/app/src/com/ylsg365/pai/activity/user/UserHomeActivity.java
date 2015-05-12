package com.ylsg365.pai.activity.user;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.ylsg365.pai.activity.NewInfoAdapter;
import com.ylsg365.pai.activity.base.TabFragment;


import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.newsinfo.NewsInfoForwardFragment;
import com.ylsg365.pai.activity.view.CheckableImageView;
import com.ylsg365.pai.activity.view.SlidingTabLayout;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.model.User;
import com.ylsg365.pai.model.UserService;
import com.ylsg365.pai.util.BitmapUtils;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserHomeActivity extends BaseActivity implements View.OnClickListener{
    private int userId;

    //粉丝数
    private TextView userFansNumTextView;
    //被关注数
    private TextView userAttentionNumTextView;
    //账户创建时间
    private TextView userCTimeTextView;
    //用户地区
    private TextView userAreaTextView;
    //用户昵称
    private TextView userNickNameTextView;
    //用户头像
    private ImageView userHeadImageView;
    private TextView settingTextView;
    //举报
    private TextView reportTextView;
    //关注
    private TextView attentionTextView;
    private CheckableImageView attentionImageView;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        userId = getIntent().getIntExtra("userId", 0);

        setupToolbar();

        loadViews();

        getUserInfo();
    }

    @Override
    protected void loadViews() {
        userCTimeTextView = (TextView)findViewById(R.id.text_user_ctime);
        userAreaTextView = (TextView)findViewById(R.id.text_user_area);
        userNickNameTextView = (TextView)findViewById(R.id.text_user_nickName);
        userFansNumTextView = (TextView)findViewById(R.id.text_user_fansNum);
        userAttentionNumTextView = (TextView)findViewById(R.id.text_user_attentionNum);
        userHeadImageView = (ImageView)findViewById(R.id.img_user_avatar);
        settingTextView = (TextView)findViewById(R.id.text_right);
        reportTextView = (TextView)findViewById(R.id.text_report);
        attentionTextView = (TextView)findViewById(R.id.text_attention);
        attentionImageView = (CheckableImageView)findViewById(R.id.img_attention);

        settingTextView.setVisibility(View.GONE);
    }

    protected void initViews(User user) {
        setToolbarTitle(String.format("%s的空间", user.getNickName()));
        userNickNameTextView.setText(user.getNickName());
        userFansNumTextView.setText(String.format("粉丝（%d）", user.getFansNum()));
        userAttentionNumTextView.setText(String.format("关注（%d）", user.getAttentionNUm()));
        userAreaTextView.setText(user.getArea());

        if(user.isAttention()){
            attentionTextView.setText("取消关注");
            attentionImageView.setChecked(true);
        }else{
            attentionTextView.setText("关注");
            attentionImageView.setChecked(false);
        }

        if(user.getcTime() != null){
            userCTimeTextView.setText(user.getcTime().split(" ")[0]);
        }

        if(user.getSex() == 2){
            Drawable femaildrawable = getResources().getDrawable(R.drawable.icon_female);
            userNickNameTextView.setCompoundDrawables(femaildrawable, null, null, null);
        }

        LogUtil.logd("initViews", user.getHeadImg());

        if(!StringUtils.isEmpty(user.getHeadImg())){
            ImageLoader.getInstance().displayImage(user.getHeadImg(), userHeadImageView, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override

                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    Drawable bgDrawable = BitmapUtils.BoxBlurFilter(bitmap);

                    findViewById(R.id.layout_me_bg).setBackgroundDrawable(bgDrawable);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }
    }

    @Override
    protected void setupListeners() {
        findViewById(R.id.layout_private_message).setOnClickListener(this);
        findViewById(R.id.layout_quiet_attention).setOnClickListener(this);
        findViewById(R.id.layout_attention).setOnClickListener(this);
        findViewById(R.id.layout_original).setOnClickListener(this);
        findViewById(R.id.layout_works).setOnClickListener(this);
        findViewById(R.id.layout_newsInfo).setOnClickListener(this);
    }


    private void getUserInfo(){
        YinApi.getOtherInfo(userId, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getOtherInfo", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    user = UserService.getUser(response);
                    initViews(user);
                    setupListeners();
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
            case R.id.layout_original:
                toOriginalPage();
                break;
            case R.id.layout_works:
                toWorksPage();
                break;
            case R.id.layout_newsInfo:
                toNewsInfoPage();
                break;
            case R.id.layout_attention:
                attention(userId);
                break;
        }
    }

    private void toOriginalPage(){
        NavHelper.toOriginalBaseListPage(UserHomeActivity.this, NavHelper.REQUEST_OTHER_ORIGINAL, userId);
    }
    private void toWorksPage(){
        NavHelper.toOtherWorksPage(UserHomeActivity.this, userId);
    }
    private void toNewsInfoPage(){
        NavHelper.toOtherNewsInfoListPage(UserHomeActivity.this, userId);
    }

    private void attention(int userId){
        if(user != null){
            if(user.isAttention()){
                unAttentionToUser(userId);
            }else {
                attentionToUser(userId);
            }
        }
    }

    private void attentionToUser(int userId){
        YinApi.attentionToUser(userId, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("attentionToUser", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    UIHelper.showToast("关注成功");
                    user.setAttention(true);
                    attentionTextView.setText("取消关注");
                    attentionImageView.setChecked(true);
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

    private void unAttentionToUser(int userId){
        YinApi.unAttentionToUser(userId+"", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("unAttentionToUser", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    UIHelper.showToast("取消关注成功");
                    user.setAttention(false);
                    attentionTextView.setText("关注");
                    attentionImageView.setChecked(false);
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
}
