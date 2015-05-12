package com.ylsg365.pai.activity.user;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;

public class WithdrawActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        setupToolbar();
        setTitle("取现");
        TextView leftTextView = (TextView)findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);
    }


}
