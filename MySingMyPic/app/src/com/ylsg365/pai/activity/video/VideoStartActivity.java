package com.ylsg365.pai.activity.video;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.ylsg365.pai.R;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.customview.DialogView;
import com.ylsg365.pai.listener.DialogListViewListener;

import java.util.ArrayList;
import java.util.List;

public class VideoStartActivity extends ActionBarActivity {

    private LinearLayout startPlay;
    DialogView dialog=new DialogView();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_start);

        startPlay=(LinearLayout)findViewById(R.id.layout_video_bottom_bar);
        startPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list=new ArrayList<String>();
                list.add("30秒");
                list.add("3分钟");
                list.add("5分钟");
                list.add("取消");
                dialog.createListViewWithTitleDialog(VideoStartActivity.this,"请选择录制视频长度",list,new DialogListViewListener() {
                    @Override
                    public void select(int pos) {
                        switch (pos)
                        {
                            case 0:
                                NavHelper.toVideoActivity(VideoStartActivity.this,0);
                                break;
                            case 1:
                                NavHelper.toVideoActivity(VideoStartActivity.this,1);
                                break;
                            case 2:
                                NavHelper.toVideoActivity(VideoStartActivity.this,2);
                                break;
                            case 3:
                                dialog.dismissDialog();
                                break;

                        }
                    }
                });
            }
        });
    }

}
