package com.ylsg365.pai.activity.main;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.CommonAdapter;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.StringUtil;
import com.ylsg365.pai.util.ValidateUtil;
import com.ylsg365.pai.util.ViewHolder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 取现界面
 */
public class DepositActivity extends BaseActivity implements View.OnClickListener{
    private EditText money;
    private EditText banknum;
    private EditText name;
    private EditText bank;
    private EditText bankname;
    private EditText phone;
    private EditText validate_code;
    private EditText idnum;
    private TextView text_validate_code;
    private Button btn_register_next;
    private boolean inQuest = false;
    private List<Map<String,String>> bankList;
    private ListView listView;
    private CommonAdapter adapter;
    private String bankid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        setupToolbar();

        setTitle("取现");

        TextView liftText = (TextView) findViewById(R.id.text_toolbar_left);
        liftText.setVisibility(TextView.VISIBLE);

        initView();
        inidListener();

    }

    private void initView(){
        money = (EditText) findViewById(R.id.edit_register_money);
        banknum = (EditText) findViewById(R.id.edit_banknum);
        name = (EditText) findViewById(R.id.edit_name);

        bank = (EditText) findViewById(R.id.edit_bank);
        bankname = (EditText) findViewById(R.id.edit_register_bankname);
        phone = (EditText) findViewById(R.id.edit_phone);
        idnum = (EditText) findViewById(R.id.edit_idnum);
        validate_code = (EditText) findViewById(R.id.edit_register_verify_code);
        text_validate_code = (TextView) findViewById(R.id.text_validate_code);
        btn_register_next = (Button) findViewById(R.id.btn_register_next);
        listView = (ListView) findViewById(R.id.list);
    }

    private void inidListener(){
        text_validate_code.setOnClickListener(this);
        btn_register_next.setOnClickListener(this);
        name.setOnClickListener(this);
        banknum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String id = banknum.getText().toString();
                    if(StringUtil.isNull(id)){
                        Toast.makeText(DepositActivity.this,"请输入正确的银行卡号!", Toast.LENGTH_LONG).show();
                    }else{
                        YinApi.getBankBist(new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        },new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        });
                    }
                }
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.text_validate_code:
                getValidateCode();
                break;

            case R.id.btn_register_next:
                String phoneStr = phone.getText().toString().trim();
                String bankStr = bank.getText().toString().trim();
                String banknameStr = bankname.getText().toString().trim();
                String banknumStr = banknum.getText().toString().trim();
                String moneyStr = money.getText().toString().trim();
                String idnumStr = idnum.getText().toString().trim();
                String toastStr = null;
                if(StringUtil.isNull(phoneStr)){
                    toastStr = "请输入正确的手机号码";
                }else  if(StringUtil.isNull(bankid)){
                    toastStr = "请选择正确的银行";
                }else  if(StringUtil.isNull(banknameStr)){
                    toastStr = "请输入正确的支行";
                }else  if(StringUtil.isNull(banknumStr)){
                    toastStr = "请输入正确的卡号";
                }else  if(StringUtil.isNull(moneyStr)){
                    toastStr = "请输入正确的提现金额";
                }else  if(StringUtil.isNull(idnumStr)){
                    toastStr = "请输入正确的身份证号码";
                }
                if(StringUtil.isNull(toastStr)){

                    YinApi.cash(banknumStr,idnumStr,bankid,banknameStr,phoneStr,moneyStr,new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if(JsonUtil.getBoolean(response,"status")){
                                NavHelper.showToast(DepositActivity.this,"成功");
                                NavHelper.finish(DepositActivity.this);
                            }else{
                                NavHelper.showToast(DepositActivity.this,"失败");
                            }

                        }
                    },new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                }else{
                    NavHelper.showToast(DepositActivity.this,toastStr);
                }

                break;

            case R.id.name:
                bank.setText("请选择银行");
                listView.setVisibility(View.VISIBLE);

                break;
        }
    }

    /**
     * 获取短信验证码
     */
    private void getValidateCode(){
        if(inQuest) return;

        String phoneStr = phone.getText().toString().trim();
        if(!ValidateUtil.checkPhone(phoneStr)){
            phone.requestFocus();
            return;
        }

        inQuest = true;


        YinApi.getValidateCode(phoneStr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                LogUtil.logd("getValidateCode", json.toString());

                TimeCount timeCount = new TimeCount(60 * 1000, 1000);
                timeCount.start();


                if(JsonUtil.getBoolean(json, "status")){
                   String token = JsonUtil.getString(json, "token");
                    String validateCode = JsonUtil.getString(json, "msg");
                    Toast.makeText(DepositActivity.this, validateCode, Toast.LENGTH_LONG).show();
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

    /**
     * 获取银行列表
     */
    private void getBankList(){
        String banknumStr = banknum.getText().toString().trim();
        YinApi.getBankBist(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if(JsonUtil.getBoolean(response,"status")){

                    JSONArray array = JsonUtil.getJSONArray(response,"banks");
                    bankList = new ArrayList<Map<String, String>>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = JsonUtil.getJSONObject(array, i);
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("id", JsonUtil.getString(json, "bankId"));
                        map.put("name", JsonUtil.getString(json, "bankName"));
                        bankList.add(map);
                    }

                    if(bankList.size() > 0){
                        adapter = new CommonAdapter(DepositActivity.this,bankList,R.layout.banklist_item) {
                            @Override
                            public void convert(ViewHolder holder, Object item) {

                                holder.setText(R.id.text,((Map<String,String>)item).get("name"));

                            }
                        };

                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                bankid = bankList.get(position).get("id");
                                bank.setText(bankList.get(position).get("name"));
                                listView.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}
