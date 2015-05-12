package com.ylsg365.pai.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.BmpUtils;
import com.ylsg365.pai.util.FileUtils;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.StringUtil;
import com.ylsg365.pai.util.ValidateUtil;

import org.json.JSONObject;

/**
 * 重置密码页面
 */
public class PassWordResetActivity extends BaseActivity implements View.OnClickListener {
    private ImageView img_user_avatar;
    private TextView text_user_nickName;
    private EditText edit_password_new;
    private EditText edit_password_new_confirm;
    private Button btn_password_modify;
    private String token;
    private String headImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        Intent intent = getIntent();
        headImg = intent.getStringExtra("headImg");
        String nickName = intent.getStringExtra("nickName");
        token = intent.getStringExtra("token");

        setupToolbar();
        loadViews();
        setupListeners();
        setTitle("重置密码");

        TextView leftTextView = (TextView) findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);

        if(!StringUtil.isNull(nickName)){
            text_user_nickName.setText(nickName);
        }

    }


    protected void loadViews() {
        img_user_avatar = (ImageView) findViewById(R.id.img_user_avatar);
        text_user_nickName = (TextView) findViewById(R.id.text_user_nickName);
        edit_password_new = (EditText) findViewById(R.id.edit_password_new);
        edit_password_new_confirm = (EditText) findViewById(R.id.edit_password_new_confirm);
        btn_password_modify = (Button) findViewById(R.id.btn_password_modify);

        ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN +headImg, img_user_avatar);
    }

    protected void setupListeners() {
        btn_password_modify.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        String password = edit_password_new.getText().toString().trim();
        String confirmPassword = edit_password_new_confirm.getText().toString().trim();

        if(!ValidateUtil.checkUserPassword(password)){
            edit_password_new.requestFocus();
            return;
        }
        if(!ValidateUtil.checkUserPasswordConfirm(password, confirmPassword)) {
            edit_password_new_confirm.requestFocus();
            return;
        }
        /**
         * 设置新密码方法
         */
        YinApi.setNewPassword(token, password,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {

                boolean status = json.optBoolean("status");

                if (status) {

                    Toast.makeText(PassWordResetActivity.this, "修改密码成功!", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(PassWordResetActivity.this, json.optString("msg"), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PassWordResetActivity.this, "修改密码失败!", Toast.LENGTH_LONG).show();
            }
        });

        NavHelper.finish(PassWordResetActivity.this);
    }
}
