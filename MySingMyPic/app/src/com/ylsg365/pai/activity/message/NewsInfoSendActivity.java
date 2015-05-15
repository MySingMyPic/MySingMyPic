package com.ylsg365.pai.activity.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Spannable;
import android.text.SpannableString;
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
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.Listener.OnItemClickListener;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.face.FaceImage;
import com.ylsg365.pai.face.FaceListener;
import com.ylsg365.pai.face.FaceView;
import com.ylsg365.pai.face.KeyboardListenRelativeLayout;
import com.ylsg365.pai.imagedisplay.ImageDisplayActivity;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsInfoSendActivity extends BaseActivity implements View.OnClickListener,FaceListener {
    private TextView titleRightTextView;
    private EditText contentEditText;

    KeyboardListenRelativeLayout topRoot;
    private RelativeLayout faceLayout;
    View faceView;
    boolean faceFlag=false;

    //操作
    ImageView atOp,faceOp,ImgOp;

    //插入图片
    private SuperRecyclerView infoImageRecyclerView;
    private NewsInfoSendImgAdapter newsInfoImgAdapter;
    private List<String> images=new ArrayList<String>();
    String filepath="";
    Bitmap mBitmap;

    //@某人
    List<JSONObject> userList=new ArrayList<JSONObject>();
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

        atOp=(ImageView)findViewById(R.id.at);
        faceOp=(ImageView)findViewById(R.id.face);
        ImgOp =(ImageView)findViewById(R.id.pic);

        FaceView face=new FaceView();
        faceView=face.faceView(this, this);

        faceLayout.addView(faceView);

        //新鲜事图片
        infoImageRecyclerView = (SuperRecyclerView) findViewById(R.id.recycler_imgs);
        LinearLayoutManager layoutManager = new LinearLayoutManager(NewsInfoSendActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        infoImageRecyclerView.setLayoutManager(layoutManager);
        infoImageRecyclerView.setLoadingMore(false);

//        infoImageRecyclerView.hideProgress();
        infoImageRecyclerView.hideMoreProgress();
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

        showFace();
        ImgOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavHelper.toImageSelectList(NewsInfoSendActivity.this, NavHelper.RESULT_SELECT_IMAGE_SUCCESS);

            }
        });

        atOp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHelper.toPrivateMessageUserListPage(NewsInfoSendActivity.this);
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
        String image="";
        if(images.size()>0)
        {
            for(String item:images)
            {
                image+=item+";";
            }
            if(image.length()>0)
                image=image.substring(0,image.length()-1);
        }
        Log.e("sendComment image","image:"+image+"");
        String users=getUsers(content);
        Log.e("sendComment user","user"+users+"");
        YinApi.sendNewsInfo(content, image, users, new Response.Listener<JSONObject>() {
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
                Log.e("sendComment",error.toString()+"");
                UIHelper.showToast("发送失败");
            }
        });
    }


    private void appendContent(String userNickName){
        SpannableString spanString = new SpannableString(String.format("@%s", userNickName+" "));
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
            if(contentEditText != null){
                contentEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(contentEditText, InputMethodManager.SHOW_IMPLICIT);

                appendContent(JsonUtil.getString(userJsonObj, "nickName"));
            }
            }
        }
        else if (requestCode == NavHelper.RESULT_SELECT_IMAGE_SUCCESS) {   //获取图片
            if(data!=null){
            ArrayList<String> imageList = data.getStringArrayListExtra("imageList");

            if(imageList!=null&&imageList.size()>0){
                images.addAll(imageList);
            Log.e("getimageList",imageList.get(0));

            if(newsInfoImgAdapter==null){
            newsInfoImgAdapter = new NewsInfoSendImgAdapter(R.layout.item_info_img, imageList);
            infoImageRecyclerView.setAdapter(newsInfoImgAdapter);
            newsInfoImgAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(View view, int postion) {
                    String item=(String) newsInfoImgAdapter.getItem(postion);
                    Intent it=new Intent(NewsInfoSendActivity.this, ImageDisplayActivity.class);
                    it.putExtra(ImageDisplayActivity.PATH,item);
                    startActivity(it);
                }
            });
            }
            else
            {
                newsInfoImgAdapter.setData(imageList);
                newsInfoImgAdapter.notifyDataSetChanged();
            }
            }
            }
        }

    }
}
