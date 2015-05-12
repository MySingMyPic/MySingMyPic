package com.ylsg365.pai.activity.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.dialog.MyDialog;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.model.User;
import com.ylsg365.pai.model.UserService;
import com.ylsg365.pai.util.ConfigUtil;
import com.ylsg365.pai.util.FileUtils;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.StringUtil;
import com.ylsg365.pai.web.dic.EnumAction;
import com.ylsg365.pai.web.dic.EnumController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息页面
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    //用户昵称
    private TextView userNickNameTextView;
    //用户手机
    private TextView userPhoneTextView;
    //用户头像
    private ImageView userHeadImageView;
    //修改密码
    private View passwordModifyView;
    private Bitmap mBitmap;
    private String filepath;
    private MyDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        setupToolbar();
        setTitle("设置账号");
        TextView leftTextView = (TextView) findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);

        init();

    }

    @Override
    protected void loadViews() {
        userNickNameTextView = (TextView) findViewById(R.id.text_user_nickName);
        userPhoneTextView = (TextView) findViewById(R.id.text_user_phone);
        userHeadImageView = (ImageView) findViewById(R.id.img_user_avatar);
        passwordModifyView = findViewById(R.id.layout_edit_verify_code);

    }

    @Override
    protected void initViews() {
        if (UserService.isLogin()) {
            User user = UserService.getUser();
            userNickNameTextView.setText(user.getNickName());
            userPhoneTextView.setText(user.getPhone());

            ImageLoader.getInstance().displayImage(user.getHeadImg(), userHeadImageView);
        }
    }

    @Override
    protected void setupListeners() {

        passwordModifyView.setOnClickListener(this);
        userNickNameTextView.setOnClickListener(this);
        userPhoneTextView.setOnClickListener(this);
        userHeadImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_edit_verify_code:
                toPasswordModifyPageForResfult();
                break;
            case R.id.text_user_nickName:
                alertModifyDialog(userNickNameTextView.getText().toString());
                break;
            case R.id.text_user_phone:
                NavHelper.toPhoneBindActivityForResule(UserInfoActivity.this);
                break;
            case R.id.img_user_avatar:
                filepath = FileUtils.path + FileUtils.headPath;
                if (!new File(FileUtils.path).exists()) {
                    new File(FileUtils.path).mkdirs();
                    try {
                        new File(filepath).createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                NavHelper.toPicCutActivityForResule(UserInfoActivity.this, filepath);
                break;
        }
    }

    private void toPasswordModifyPageForResfult() {
        NavHelper.toPasswordModifyPageForResult(UserInfoActivity.this);
    }

    //昵称修改弹出框
    private void alertModifyDialog(String str) {
        final String content = str;

        dialog = new MyDialog(UserInfoActivity.this);
        dialog.setStr( str,"");
        dialog.setOnOkClickListener(new MyDialog.OnOkClickListener() {
            @Override
            public void onPositiveclick() {
                String t = dialog.getStr();
                if (!StringUtil.isNull(t)) {
                    updateNickName(t);
                }
            }
            @Override
            public void onNegativeclick() {
            }
        });
    }

    /**
     * 更新昵称方法
     * @param t
     */
    private void updateNickName(final String t) {

        String token = ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN);

        YinApi.updateNickName(token, t, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {


                if (JsonUtil.getBoolean(json, "status")) {
                    String msg = JsonUtil.getString(json, "msg");

                    userNickNameTextView.setText(t);
                    User user = UserService.getUser();
                    user.setNickName(t);
                    ConfigUtil.saveValue(ConfigUtil.CONFIG_USER, new Gson().toJson(user));

                    Toast.makeText(UserInfoActivity.this, msg, Toast.LENGTH_LONG).show();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NavHelper.REQUEST_GO_TO_LOGIN && resultCode == NavHelper.RESULT_GO_TO_LOGIN_SUCCESS) {
            UIHelper.showToast("修改密码成功，需要刷新");
        } else if (requestCode == NavHelper.REQUEST_GO_TO_PHONE_BIND && resultCode == NavHelper.RESULT_GO_TO_PHONE_BIND_SUCCESS) {
            UIHelper.showToast("修改绑定手机成功，需要刷新");
        } else if (requestCode == NavHelper.REQUEST_GO_TO_PICCUT) {   //更换头像返回方法
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                mBitmap = BitmapFactory.decodeFile(filepath, options);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

                if (mBitmap != null) {
                    userHeadImageView.setImageBitmap(mBitmap);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            File file = new File(filepath);
                            Map<String, File> files = new HashMap<String, File>();
                            files.put(file.getName(), file);
                            String url = Constants.WEB_SERVER_DOMAIN + "fileController/imgUpload" ;

                            try {
                                String str = YinApi.imgUplode(url, new HashMap<String, String>(), files);
                                if (!StringUtil.isNull(str)) {
                                    JSONObject json = new JSONObject(str);
                                    if (JsonUtil.getBoolean(json, "status")) {

                                        JSONArray array = JsonUtil.getJSONArray(json, "fileName");
                                        if (array.length() > 0) {
                                            str =  array.get(0).toString();
                                            updateImg(str);
                                        }

                                    }
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                    ;

            }
        }

    }

    private void updateImg(final String str) {
        YinApi.updateHeadImg(str, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                if (JsonUtil.getBoolean(json, "status")) {
                    User user = UserService.getUser();
                    user.setHeadImg(str);
//                    String token = ConfigUtil.getStringValue(ConfigUtil.CONFIG_TOKEN);
//                    user.setToken(token);

                    try {
                        UserService.saveUser(new JSONObject(new Gson().toJson(user)));
                    }catch (JSONException e){

                    }
                    Toast.makeText(UserInfoActivity.this, "设置成功!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UserInfoActivity.this, "头像设置失败,请稍后重试!", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserInfoActivity.this, "图片上传失败,请稍候再试!", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }

        super.onDestroy();
    }

}

