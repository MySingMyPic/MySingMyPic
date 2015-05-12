package com.ylsg365.pai.activity.user;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.model.User;
import com.ylsg365.pai.model.UserService;

/**
 * 我的音乐币页面
 */
public class MoneyManagerActivity extends BaseActivity {
    private TextView moneyNumTextView;
    private RelativeLayout deposit;
    private RelativeLayout recharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_manager);

        setupToolbar();
        setTitle("音乐币管理");
        TextView leftTextView = (TextView)findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);

        deposit = (RelativeLayout) findViewById(R.id.layout_edit_password_new);
        recharge = (RelativeLayout) findViewById(R.id.layout_edit_password_new_confirm);
        deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到提现页面
                NavHelper.toDepositActivity(MoneyManagerActivity.this);
            }
        });
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到充值页面
                NavHelper.toRechargeActivity(MoneyManagerActivity.this);
            }
        });

        init();
    }

    @Override
    protected void loadViews() {
        moneyNumTextView = (TextView)findViewById(R.id.text_moneyNum);
    }

    @Override
    protected void initViews() {
        if(UserService.isLogin()){
            User user = UserService.getUser();

            moneyNumTextView.setText(user.getBalance() + "");
        }

    }
}
