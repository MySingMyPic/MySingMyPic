package com.ylsg365.pai.activity.room;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.view.CircleImageView;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.model.User;
import com.ylsg365.pai.model.UserService;
import com.ylsg365.pai.util.JsonUtil;

import org.json.JSONObject;

/**
 * 赠送礼物界面
 */
public class GiftInfoActivity extends BaseActivity {
    private TextView money;
    private TextView price;
    private TextView name;
    private CircleImageView img_gift;
    private Button btn_login_login;
    private String[] datas;
    private int type=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_info);

        setupToolbar();
        TextView leftTextView = (TextView) findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);

        TextView rightTextView = (TextView) findViewById(R.id.text_right);
        rightTextView.setVisibility(View.VISIBLE);
        rightTextView.setText("充值");
        rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //充值
            }
        });

        setTitle("送礼物");

        type=getIntent().getIntExtra("type",0);

        money = (TextView) findViewById(R.id.money);
        price = (TextView) findViewById(R.id.price);
        name = (TextView) findViewById(R.id.name);
        img_gift = (CircleImageView) findViewById(R.id.img_gift);
        btn_login_login = (Button) findViewById(R.id.btn_login_login);

        datas = getIntent().getStringArrayExtra("giftInfo");
        User user = UserService.getUser();
        price.setText(datas[2]);
        name.setText(datas[0]);
        money.setText(user.getBalance() + "");//我的音乐币剩余数量
        ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + datas[1], img_gift);

        btn_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 赠送礼物的方法
                 */
                if(type==0){
                YinApi.sendGift(datas[3], getIntent().getStringExtra("count"), getIntent().getStringExtra("houseId"), getIntent().getStringExtra("receiveUserId"), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (JsonUtil.getBoolean(response, "status")) {
                            NavHelper.showToast(GiftInfoActivity.this, "礼物赠送成功!");
                        } else {
                            NavHelper.showToast(GiftInfoActivity.this, "礼物赠送失败!");
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NavHelper.showToast(GiftInfoActivity.this, "礼物赠送失败!");
                    }
                });
                }
                else
                {
                    YinApi.sendGiftForNewsInfo(datas[3], getIntent().getStringExtra("count"), getIntent().getStringExtra("houseId"),type, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (JsonUtil.getBoolean(response, "status")) {
                                NavHelper.showToast(GiftInfoActivity.this, "礼物赠送成功!");
                            } else {
                                NavHelper.showToast(GiftInfoActivity.this, "礼物赠送失败!");
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            NavHelper.showToast(GiftInfoActivity.this, "礼物赠送失败!");
                        }
                    });
                }
            }
        });

    }


}
