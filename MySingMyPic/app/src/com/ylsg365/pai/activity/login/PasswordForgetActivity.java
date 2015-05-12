package com.ylsg365.pai.activity.login;

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
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.BmpUtils;
import com.ylsg365.pai.util.ConfigUtil;
import com.ylsg365.pai.util.FileUtils;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.StringUtil;
import com.ylsg365.pai.util.ValidateUtil;

import org.json.JSONObject;

/**
 * 忘记密码页面
 */
public class PasswordForgetActivity extends BaseActivity {
    private static EditText edit_phone;
    private static EditText edit_verify_code;
    private static TextView get_verify_code;
    private boolean inQuest = false;
    private static String token;
    private static Button btn_register_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_forget);

        loadView();
        setupToolbar();
        setTitle("找回账号或者密码");
        TextView leftTextView = (TextView) findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);

    }

    private void loadView() {
        edit_phone = (EditText) findViewById(R.id.edit_phone);
        edit_verify_code = (EditText) findViewById(R.id.edit_verify_code);
        get_verify_code = (TextView) findViewById(R.id.get_verify_code);
        btn_register_next = (Button) findViewById(R.id.btn_register_next);
        btn_register_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = edit_phone.getText().toString();
                String verify_code = edit_verify_code.getText().toString();
                token = ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN);

                if (StringUtil.isNull(phone) || StringUtil.isNull(verify_code)) {
                    return;
                }
                //根据手机号找到用户
                YinApi.findPwdMember(phone, verify_code, token,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject json) {
                        LogUtil.logd("getValidateCode", json.toString());

                        if (JsonUtil.getBoolean(json, "status")) {
                            token = JsonUtil.getString(json, "token");
                            String nickName = JsonUtil.getString(json, "nickName");
                            String headImg = JsonUtil.getString(json, "headImg");

                            ConfigUtil.saveValue(ConfigUtil.CONFIG_TOKEN,token);
                            //到重置密码界面
                            toPassWordReset(token, nickName, headImg);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }
        });
        get_verify_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValidateCode();
            }
        });
    }

    /**
     * 获取短信验证码
     */
    private void getValidateCode() {

        if (inQuest) return;

        String phone = edit_phone.getText().toString().trim();
        if (!ValidateUtil.checkPhone(phone)) {
            edit_phone.requestFocus();
            return;
        }

        inQuest = true;


        YinApi.getValidateCode(phone, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                LogUtil.logd("getValidateCode", json.toString());

                TimeCount timeCount = new TimeCount(60 * 1000, 1000);
                timeCount.start();


                if (JsonUtil.getBoolean(json, "status")) {
                    token = JsonUtil.getString(json, "token");
                    String validateCode = JsonUtil.getString(json, "msg");

                    ConfigUtil.saveValue(ConfigUtil.CONFIG_TOKEN,token);

                    Toast.makeText(PasswordForgetActivity.this, validateCode, Toast.LENGTH_LONG).show();

                }else {
                    String msg = JsonUtil.getString(json, "msg");
                    UIHelper.showToast(msg);
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
            get_verify_code.setText("获取验证码");
            get_verify_code.setClickable(true);
            inQuest = false;
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            get_verify_code.setClickable(false);
            get_verify_code.setText(millisUntilFinished / 1000 + "秒");
        }
    }

    private void toPassWordReset(String token, String nickName, String headImg) {
        NavHelper.toPassWordResetActivity(PasswordForgetActivity.this, token, nickName, headImg);
    }
}
