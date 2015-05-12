package com.ylsg365.pai.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.ValidateUtil;
import com.ylsg365.pai.app.YinApi;

import org.json.JSONObject;

/**
 *注册账号页面
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener{
    private Button registerButton;
    private TextView validateCodeTextView;
    private EditText phoneEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private EditText validateCodeEditText;
    private String token;
    private boolean inQuest = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupToolbar();
        setTitle("注册账号");
        TextView leftTextView = (TextView)findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);

        loadViews();
        setupListeners();
    }



    @Override
    protected void loadViews() {
        registerButton = (Button)findViewById(R.id.btn_register_next);
        validateCodeTextView = (TextView)findViewById(R.id.text_validate_code);
        phoneEditText = (EditText)findViewById(R.id.edit_register_phone);
        passwordEditText = (EditText)findViewById(R.id.edit_register_password);
        passwordConfirmEditText = (EditText)findViewById(R.id.edit_register_password_confirm);
        validateCodeEditText = (EditText)findViewById(R.id.edit_register_verify_code);
    }

    @Override
    protected void setupListeners() {
        validateCodeTextView.setOnClickListener(this);
        registerButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_validate_code:   //获取验证码
                getValidateCode();
                break;
            case R.id.btn_register_next:
                register();                    //注册
                break;
        }

    }


    /**
     * 注册
     */
    private void register(){
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = passwordConfirmEditText.getText().toString().trim();
        String validateCode = validateCodeEditText.getText().toString().trim();

        if(!ValidateUtil.checkPhone(phone)){
            phoneEditText.requestFocus();
            return;
        }
        if(!ValidateUtil.checkUserPassword(password)){
            passwordEditText.requestFocus();
            return;
        }
        if(!ValidateUtil.checkUserPasswordConfirm(password, confirmPassword)){
            passwordConfirmEditText.requestFocus();
            return;
        }
        if(!ValidateUtil.checkValidateCode(validateCode)){
            validateCodeEditText.requestFocus();
            return;
        }


        YinApi.registerByPhoneStep_1(phone, password, validateCode, token, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                LogUtil.logd("registerByPhone", json.toString());
                if(JsonUtil.getBoolean(json, "status")){
                    token = JsonUtil.getString(json, "token");
                    toRigsterPageStep2();
                }else {
                    UIHelper.showToast("注册失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UIHelper.showToast("注册失败");
            }
        });
    }

    private void toRigsterPageStep2(){
        NavHelper.toRegisterPageStep_2ForResult(RegisterActivity.this, token);
    }

    /**
     * 获取短信验证码
     */
    private void getValidateCode(){
        if(inQuest) return;

        String phone = phoneEditText.getText().toString().trim();
        if(!ValidateUtil.checkPhone(phone)){
            phoneEditText.requestFocus();
            return;
        }

        inQuest = true;


        YinApi.getValidateCode(phone, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                LogUtil.logd("getValidateCode", json.toString());

                TimeCount timeCount = new TimeCount(60 * 1000, 1000);
                timeCount.start();


                if(JsonUtil.getBoolean(json, "status")){
                    token = JsonUtil.getString(json, "token");
                    String validateCode = JsonUtil.getString(json, "msg");
                    Toast.makeText(RegisterActivity.this, validateCode, Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                inQuest = false;
            }
        });
    }

    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            validateCodeTextView.setText("获取验证码");
            validateCodeTextView.setClickable(true);
            inQuest = false;
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            validateCodeTextView.setClickable(false);
            validateCodeTextView.setText(millisUntilFinished / 1000 + "秒");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == AccountSettingActivity.REGISTER_SUCCESS){
            finish();
        }
    }
}
