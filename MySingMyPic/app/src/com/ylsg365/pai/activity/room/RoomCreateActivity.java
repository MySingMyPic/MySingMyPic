package com.ylsg365.pai.activity.room;

import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.rockerhieu.emojicon.EmojiconEditText;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.dialog.MyDialog;
import com.ylsg365.pai.activity.view.TextWatcherAdapter;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.StringUtil;

import org.json.JSONObject;

/**
 * 创建包房页面
 */
public class RoomCreateActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private EmojiconEditText createEdit;
    private RadioGroup group, mic_group;
    private RadioButton btn_in_all, btn_mic_all, btn_in_pwd, btn_in_private, btn_mic_private;
    private Button btn_login_login;
    private String inStr = "0", micStr = "0", pwd = "";
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_create);

        setupToolbar();
        setTitle("创建房间");
        TextView leftTextView = (TextView) findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);

        loadViews();
        setupListeners();
    }

    @Override
    protected void loadViews() {
        createEdit = (EmojiconEditText) findViewById(R.id.edit_login_phone);
        group = (RadioGroup) findViewById(R.id.group);
        mic_group = (RadioGroup) findViewById(R.id.mic_group);
        btn_in_all = (RadioButton) findViewById(R.id.btn_in_all);
        btn_in_pwd = (RadioButton) findViewById(R.id.btn_in_pwd);
        btn_in_private = (RadioButton) findViewById(R.id.btn_in_private);
        btn_mic_all = (RadioButton) findViewById(R.id.btn_mic_all);
        btn_mic_private = (RadioButton) findViewById(R.id.btn_mic_private);
        btn_login_login = (Button) findViewById(R.id.btn_login_login);
    }

    @Override
    protected void setupListeners() {
        btn_login_login.setOnClickListener(this);
        group.setOnCheckedChangeListener(this);
        mic_group.setOnCheckedChangeListener(this);

//        createEdit.addTextChangedListener(new TextWatcherAdapter() {
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                createEdit.setText(s);
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 创建包房方法
             */
            case R.id.btn_login_login:
                name = createEdit.getText().toString();
                if (StringUtil.isNull(name)) {
                    Toast.makeText(RoomCreateActivity.this, "请输入房间名!", Toast.LENGTH_LONG).show();
                } else if ("2".equals(inStr) && StringUtil.isNull(pwd)) {
                    Toast.makeText(RoomCreateActivity.this, "请输入房间访问密码!", Toast.LENGTH_LONG).show();
                } else {
                    YinApi.addHouse(name.trim(), inStr, pwd.trim(), micStr, "12", "125", "公告", "154", "56456", "0", new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (JsonUtil.getBoolean(response, "status")) {
                                Toast.makeText(RoomCreateActivity.this, "包房创建成功!", Toast.LENGTH_LONG).show();
                                NavHelper.toMyRoomActivity(RoomCreateActivity.this);
                                finish();
                            } else {
                                Toast.makeText(RoomCreateActivity.this, "包房创建失败,请稍后重试!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(RoomCreateActivity.this, "包房创建失败,请稍后重试!", Toast.LENGTH_LONG).show();
                        }
                    });

                }

                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {

            case R.id.group:
                if (checkedId == R.id.btn_in_all) {
                    inStr = "0";
                } else if (checkedId == R.id.btn_in_private) {
                    inStr = "1";
                } else if (checkedId == R.id.btn_in_pwd) {
                    inStr = "2";
                    alertDialog();
                }
                break;
            case R.id.mic_group:
                if (checkedId == R.id.btn_mic_all) {
                    micStr = "0";
                } else if (checkedId == R.id.btn_mic_private) {
                    micStr = "1";
                }
                break;
        }
    }

    /**
     * 包房密码设置弹出框
     */
    private void alertDialog(){
        final MyDialog dialog = new MyDialog(RoomCreateActivity.this);
        dialog.tv_content2.setVisibility(View.GONE);
        dialog.tv_content.setText(pwd);
        dialog.setOnOkClickListener(new MyDialog.OnOkClickListener() {
            @Override
            public void onPositiveclick() {
                pwd =dialog.getStr();
            }

            @Override
            public void onNegativeclick() {
                btn_in_all.setChecked(true);
                inStr = "0";

            }
        });
    }
}
