package com.ylsg365.pai.activity.room;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.event.UserAttentionEvent;
import com.ylsg365.pai.model.UserService;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.ShareUtil;

import de.greenrobot.event.EventBus;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.util.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class RoomMainActivity extends BaseActivity implements
        IWeiboHandler.Response, OnClickListener {
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ArrayList<TabFragment> fragments;
    private ViewPager_Adapter viewPager_Adapter;
    private LinearLayout shard_btn;
    private LinearLayout gift;
//    private IWeiboShareAPI mWeiboShareAPI;
    private Bitmap bmp;

    private ImageView mHouseImgIV, mUserImgIV;
    private TextView mUserIdTV;
    private Button mAttentionBT;

    /**
     * 聊天
     */
    private FrameLayout mChatFLayout;
    private EditText mChatET;
    private Button mChatBT;

    private LinearLayout mChatLL, mPaiLL;

    private String mHouseId;
    private int mUserId;

    private RoomChatFragment mChatFragment;
    private UserAttentionFragment mViewerFragment;
    private MicQueueFragment mMicFragment;

    private TimerTask mTask = new TimerTask() {
        @Override
        public void run() {
            mChatFragment.refresh();
            mViewerFragment.refresh();
            mMicFragment.refresh();
        }
    };
    private Timer mTimer = new Timer(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_main);

        EventBus.getDefault().register(this);

        if (savedInstanceState != null) {
            ShareUtil.mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }

        mHouseImgIV = (ImageView) findViewById(R.id.iv_house_img);
        mUserImgIV = (ImageView) findViewById(R.id.img_user_avatar);
        mUserIdTV = (TextView) findViewById(R.id.tv_user_id);
        mAttentionBT = (Button) findViewById(R.id.bt_attention);

        mChatFLayout = (FrameLayout) findViewById(R.id.fl_chat);
        mChatFLayout.setOnClickListener(this);
        mChatET = (EditText) findViewById(R.id.et_content);
        mChatBT = (Button) findViewById(R.id.bt_commit);
        mChatBT.setOnClickListener(this);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.demo_tab);
        viewPager = (ViewPager) findViewById(R.id.pager_fresh);

        shard_btn = (LinearLayout) findViewById(R.id.shard_btn);
        gift = (LinearLayout) findViewById(R.id.gift);
        gift.setOnClickListener(this);
        mChatLL = (LinearLayout) findViewById(R.id.ll_chat_);
        mChatLL.setOnClickListener(this);
        mPaiLL = (LinearLayout) findViewById(R.id.ll_pai);
        mPaiLL.setOnClickListener(this);
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
                                .sendReq(RoomMainActivity.this, 1, "测试分享", bmp);
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

        mHouseId = getIntent().getExtras().getString("nid");
        // 设置ViewPager
        fragments = new ArrayList<TabFragment>();
        mChatFragment = new RoomChatFragment(mHouseId);
        fragments.add(mChatFragment);
        mViewerFragment = new UserAttentionFragment(mHouseId);
        fragments.add(mViewerFragment);
        mMicFragment = new MicQueueFragment(mHouseId);
        fragments.add(mMicFragment);
        viewPager_Adapter = new ViewPager_Adapter(getSupportFragmentManager(),
                fragments);
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setAdapter(viewPager_Adapter);

        // 设置SlidingTab
        slidingTabLayout.setViewPager(viewPager);

        setupToolbar();

        getHouseDetail();

        mTimer.schedule(mTask, 15000, 15000);
    }

    private void getHouseDetail() {
        /**
         * 获取房屋详情
         */
        YinApi.getHouseDetail(mHouseId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logd("getHouseDetail", response);
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                } catch (JSONException e) {
                }
                if (json != null && JsonUtil.getBoolean(json, "status")) {
                    ImageLoader.getInstance().displayImage(
                            Constants.WEB_IMG_DOMIN
                                    + JsonUtil.getString(json, "imgUrl"),
                            mHouseImgIV);
                    ImageLoader.getInstance().displayImage(
                            Constants.WEB_IMG_DOMIN
                                    + JsonUtil.getString(json, "headImg"),
                            mUserImgIV);
                    setTitle(JsonUtil.getString(json, "nname"));
                    mUserIdTV.setText(JsonUtil.getString(json, "nickName"));
                    mUserId = JsonUtil.getInt(json, "userId");
                    if (mUserId == UserService.getUser().getUserId()) {
                        mAttentionBT.setVisibility(View.INVISIBLE);
                        return;
                    } else {
                        mAttentionBT.setVisibility(View.VISIBLE);
                    }
                    final boolean attention = JsonUtil.getBoolean(json,
                            "attention");
                    if (attention) {
                        mAttentionBT.setPressed(true);
                    } else {
                        mAttentionBT.setPressed(false);
                    }
                    mAttentionBT.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventBus.getDefault().post(
                                    new UserAttentionEvent(mUserId, attention,
                                            mAttentionBT));
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
        LogUtil.logd("newIntent", "newIntent");
        super.onNewIntent(intent);
        ShareUtil.mWeiboShareAPI.handleWeiboResponse(intent, this); // 当前应用唤起微博分享后，返回当前应用
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
        mTimer.cancel();
        exitHouse(mHouseId);
        EventBus.getDefault().unregister(this);
    }

    private void exitHouse(final String houseId) {
        YinApi.inoutHouse(houseId, 0, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logd("inoutHouse", response.toString());
                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                } catch (JSONException e) {
                }
                if (obj == null || !JsonUtil.getBoolean(obj, "status")) {
                    LogUtil.logd("inoutHouse", "退出包房失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.logd("inoutHouse", "退出包房失败");
            }
        });
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

    private void showChatLayout(boolean flag) {
        if (flag) {
            mChatFLayout.setVisibility(View.VISIBLE);
            mChatET.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mChatET, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            mChatFLayout.setVisibility(View.GONE);
        }
    }

    private void commitChat() {
        String content = mChatET.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(getBaseContext(), "请输入内容", Toast.LENGTH_SHORT)
                    .show();
            mChatET.requestFocus();
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mChatET.getWindowToken(), 0);
        showChatLayout(false);

        YinApi.sendHouseChat(mHouseId, content,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtil.logd("sendHouseChat", response.toString());
                        if (response != null
                                && JsonUtil.getBoolean(response, "status")) {
                            Toast.makeText(getBaseContext(), "发送成功",
                                    Toast.LENGTH_SHORT).show();
                            mChatET.setText("");
                            mChatFragment.refresh();
                        } else {
                            Toast.makeText(getBaseContext(), "发送失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(), "发送失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.bt_commit:
            // 发送聊天
            commitChat();
            break;
        case R.id.ll_chat_:
            // 打开聊天页面
            showChatLayout(true);
            break;
        case R.id.gift:
            // 送礼物
            NavHelper.toGiftListActivity(RoomMainActivity.this, mHouseId,
                    mUserId + "");
            break;
        case R.id.ll_pai:
            // 点歌排麦
            break;
        case R.id.fl_chat:
            // 关闭聊天页面
            showChatLayout(false);
            break;
        }
    }

}
