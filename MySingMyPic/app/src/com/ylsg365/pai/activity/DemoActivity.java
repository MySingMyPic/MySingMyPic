package com.ylsg365.pai.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.login.AccountSettingActivity;
import com.ylsg365.pai.activity.login.LoginActivity;
import com.ylsg365.pai.activity.login.PasswordForgetActivity;
import com.ylsg365.pai.activity.login.PasswordModifyActivity;
import com.ylsg365.pai.activity.login.RegisterActivity;
import com.ylsg365.pai.activity.main.GiftListActivity;
import com.ylsg365.pai.activity.main.HomeActivity;
import com.ylsg365.pai.activity.main.MainActivity;
import com.ylsg365.pai.activity.main.OriginalBaseActivity;
import com.ylsg365.pai.activity.message.CommentActivity;
import com.ylsg365.pai.activity.message.CommentSendActivity;
import com.ylsg365.pai.activity.message.SystemMessageActivity;
import com.ylsg365.pai.activity.newsinfo.NewsInfoDetalActivity;
import com.ylsg365.pai.activity.room.FriendInviteActivity;
import com.ylsg365.pai.activity.room.GameInfoActivity;
import com.ylsg365.pai.activity.room.GiftInfoActivity;
import com.ylsg365.pai.activity.room.RoomCreateActivity;
import com.ylsg365.pai.activity.room.RoomInfoActivity;
import com.ylsg365.pai.activity.room.SingerPriorityActivity;
import com.ylsg365.pai.activity.setting.SettingActivity;
import com.ylsg365.pai.activity.singsong.SingerActivity;
import com.ylsg365.pai.activity.singsong.SongActivity;
import com.ylsg365.pai.activity.singsong.SongCategoryActivity;
import com.ylsg365.pai.activity.user.AccountFindSuccessActivity;
import com.ylsg365.pai.activity.user.MoneyManagerActivity;
import com.ylsg365.pai.activity.user.UserChatActivity;
import com.ylsg365.pai.activity.user.UserHomeActivity;
import com.ylsg365.pai.activity.user.UserInfoActivity;
import com.ylsg365.pai.activity.user.WithdrawActivity;
import com.ylsg365.pai.activity.video.CappellaRecordActivity;
import com.ylsg365.pai.activity.video.VideoActivity;

import java.util.ArrayList;

