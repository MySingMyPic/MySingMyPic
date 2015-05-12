package com.ylsg365.pai.activity.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.face.FaceImage;
import com.ylsg365.pai.face.FaceListener;
import com.ylsg365.pai.face.FaceView;
import com.ylsg365.pai.face.KeyboardListenRelativeLayout;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONObject;

public class NewsInfoSendActivity extends BaseActivity implements View.OnClickListener,FaceListener {
    private TextView titleRightTextView;
    private EditText contentEditText;

    KeyboardListenRelativeLayout topRoot;
    private RelativeLayout faceLayout;
    View faceView;
    boolean faceFlag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsinfo_send);

        setupToolbar();

        setTitle("新鲜事");

        init();

    }

    @Override
    protected void loadViews() {
        titleRightTextView = (TextView)findViewById(R.id.text_right);
        contentEditText = (EditText)findViewById(R.id.edit_content);
        faceLayout=(RelativeLayout)findViewById(R.id.face_layout);
        topRoot=(KeyboardListenRelativeLayout)findViewById(R.id.top_root);
        FaceView face=new FaceView();
        faceView=face.faceView(this, this);

        faceLayout.addView(faceView);
    }

    @Override
    protected void initViews() {
        titleRightTextView.setVisibility(View.VISIBLE);
        titleRightTextView.setText("发送");
        topRoot.setOnKeyboardStateChangedListener(new KeyboardListenRelativeLayout.IOnKeyboardStateChangedListener() {

            public void onKeyboardStateChanged(int state) {
                switch (state) {
                    case KeyboardListenRelativeLayout.KEYBOARD_STATE_HIDE://软键盘隐藏
                        faceFlag=true;
                        showFace();

                        break;
                    case KeyboardListenRelativeLayout.KEYBOARD_STATE_SHOW://软键盘显示
                        faceFlag=false;
                        showFace();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    public void showFace()
    {
        if(faceFlag==false)
        {
            faceLayout.removeAllViews();
        }
        else{
            InputMethodManager inputmanger = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(topRoot.getWindowToken(), 0);
            faceLayout.removeAllViews();
            faceLayout.addView(faceView);
        }
    }



    @Override
    protected void setupListeners() {
        titleRightTextView.setOnClickListener(this);
//        findViewById(R.id.layout_private_message).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_right:
//                showChat();
                sendNewsInfo();
                break;
            case R.id.layout_private_message:
                toPrivateMessageUserListPage();
                break;
        }
    }

    private void toPrivateMessageUserListPage(){
        NavHelper.toPrivateMessageUserListPage(this);
    }

    private void sendNewsInfo(){
        String content = contentEditText.getText().toString().trim();
        LogUtil.logd("sendComment conent", content);
        YinApi.sendNewsInfo(content, "", "", new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("sendComment", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    UIHelper.showToast("发送成功");
                } else {
                    UIHelper.showToast("发送失败");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UIHelper.showToast("发送失败");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == NavHelper.RESULT_SELECT_USER_SUCCESS){
            JSONObject userJsonObj = JsonUtil.getJSONObject(data.getStringExtra("selectedUser"));
            UIHelper.showToast(JsonUtil.getString(userJsonObj, "nickName"));
            if(contentEditText != null){
                contentEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(contentEditText, InputMethodManager.SHOW_IMPLICIT);

                appendContent(JsonUtil.getString(userJsonObj, "nickName"));
            }

        }
    }

    private void appendContent(String userNickName){
        SpannableString spanString = new SpannableString(String.format("@%s", userNickName));
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.purple));
        spanString.setSpan(span, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        contentEditText.append(spanString);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void selectFace(int face)
    {
        Bitmap bitmap = null;

        String str = FaceImage.strings.get(face);

        bitmap = BitmapFactory.decodeResource(getResources(), face);
        ImageSpan imageSpan = new ImageSpan(this,small(bitmap));

        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(imageSpan, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        contentEditText.append(spannableString);
    }

    private static Bitmap small(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.6f,0.6f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }

    String zhengze = "#[1-9]#|#1[0-9]#|#2[0-5]#";
    public void removeFace()
    {

    }
}
