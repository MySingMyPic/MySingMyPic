package com.ylsg365.pai.activity.user;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ylsg365.pai.OpenVipSelectActivity;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.dialog.BuySuccessDailogFragment;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.model.User;
import com.ylsg365.pai.model.UserService;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.StringUtil;

import org.json.JSONObject;

/**
 * 购买版权
 */
public class BuyTheCopyrightActivity extends BaseActivity {
    private TextView moneyNumTextView;
    private EditText name;
    private EditText idCard;
    private Button btn_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_the_copyright);

        setupToolbar();
        setTitle("购买版权");
        TextView leftTextView = (TextView) findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);


        TextView rightTextView = (TextView) findViewById(R.id.text_right);
        rightTextView.setText("充值");
        rightTextView.setVisibility(View.VISIBLE);
        rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHelper.toRechargeActivity(BuyTheCopyrightActivity.this);
            }
        });

        init();
    }

    @Override
    protected void loadViews() {
        moneyNumTextView = (TextView) findViewById(R.id.text_moneyNum);
        name = (EditText) findViewById(R.id.edit_password_new);
        idCard = (EditText) findViewById(R.id.edit_password_new_confirm);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameStr = name.getText().toString().trim();
                String idCardStr = idCard.getText().toString().trim();
                if(StringUtil.isNull(nameStr)){
                    NavHelper.showToast(BuyTheCopyrightActivity.this,"请输入正确的姓名!");
                }else if(StringUtil.isNull(idCardStr)){
                    NavHelper.showToast(BuyTheCopyrightActivity.this,"请输入正确的身份证号码!");
                }else{
                    YinApi.recordPay(getIntent().getStringExtra("id"),nameStr,idCardStr,new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (JsonUtil.getBoolean(response, "status")) {
                                BuySuccessDailogFragment vipFragment = BuySuccessDailogFragment.newInstance("已扣除" + getIntent().getStringExtra("money") + "音乐币\n您可以进入个人中心查看直接购买的歌曲");
                                vipFragment.show(BuyTheCopyrightActivity.this.getSupportFragmentManager(), "NoticeConfirmFragment");

                                TimeCount timeCount = new TimeCount(3 * 1000, 1000, vipFragment);
                                timeCount.start();
                            }else{
                                NavHelper.showToast(BuyTheCopyrightActivity.this, JsonUtil.getString(response,"msg"));
                            }
                        }
                    },new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                }
            }
        });
    }

    @Override
    protected void initViews() {
        if (UserService.isLogin()) {
            User user = UserService.getUser();

            moneyNumTextView.setText(user.getBalance() + "");
        }

        ((TextView)findViewById(R.id.notice)).setText("您要购买会员"+getIntent().getStringExtra("name")+"所唱的"+getIntent().getStringExtra("songName")+"版权,需要支付"+getIntent().getStringExtra("money")+"音乐币.");

    }

    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        private BuySuccessDailogFragment vipFragment;

        public TimeCount(long millisInFuture, long countDownInterval, BuySuccessDailogFragment fragment) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
            vipFragment = fragment;
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            vipFragment.dismiss();
            finish();
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示

        }
    }
}
