package com.ylsg365.pai.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.util.StringUtil;


/**
 * 自定义通用dialog
 * @author lanzhihong
 */
public class MyDialog extends Dialog implements OnClickListener {

    private Context activity;
    public TextView tv_ok, tv_bad;
    private OnOkClickListener clickListener;
    public TextView tv_content;
    public TextView tv_content2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_my);
        setCanceledOnTouchOutside(true);

        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content2 = (TextView) findViewById(R.id.tv_content2);

        tv_ok = (TextView) findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(this);
        tv_bad = (TextView) findViewById(R.id.tv_bad);
        tv_bad.setOnClickListener(this);
    }

    public MyDialog(Context activity) {
        super(activity, R.style.DialogStyle);
        this.activity = activity;
        this.show();
    }

    @Override
    public void onClick(View v) {

        if (clickListener != null) {

            switch (v.getId()) {
                case R.id.tv_ok:
                    clickListener.onPositiveclick();
                    break;
                case R.id.tv_bad:
                    clickListener.onNegativeclick();
                    break;

            }
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE); imm.hideSoftInputFromWindow(tv_content.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(tv_content2.getWindowToken(), 0);
            this.dismiss();
        }
    }


    public void setStr(String str,String str2){
        if (tv_content != null) {
            tv_content.setText(str);
        }
        if (tv_content2 != null) {
           tv_content2.setText(str2);
        }
    }
    public String getStr() {
        String str = "";
        if (tv_content != null) {
            str = tv_content.getText().toString();
        }
        return str;
    }
    public String getStr2() {
        String str = "";
        if (tv_content2 != null) {
            str = tv_content2.getText().toString();
        }
        return str;
    }

    public interface OnOkClickListener {
        public void onPositiveclick();   //确定事件

        public void onNegativeclick();    //取消事件

    }

    public void setOnOkClickListener(OnOkClickListener listener) {
        clickListener = listener;
    }

}

