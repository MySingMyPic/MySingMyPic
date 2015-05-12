package com.ylsg365.pai.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.event.NavEvent;
import com.ylsg365.pai.model.UserService;

import de.greenrobot.event.EventBus;

public class SettingActivity extends BaseActivity implements View.OnClickListener{
    private Button logoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        setupToolbar();
        setTitle("软件设置");
        TextView leftTextView = (TextView)findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);

        init();
    }


    @Override
    protected void loadViews() {
        logoutButton = (Button)findViewById(R.id.btn_setting_logout);
    }

    @Override
    protected void setupListeners() {
        logoutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_setting_logout:
                logout();
        }
    }

    private void logout(){
        NavHelper.finish(SettingActivity.this);
        EventBus.getDefault().post(new NavEvent(R.id.layout_main_bottom_bar_home));
        UserService.logout();
    }
}