public class DemoActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView demoListView;
    private ArrayList<String> demoNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        demoListView = (ListView) findViewById(R.id.list_demo);

        demoNameList = new ArrayList<String>();

        initList();

        setupToolbar();

        setTitle("Demo");
    }

    private void initList() {
        demoNameList.add("用户登录");
        demoNameList.add("创建房间");
        demoNameList.add("流行歌曲");
        demoNameList.add("分类点歌");
        demoNameList.add("首页");
        demoNameList.add("新鲜事");
        demoNameList.add("其他人的空间");
        demoNameList.add("主页");
        demoNameList.add("修改密码");
        demoNameList.add("用户注册");
        demoNameList.add("找回密码");
        demoNameList.add("设置账号");
        demoNameList.add("软件设置");
        demoNameList.add("个人信息");
        demoNameList.add("找回账号成功设置密码");
        demoNameList.add("弹出对话框列表");
        demoNameList.add("房间信息");
        demoNameList.add("优先歌者");
        demoNameList.add("邀请好友");
        demoNameList.add("比赛信息");
        demoNameList.add("已参加比赛");
        demoNameList.add("所有礼物");
        demoNameList.add("礼物详情");
        demoNameList.add("ffmpeg测试");
        demoNameList.add("系统消息");
        demoNameList.add("我的评论");
        demoNameList.add("歌星点歌");
        demoNameList.add("视频录制");
        demoNameList.add("清唱录制");
        demoNameList.add("原创基地");
        demoNameList.add("音乐币管理");
        demoNameList.add("购买版权");
        demoNameList.add("发评论");
        demoNameList.add("取现");
        demoNameList.add("用户聊天");
        ArrayAdapter<String> demoAdapter = new ArrayAdapter(this, R.layout.item_demo, demoNameList);

        demoListView.setAdapter(demoAdapter);
        demoListView.setOnItemClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        switch (position) {
            case 0:
                intent.setClass(DemoActivity.this, LoginActivity.class);
                break;
            case 1:
                intent.setClass(DemoActivity.this, RoomCreateActivity.class);
                break;
            case 2:
                intent.setClass(DemoActivity.this, SongActivity.class);
                break;
            case 3:
                intent.setClass(DemoActivity.this, SongCategoryActivity.class);
                break;
            case 4:
                intent.setClass(DemoActivity.this, HomeActivity.class);
                break;
            case 5:
                intent.setClass(DemoActivity.this, NewsInfoDetalActivity.class);
                break;
            case 6:
                intent.setClass(DemoActivity.this, UserHomeActivity.class);
                break;
            case 7:
                intent.setClass(DemoActivity.this, MainActivity.class);
                break;
            case 8:
                intent.setClass(DemoActivity.this, PasswordModifyActivity.class);
                break;
            case 9:
                intent.setClass(DemoActivity.this, RegisterActivity.class);
                break;
            case 10:
                intent.setClass(DemoActivity.this, PasswordForgetActivity.class);
                break;
            case 11:
                intent.setClass(DemoActivity.this, AccountSettingActivity.class);
                break;
            case 12:
                intent.setClass(DemoActivity.this, SettingActivity.class);
                break;
            case 13:
                intent.setClass(DemoActivity.this, UserInfoActivity.class);
                break;
            case 14:
                intent.setClass(DemoActivity.this, AccountFindSuccessActivity.class);
                break;
            case 15:
                intent.setClass(DemoActivity.this, DemoDialodActivity.class);
                break;
            case 16:
                intent.setClass(DemoActivity.this, RoomInfoActivity.class);
                break;
            case 17:
                intent.setClass(DemoActivity.this, SingerPriorityActivity.class);
                break;
            case 18:
                intent.setClass(DemoActivity.this, FriendInviteActivity.class);
                break;
            case 19:
                intent.setClass(DemoActivity.this, GameInfoActivity.class);
                break;
//            case 20:
//                intent.setClass(DemoActivity.this, GameJoinedActivity.class);
//                break;
            case 21:
                intent.setClass(DemoActivity.this, GiftListActivity.class);
                break;
            case 22:
                intent.setClass(DemoActivity.this, GiftInfoActivity.class);
                break;
            case 23:
                intent.setClass(DemoActivity.this, FFmpeg4AndroidActivity.class);
                break;
            case 24:
                intent.setClass(DemoActivity.this, SystemMessageActivity.class);
                break;
            case 25:
                intent.setClass(DemoActivity.this, CommentActivity.class);
                break;
            case 26:
                intent.setClass(DemoActivity.this, SingerActivity.class);
                break;
            case 27:
                intent.setClass(DemoActivity.this, VideoActivity.class);
                break;
            case 28:
                intent.setClass(DemoActivity.this, CappellaRecordActivity.class);
                break;
            case 29:
                intent.setClass(DemoActivity.this, OriginalBaseActivity.class);
                break;
            case 30:
                intent.setClass(DemoActivity.this, MoneyManagerActivity.class);
                break;
//            case 31:
//                intent.setClass(DemoActivity.this, CopyrightBuyActivity.class);
//                break;
            case 32:
                intent.setClass(DemoActivity.this, CommentSendActivity.class);
                break;
            case 33:
                intent.setClass(DemoActivity.this, WithdrawActivity.class);
                break;
            case 34:
                intent.setClass(DemoActivity.this, UserChatActivity.class);
                break;

        }

        startActivity(intent);
    }
}
