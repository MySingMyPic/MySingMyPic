package com.ylsg365.pai.activity.video;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ylsg365.pai.R;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.customview.DialogView;
import com.ylsg365.pai.listener.DialogListViewListener;
import com.ylsg365.pai.listener.DialogMessageListener;
import com.ylsg365.pai.listener.DialogShareListener;

import java.util.ArrayList;
import java.util.List;

public class VideoLongActivity extends ActionBarActivity {

    private LinearLayout finishPlay;
    private LinearLayout inputMusic;

    public int way;//0 3分钟，1 5分钟
    //对话框
    DialogView dialog=new DialogView();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_long);

        way=getIntent().getIntExtra("choice",0);

        finishPlay=(LinearLayout)findViewById(R.id.layout_video_bottom_bar);
        inputMusic=(LinearLayout)findViewById(R.id.input_music);
        finishPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list=new ArrayList<String>();
                list.add("上传");
                list.add("取消");

                dialog.createListViewDialog(VideoLongActivity.this,list,new DialogListViewListener() {
                    @Override
                    public void select(int pos) {
                        switch(pos){
                            case 0:
                                createMessageDialog();
                                break;
                            case 1:
                                dialog.dismissDialog();
                                break;
                        }
                    }
                });
            }
        });
        inputMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHelper.toVideoAddMusicSelectActivity(VideoLongActivity.this,1);
            }
        });
    }

    public void createMessageDialog()
    {
        dialog.createMessageDialog(this,new DialogMessageListener() {
            @Override
            public void getMessage(String message) {
                createShareDialog();
            }
        });
    }

    public void createShareDialog()
    {
        dialog.createShareDialog(this,new DialogShareListener() {
            //0是微信，1是微博
            @Override
            public void select(int choice) {
                switch(choice)
                {
                    case 0:
                        Toast.makeText(VideoLongActivity.this, "微信", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(VideoLongActivity.this,"微博",Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });
    }

}
