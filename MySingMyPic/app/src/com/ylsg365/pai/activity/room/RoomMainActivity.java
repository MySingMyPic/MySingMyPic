package com.ylsg365.pai.activity.room;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.constant.WBConstants;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.base.TabFragment;
import com.ylsg365.pai.activity.dialog.ShareDialog;
import com.ylsg365.pai.activity.view.SlidingTabLayout;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.event.UserAttentionEvent;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.ShareUtil;

import de.greenrobot.event.EventBus;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class RoomMainActivity extends BaseActivity implements
        IWeiboHandler.Response {
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ArrayList<TabFragment> fragments;
    private ViewPager_Adapter viewPager_Adapter;
    private LinearLayout shard_btn;
    private LinearLayout gift;
    private IWeiboShareAPI mWeiboShareAPI;
    private Bitmap bmp;
    
    private ImageView mHouseImgIV, mUserImgIV;
    private TextView mUserIdTV;
    private Button mAttentionBT;

    private String nid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_main);

        EventBus.getDefault().register(this);

        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
        
        mHouseImgIV = (ImageView)findViewById(R.id.iv_house_img);
        mUserImgIV = (ImageView)findViewById(R.id.img_user_avatar);
        mUserIdTV = (TextView)findViewById(R.id.tv_user_id);
        mAttentionBT = (Button)findViewById(R.id.bt_attention);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.demo_tab);
        viewPager = (ViewPager) findViewById(R.id.pager_fresh);

        shard_btn = (LinearLayout) findViewById(R.id.shard_btn);
        gift = (LinearLayout) findViewById(R.id.gift);
        bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher);
        shard_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShareDialog dialog = new ShareDialog(RoomMainActivity.this);
                dialog.setOnShareClickListener(new ShareDialog.OnShareClickListener() {
                    @Override
                    public void onWechatClick() {
                        ShareUtil
                                .sendReq(RoomMainActivity.this, 0, "测试分享", bmp);
                    }

                    @Override
                    public void onSinaClick() {
                        ShareUtil.responseMessage(RoomMainActivity.this,
                                "测试新浪微博分享,", bmp, "title", "www.baidu.com",
                                true, false, false, false, false, false);

                    }

                    @Override
                    public void onYinClick() {

                    }
                });
            }
        });
        gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // NavHelper.toGiftListActivity(RoomMainActivity.this,"");
            }
        });

        nid = getIntent().getExtras().getString("nid");
        // 设置ViewPager
        fragments = new ArrayList<TabFragment>();
        fragments.add(new RoomChatFragment(nid));
        fragments.add(new UserAttentionFragment(nid));
        fragments.add(new MicQueueFragment(nid));
        viewPager_Adapter = new ViewPager_Adapter(getSupportFragmentManager(),
                fragments);
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setAdapter(viewPager_Adapter);

        // 设置SlidingTab
        slidingTabLayout.setViewPager(viewPager);

        setupToolbar();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        getHouseDetail();
    }

    private void getHouseDetail() {
        /**
         * 获取房屋详情
         */
        YinApi.getHouseDetail(nid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.loge("getHouseDetail", response);
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                }
                if(json != null && JsonUtil.getBoolean(json, "status")) {
                    ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN +JsonUtil.getString(json, "imgUrl"), mHouseImgIV);
                    ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN +JsonUtil.getString(json, "headImg"), mUserImgIV);
                    setTitle(JsonUtil.getString(json, "nname"));
                    mUserIdTV.setText(JsonUtil.getString(json, "nickName"));
                    final int userId = JsonUtil.getInt(json, "userId");
                    final boolean attention = JsonUtil.getBoolean(json, "attention");
                    if(attention) {
                        mAttentionBT.setPressed(true);
                    } else {
                        mAttentionBT.setPressed(false);
                    }
                    mAttentionBT.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventBus.getDefault().post(new UserAttentionEvent(userId, attention, mAttentionBT));
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    public static class Fragment_Tab_1 extends TabFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_sing_shoot, container,
                    false);
        }

        public String getTitle() {
            return "公聊";
        }
    }

    public static class Fragment_Tab_2 extends TabFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_me, container, false);
        }

        public String getTitle() {
            return "观众(39)";
        }
    }

    public static class Fragment_Tab_3 extends TabFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_me, container, false);
        }

        public String getTitle() {
            return "拍麦(22)";
        }
    }

    public class ViewPager_Adapter extends FragmentPagerAdapter {

        private ArrayList<TabFragment> fragments;

        public ViewPager_Adapter(FragmentManager fm,
                ArrayList<TabFragment> fragments) {
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

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWeiboShareAPI.handleWeiboResponse(intent, this); // 当前应用唤起微博分享后，返回当前应用
    }

    @Override
    public void onResponse(BaseResponse baseResp) {// 接收微客户端博请求的数据。
        switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
            break;
        case WBConstants.ErrorCode.ERR_CANCEL:
            break;
        case WBConstants.ErrorCode.ERR_FAIL:
            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(UserAttentionEvent event) {
        if (event.isAttentioned) {
            // 取消关注
            attentionUser(event);
        } else {
            // 关注
            unattentionUser(event);
        }
    }

    public void attentionUser(final UserAttentionEvent event) {
        YinApi.attentionToUser(event.userId,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.logd("attentionToUser", response.toString());
                        if (JsonUtil.getBoolean(response, "status")) {
                            event.btn.setPressed(false);
                        } else {
                            Toast.makeText(RoomMainActivity.this, "操作失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RoomMainActivity.this, "操作失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void unattentionUser(final UserAttentionEvent event) {
        YinApi.unAttentionToUser(event.userId + "",
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.logd("attentionToUser", response.toString());
                        if (JsonUtil.getBoolean(response, "status")) {
                            event.btn.setPressed(true);
                        } else {
                            Toast.makeText(RoomMainActivity.this, "操作失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RoomMainActivity.this, "操作失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
