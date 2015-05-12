package com.ylsg365.pai.activity.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.user.UserInfoActivity;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.event.UserInfoReloadEvent;
import com.ylsg365.pai.model.User;
import com.ylsg365.pai.model.UserService;
import com.ylsg365.pai.util.ConfigUtil;
import com.ylsg365.pai.util.DateUtil;
import com.ylsg365.pai.util.FileUtils;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.StringUtil;
import com.ylsg365.pai.util.ValidateUtil;
import com.ylsg365.pai.web.dic.EnumAction;
import com.ylsg365.pai.web.dic.EnumController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class AccountSettingActivity extends BaseActivity implements View.OnClickListener{
    private EditText userNickNameEditText;
    private EditText userAddressEditText;
    private Button registerButton;
    //radioGroup_gender
    private RadioGroup genderRadioGroup;

    private String token = "";
    private String gender = "1";
    private String headImgUrl = "";
    private ImageView img_headImg;
    private Bitmap mBitmap;
    private String filepath;
    private RelativeLayout layout_edit_head;

    public static final int REGISTER_SUCCESS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);


        setupToolbar();
        setTitle("设置账号");
        TextView leftTextView = (TextView)findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);

        token = getIntent().getStringExtra("token");

        init();
    }

    @Override
    protected void loadViews() {
        userNickNameEditText = (EditText)findViewById(R.id.edit_user_nickName);
        genderRadioGroup = (RadioGroup)findViewById(R.id.radioGroup_gender);
        userAddressEditText = (EditText)findViewById(R.id.edit_user_address);
        registerButton = (Button)findViewById(R.id.btn_register_done);
        layout_edit_head = (RelativeLayout)findViewById(R.id.layout_edit_head);
        img_headImg = (ImageView)findViewById(R.id.img_headImg);
    }

    @Override
    protected void initViews() {
        super.initViews();
    }

    @Override
    protected void setupListeners() {
        genderRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                gender = checkedId == R.id.radio_male ? "1" : "2";
            }
        });

        registerButton.setOnClickListener(this);
        layout_edit_head.setOnClickListener(this);
    }


    private void registerStep2(){
        String nickName = userNickNameEditText.getText().toString().trim();
        String gender = this.gender;
        String address = userAddressEditText.getText().toString().trim();
        String headImgUrl = this.headImgUrl;

        if(!ValidateUtil.checkUserNickName(nickName)){
            userNickNameEditText.requestFocus();
            return;
        }
        if(!ValidateUtil.checkUserAddress(address)){
            userAddressEditText.requestFocus();
            return;
        }

        YinApi.registerByPhoneStep_2(nickName, headImgUrl, gender, address, token, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("registerByPhoneStep_2", response.toString());
                if(JsonUtil.getBoolean(response, "status")){
                    token = JsonUtil.getString(response, "token");
                    UIHelper.showToast("注册成功");
                    ConfigUtil.saveValue(ConfigUtil.CONFIG_TOKEN, token);

                    NavHelper.finish(AccountSettingActivity.this, REGISTER_SUCCESS);

                    EventBus.getDefault().post(new UserInfoReloadEvent());
                }else {
                    UIHelper.showToast("注册失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_register_done:
                registerStep2();
                break;
            case R.id.layout_edit_head:
                filepath = FileUtils.path +  FileUtils.headPath;
                if (!new File(FileUtils.path).exists()) {
                    new File(FileUtils.path).mkdirs();
                    try {
                        new File(filepath).createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                NavHelper.toPicCutActivityForResule(AccountSettingActivity.this,filepath);

                break;

        }
    }

    /**
     * 选择头像回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case NavHelper.REQUEST_GO_TO_PICCUT:
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        mBitmap = BitmapFactory.decodeFile(filepath, options);
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }
                    if (mBitmap != null) {
                        img_headImg.setImageBitmap(mBitmap);

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
                                                str = array.get(0).toString();
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
                    }


                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 更新头像方法
     * @param str
     */
    private void updateImg(final String str){
        YinApi.updateHeadImg(str,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject json) {
                if(JsonUtil.getBoolean(json,"status")){
                    User user = UserService.getUser();
                    user.setHeadImg(str);
                    try {
                        UserService.saveUser(new JSONObject(new Gson().toJson(user)));
                    }catch (JSONException e){

                    }
                    Toast.makeText(AccountSettingActivity.this,"设置成功!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(AccountSettingActivity.this,"头像设置失败,请稍后重试!", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AccountSettingActivity.this,"图片上传失败,请稍候再试!", Toast.LENGTH_LONG).show();
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
