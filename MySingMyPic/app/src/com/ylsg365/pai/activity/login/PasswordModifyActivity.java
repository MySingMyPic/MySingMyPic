package com.ylsg365.pai.activity.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.ValidateUtil;

import org.json.JSONObject;

/**
 * 修改密码界面
 */
public class PasswordModifyActivity extends BaseActivity implements View.OnClickListener{
    private Button passwordModifyButton;
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private EditText confirmPasswordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_modify);

        setupToolbar();
        setTitle("修改密码");
        TextView leftTextView = (TextView)findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);


        init();
    }

    @Override
    protected void loadViews() {
        passwordModifyButton = (Button)findViewById(R.id.btn_password_modify);

        oldPasswordEditText = (EditText)findViewById(R.id.edit_password_old);
        newPasswordEditText = (EditText)findViewById(R.id.edit_password_new);
        confirmPasswordEditText = (EditText)findViewById(R.id.edit_password_new_confirm);
    }


    @Override
    protected void setupListeners() {
        passwordModifyButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_password_modify:
                modifyPassword();
                break;
        }
    }

    /**
     * 修改密码方法
     */
    private void modifyPassword(){
        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if(!ValidateUtil.checkUserPassword(oldPassword)){
            oldPasswordEditText.requestFocus();
            return;
        }
        if(!ValidateUtil.checkUserPassword(newPassword)){
            oldPasswordEditText.requestFocus();
            return;
        }
        if(!ValidateUtil.checkUserPassword(confirmPassword)){
            confirmPasswordEditText.requestFocus();
            return;
        }
        if(!ValidateUtil.checkUserPasswordConfirm(newPassword, confirmPassword)){
            confirmPasswordEditText.requestFocus();
            return;
        }


        YinApi.modifyPassword(oldPassword, newPassword, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("modifyPassword", response.toString());


                if(JsonUtil.getBoolean(response, "status")){
                    modefiyPassowrdSuccess();
                }else {
                    String msg = JsonUtil.getString(response, "msg");
                    modefiyPassowrdFailed(msg);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

    }

    private void modefiyPassowrdSuccess(){
        setResult(NavHelper.RESULT_GO_TO_LOGIN_SUCCESS);
        NavHelper.finish(PasswordModifyActivity.this);
    }

    private void modefiyPassowrdFailed(String msg){
        UIHelper.showToast(msg);
    }
}
