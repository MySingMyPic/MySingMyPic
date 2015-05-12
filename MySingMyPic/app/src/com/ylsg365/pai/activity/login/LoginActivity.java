package com.ylsg365.pai.activity.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.main.MainActivity;
import com.ylsg365.pai.event.NavEvent;
import com.ylsg365.pai.event.UserInfoRefreshEvent;
import com.ylsg365.pai.model.UserService;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.util.ConfigUtil;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.ValidateUtil;
import com.ylsg365.pai.app.YinApi;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private Button loginButton;
    private Button registerButton;
    private Button forgetPwdButton;
    private EditText phoneEditText;
    private EditText passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupToolbar();
        setTitle("请先登录");
        TextView leftTextView = (TextView)findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);

        init();
    }


    @Override
    protected void loadViews() {
        loginButton = (Button)findViewById(R.id.btn_login_login);
        registerButton = (Button)findViewById(R.id.btn_login_register);
        forgetPwdButton = (Button)findViewById(R.id.btn_login_password_forget);
        phoneEditText = (EditText)findViewById(R.id.edit_login_phone);
        passwordEditText = (EditText)findViewById(R.id.edit_login_password);
    }

    @Override
    protected void setupListeners() {
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        forgetPwdButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login_login:
                loginByPhone();
                break;
            case R.id.btn_login_register:
                toRegisterPage();
                break;
            case R.id.btn_login_password_forget:
                toPassWordForget();
                break;
        }
    }

    private void toPassWordForget() {
        NavHelper.toPassWordForgetActivity(LoginActivity.this);
    }

    private void toRegisterPage(){
        NavHelper.toRegisterPageStep_1(LoginActivity.this);
    }

    private void loginByPhone(){
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if(!ValidateUtil.checkPhone(phone)){
            phoneEditText.requestFocus();
            return;
        }
        if(!ValidateUtil.checkUserPassword(password)){
            passwordEditText.requestFocus();
            return;
        }


        showLoading();
        YinApi.loginByPhone(phone, password, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("loginByPhone", response.toString());
                if (JsonUtil.getBoolean(response, "status")) {
                    String token = JsonUtil.getString(response, "token");
                    ConfigUtil.saveValue(ConfigUtil.CONFIG_TOKEN, token);

                    YinApi.getUserInfo(new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            LogUtil.logd("getUserInfo", response.toString());
                            hideLoading();

                            if (JsonUtil.getBoolean(response, "status")) {

                                UserService.saveUser(response);

                                UIHelper.showToast(R.string.toast_login_success);

                                UIHelper.hideSoftInput();
                                setResult(MainActivity.RESULT_LOGIN_SUCCESS);
                                NavHelper.finish(LoginActivity.this);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideLoading();
                            UIHelper.showToast(R.string.toast_login_failed);
                        }
                    });


                }else {
                    hideLoading();
                    UIHelper.showToast(R.string.toast_login_failed);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoading();
                UIHelper.showToast(R.string.toast_login_failed);
            }
        });


    }
}
