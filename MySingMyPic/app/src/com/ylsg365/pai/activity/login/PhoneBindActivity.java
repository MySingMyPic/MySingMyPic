package com.ylsg365.pai.activity.login;

import android.os.CountDownTimer;
import android.os.Bundle;
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
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.ConfigUtil;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.StringUtil;
import com.ylsg365.pai.util.ValidateUtil;

import org.json.JSONObject;

/**
 * 绑定手机界面
 */
public class PhoneBindActivity extends BaseActivity implements TextView.OnClickListener{

    private EditText edit_password_old;
    private EditText edit_password_new;
    private EditText edit_register_verify_code;
    private Button btn_password_modify;
    private TextView text_validate_code;
    private boolean inQuest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_bind);

        setupToolbar();
        setTitle("绑定手机");
        loadViews();
        setupListeren();
        TextView leftTextView = (TextView)findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);
    }

    protected void loadViews() {
        edit_password_old = (EditText) findViewById(R.id.edit_password_old);
        edit_password_new = (EditText) findViewById(R.id.edit_password_new);
        edit_register_verify_code = (EditText) findViewById(R.id.edit_register_verify_code);
        btn_password_modify = (Button) findViewById(R.id.btn_password_modify);
        text_validate_code = (TextView) findViewById(R.id.text_validate_code);
    }

    private void setupListeren(){
        text_validate_code.setOnClickListener(this);
        btn_password_modify.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_password_modify:  //提交
            String phone = edit_password_old.getText().toString();
            String newPhone = edit_password_new.getText().toString();
            String validateCode = edit_register_verify_code.getText().toString();

                if(StringUtil.isNull(phone) || StringUtil.isNull(newPhone) ||StringUtil.isNull(validateCode)){
                    return;
                }

                if(!ValidateUtil.checkPhone(phone)){
                edit_password_old.requestFocus();
                return;
            }
                if(!ValidateUtil.checkPhone(newPhone)){
                    edit_password_old.requestFocus();
                    return;
                }

                YinApi.updatePhone(phone,newPhone,validateCode, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {

                        if (JsonUtil.getBoolean(json, "status")) {
                            String msg = JsonUtil.getString(json, "msg");
                            String token = JsonUtil.getString(json,"token");

                            ConfigUtil.saveValue(ConfigUtil.CONFIG_TOKEN,token);

                            Toast.makeText(PhoneBindActivity.this, msg, Toast.LENGTH_LONG).show();

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                //todo
               PhoneBindActivity.this.setResult(NavHelper.RESULT_GO_TO_PHONE_BIND_SUCCESS);
                NavHelper.finish(PhoneBindActivity.this);

                break;
            case R.id.text_validate_code:  //获取验证码
                getValidateCode();
                break;
        }
    }

    /**
     * 获取短信验证码
     */
    private void getValidateCode(){
        if(inQuest) return;

        String phone = edit_password_new.getText().toString().trim();
        if(!ValidateUtil.checkPhone(phone)){
            edit_password_new.requestFocus();
            return;
        }

        inQuest = true;


        YinApi.getValidateCode(phone, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                LogUtil.logd("getValidateCode", json.toString());

                TimeCount timeCount = new TimeCount(60 * 1000, 1000);
                timeCount.start();


                if (JsonUtil.getBoolean(json, "stauts")) {
                    String validateCode = JsonUtil.getString(json, "msg");
                    Toast.makeText(PhoneBindActivity.this, validateCode, Toast.LENGTH_LONG).show();
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
            text_validate_code.setText("获取验证码");
            text_validate_code.setClickable(true);
            inQuest = false;
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            text_validate_code.setClickable(false);
            text_validate_code.setText(millisUntilFinished / 1000 + "秒");
        }
    }
}
