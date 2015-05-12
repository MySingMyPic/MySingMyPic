package com.ylsg365.pai.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ylsg365.pai.R;


/**
 * 选择分享项目dialog
 * @author lanzhihong
 */
public class ShareDialog extends Dialog implements OnClickListener {

    private Context activity;
    TextView exit;
    ImageView myfrends;
    ImageView sina;
    ImageView wechat;
    private OnShareClickListener clickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share);
        setCanceledOnTouchOutside(true);

        exit = (TextView) findViewById(R.id.exit);
        myfrends = (ImageView) findViewById(R.id.myfrends);
        sina = (ImageView) findViewById(R.id.sina);
        wechat = (ImageView) findViewById(R.id.wechat);

        exit.setOnClickListener(this);
        myfrends.setOnClickListener(this);
        sina.setOnClickListener(this);
        wechat.setOnClickListener(this);

    }

    public ShareDialog(Context activity) {
        super(activity, R.style.DialogStyle);
        this.activity = activity;
        this.show();
    }

    @Override
    public void onClick(View v) {
        if (clickListener != null) {

            switch (v.getId()) {
                case R.id.exit:

                    break;
                case R.id.myfrends:
                    clickListener.onYinClick();
                    break;
                case R.id.sina:
                    clickListener.onSinaClick();

                    break;
                case R.id.wechat:
                    clickListener.onWechatClick();
                    break;

            }
        }
        dismiss();
    }

    public interface OnShareClickListener {

        public void onWechatClick();  //微信

        public void onSinaClick();     //新浪

        public void onYinClick();      //我的好友

    }

    public void setOnShareClickListener(OnShareClickListener listener) {
        clickListener = listener;
    }

}

