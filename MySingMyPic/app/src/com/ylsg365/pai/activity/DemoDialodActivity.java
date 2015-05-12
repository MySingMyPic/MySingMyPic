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
import com.ylsg365.pai.activity.dialog.BuySuccessDailogFragment;
import com.ylsg365.pai.activity.dialog.MVUploadFragment;
import com.ylsg365.pai.activity.dialog.NoticeConfirmFragment;
import com.ylsg365.pai.activity.dialog.NoticeDialodFragment;
import com.ylsg365.pai.activity.dialog.NoticePriceUploadFragment;
import com.ylsg365.pai.activity.dialog.NoticeTitleDailogFragment;
import com.ylsg365.pai.activity.dialog.NoticeUploadFragment;
import com.ylsg365.pai.activity.dialog.RecordUploadFragment;
import com.ylsg365.pai.activity.dialog.VideoTimeSelectFragment;
import com.ylsg365.pai.activity.dialog.VideoUploadFragment;
import com.ylsg365.pai.activity.dialog.VipOpenPromptFragment;

import java.util.ArrayList;

public class DemoDialodActivity extends BaseActivity implements AdapterView.OnItemClickListener{
    private ListView demoListView;
    private ArrayList<String> demoNameList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        demoListView = (ListView)findViewById(R.id.list_demo);

        demoNameList = new ArrayList<String>();

        initList();

        setupToolbar();

        setTitle("Demo");
    }

    private void initList(){
        demoNameList.add("3-5-4-1 是否分享");
        demoNameList.add("5-1-4-1 是否分享");
        demoNameList.add("5-1-6   录制完成，是否上传");
        demoNameList.add("5-1-3   录制完成，是否上传");
        demoNameList.add("3-5-3   保存录音，定价后上传");
        demoNameList.add("3-5-4   输入一段话，直接上传");
        demoNameList.add("5-1-4   输入一段话，直接上传");
        demoNameList.add("5-1-6-3 输入一段话，直接上传");
        demoNameList.add("3-5-5   输入一段话，定价上传");
        demoNameList.add("3-8-4   填写比赛信息");
        demoNameList.add("2-1-8   发送成功");
        demoNameList.add("5-1-5   插入音频合成中");
        demoNameList.add("2-7-2   购买会员确认");
        demoNameList.add("7-3-3   开通会员提示");
        demoNameList.add("5-1-1   拍视频时间长度选择");
        demoNameList.add("2-7-3   开通会员成功");
        demoNameList.add("3-7-2   购买成功");

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
        switch (position){
//            case 0:
//                ShareDialodFragment.newInstance(R.string.dialog_share_notice_sing).show(getSupportFragmentManager(), "ShareDialodFragment");
//                break;
//            case 1:
//                ShareDialodFragment.newInstance(R.string.dialog_share_notice_shoot).show(getSupportFragmentManager(), "ShareDialodFragment");
//                break;
            case 2:
                MVUploadFragment.newInstance().show(getSupportFragmentManager(), "MVUploadFragment");
                break;
            case 3:
                VideoUploadFragment.newInstance().show(getSupportFragmentManager(), "VideoUploadFragment");
                break;
            case 4:
                RecordUploadFragment.newInstance().show(getSupportFragmentManager(), "RecordUploadFragment");
                break;
            case 5:
                NoticeUploadFragment.newInstance().show(getSupportFragmentManager(), "NoticeUploadFragment");
                break;
            case 6:
                NoticeUploadFragment.newInstance().show(getSupportFragmentManager(), "NoticeUploadFragment");
                break;
            case 7:
                NoticeUploadFragment.newInstance().show(getSupportFragmentManager(), "NoticeUploadFragment");
                break;
            case 8:
                NoticePriceUploadFragment.newInstance().show(getSupportFragmentManager(), "NoticePriceUploadFragment");
                break;
            case 9:
//                GameInfoInputFragment.newInstance().show(getSupportFragmentManager(), "GameInfoInputFragment");
                break;
            case 10:
                NoticeDialodFragment.newInstance(R.string.dialog_notice_send_success).show(getSupportFragmentManager(), "NoticeDialodFragment");
                break;
            case 11:
                NoticeDialodFragment.newInstance(R.string.dialog_notice_working).show(getSupportFragmentManager(), "NoticeDialodFragment");
                break;
            case 12:
                NoticeConfirmFragment.newInstance(String.format(getString(R.string.dialog_confirm_notice), 1500)).show(getSupportFragmentManager(), "NoticeConfirmFragment");
                break;
            case 13:
                VipOpenPromptFragment.newInstance("").show(getSupportFragmentManager(), "VipOpenPromptFragment");
                break;
            case 14:
                VideoTimeSelectFragment.newInstance().show(getSupportFragmentManager(), "VideoTimeSelectFragment");
                break;
            case 15:
                NoticeTitleDailogFragment.newInstance().show(getSupportFragmentManager(), "NoticeTitleDailogFragment");
                break;
            case 16:
                BuySuccessDailogFragment.newInstance("").show(getSupportFragmentManager(), "BuySuccessDailogFragment");
                break;

        }

    }
}
