package com.ylsg365.pai.activity.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentSendActivity extends BaseActivity implements View.OnClickListener,FaceListener{
    private TextView titleRightTextView;
    public EditText comment;
    private JSONObject infoJsonObj;
    KeyboardListenRelativeLayout topRoot;
    private RelativeLayout faceLayout;
    View faceView;
    boolean faceFlag=false;
    //操作
    ImageView atOp,faceOp;
    //@某人
    List<JSONObject> userList=new ArrayList<JSONObject>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_send);

        infoJsonObj = JsonUtil.getJSONObject(getIntent().getStringExtra("info"));

        setupToolbar();

        setTitle("发评论");

        init();


    }

    @Override
    protected void loadViews() {
        titleRightTextView = (TextView)findViewById(R.id.text_right);
        comment=(EditText)findViewById(R.id.edit_content);
        faceLayout=(RelativeLayout)findViewById(R.id.face_layout);
        topRoot=(KeyboardListenRelativeLayout)findViewById(R.id.top_root);
        atOp=(ImageView)findViewById(R.id.at);
        faceOp=(ImageView)findViewById(R.id.face);
        FaceView face=new FaceView();
        faceView=face.faceView(this, this);

        faceLayout.addView(faceView);
        showFace();


    }

    @Override
    protected void initViews() {

        titleRightTextView.setVisibility(View.VISIBLE);
        titleRightTextView.setText("发送");

        topRoot.setOnKeyboardStateChangedListener(new KeyboardListenRelativeLayout.IOnKeyboardStateChangedListener() {

            public void onKeyboardStateChanged(int state) {
                switch (state) {
                    case KeyboardListenRelativeLayout.KEYBOARD_STATE_HIDE://软键盘隐藏
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
        atOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHelper.toPrivateMessageUserListPage(CommentSendActivity.this);
            }
        });
        faceOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(faceFlag==false)
                    faceFlag=true;
                else faceFlag=false;
                showFace();
            }
        });
    }

    private void appendContent(String userNickName){
        SpannableString spanString = new SpannableString(String.format("@%s", userNickName+" "));
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.purple));
        spanString.setSpan(span, 0, spanString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        comment.append(spanString);
    }

    private final Pattern AT_PATTERN = Pattern.compile("@[\\u4e00-\\u9fa5\\w\\-]+");

    private String getUsers(String content)
    {
        String result="";
        if(userList.size()>0){
            Matcher m = AT_PATTERN.matcher(content);
            while (m.find()) {
                String atUserName = m.group();
                atUserName=atUserName.substring("@".length(),atUserName.length());
                for(int i=0;i<userList.size();i++)
                {

                    JSONObject json=userList.get(i);
                    String name=JsonUtil.getString(json,"nickName");
                    if(name.trim().equals(atUserName))
                    {
                        result+=JsonUtil.getInt(json,"userId")+";";
                    }
                }
            }
            if(result.length()>0)
                result=result.substring(0,result.length()-1);
        }
        return result;
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_right:
                sendComment();
                break;
        }
    }

    private void sendComment(){
        int newsInfoId = JsonUtil.getInt(infoJsonObj, "nid");

        SpannableStringBuilder builder = new SpannableStringBuilder(comment.getText());
        Log.e("",""+builder.toString());
        String users=getUsers(builder.toString());
        YinApi.sendNewsInfoComment(newsInfoId, builder.toString(), users, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("sendComment", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    UIHelper.showToast("评论成功");
                } else {
                    UIHelper.showToast("评论失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UIHelper.showToast("评论失败");
            }
        });
    }

    public void selectFace(int face)
    {
        Bitmap bitmap = null;

        String str = FaceImage.strings.get(face);

        bitmap = BitmapFactory.decodeResource(getResources(), face);
        ImageSpan imageSpan = new ImageSpan(this,small(bitmap));

        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(imageSpan, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        comment.append(spannableString);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == NavHelper.RESULT_SELECT_USER_SUCCESS){
            if(data!=null){
            JSONObject userJsonObj = JsonUtil.getJSONObject(data.getStringExtra("selectedUser"));
            userList.add(userJsonObj);
            UIHelper.showToast(JsonUtil.getString(userJsonObj, "nickName"));
            if(comment != null){
                comment.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(comment, InputMethodManager.SHOW_IMPLICIT);

                appendContent(JsonUtil.getString(userJsonObj, "nickName"));
            }
            }
        }
    }
}
