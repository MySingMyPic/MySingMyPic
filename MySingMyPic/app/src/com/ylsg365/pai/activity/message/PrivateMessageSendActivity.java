package com.ylsg365.pai.activity.message;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.face.FaceImage;
import com.ylsg365.pai.face.FaceListener;
import com.ylsg365.pai.face.FaceView;
import com.ylsg365.pai.face.KeyboardListenRelativeLayout;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PrivateMessageSendActivity extends ActionBarActivity implements FaceListener,PrivateMessageRoomAdapter.onRoomItemClickListener {
    private int currentPage = 0;
    private final int rows = 10;
    PrivateMessageRoomAdapter adapter;
    private SuperRecyclerView recyclerView;
    private ImageView faceIcon,atIcon;
    private EditText input;
    private TextView send;
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    private boolean isRefresh = false;
    private boolean isLoad=true;

    KeyboardListenRelativeLayout topRoot;
    private RelativeLayout faceLayout;
    View faceView;
    boolean faceFlag=false;

    private int userId;
    private String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_message_send);
        setupToolbar();

        recyclerView = (SuperRecyclerView) findViewById(R.id.recycler);
        atIcon=(ImageView)findViewById(R.id.at);
        faceIcon =(ImageView)findViewById(R.id.biaoqing);
        input=(EditText)findViewById(R.id.input);
        send=(TextView)findViewById(R.id.send);

        faceLayout=(RelativeLayout)findViewById(R.id.face_layout);
        topRoot=(KeyboardListenRelativeLayout)findViewById(R.id.top_root);
        FaceView face=new FaceView();
        faceView=face.faceView(this, this);

        faceLayout.addView(faceView);

        faceIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(faceFlag==false)
                    faceFlag=true;
                else  faceFlag=false;
                showFace();
            }
        });
        showFace();
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                   input.setBackgroundDrawable(PrivateMessageSendActivity.this.getResources().getDrawable(R.drawable.liaotianshurukuangshuru));
                }else{
                    input.setBackgroundDrawable(PrivateMessageSendActivity.this.getResources().getDrawable(R.drawable.liaotianshurukuang));
                }
            }
        });

        topRoot.setOnKeyboardStateChangedListener(new KeyboardListenRelativeLayout.IOnKeyboardStateChangedListener() {

            public void onKeyboardStateChanged(int state) {
                switch (state) {
                    case KeyboardListenRelativeLayout.KEYBOARD_STATE_HIDE://软键盘隐藏

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

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(input.getText().toString());
            }
        });
        userId=getIntent().getIntExtra("userId",0);
        userName=getIntent().getStringExtra("userName");
        toolbarTitle.setText(userName+"");

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            Paint paint = new Paint();

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
            }

            @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDrawOver(c, parent, state);
                paint.setColor(getResources().getColor(R.color.line_radio));
                for (int i = 0, size = parent.getChildCount(); i < size; i++) {
                    View child = parent.getChildAt(i);
                    c.drawLine(child.getLeft() + 20, child.getBottom(), child.getRight(), child.getBottom(), paint);
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PrivateMessageRoomAdapter(this, userId,infoList);
        adapter.setOnRoomItemClickListener(this);
        recyclerView.setAdapter(adapter);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                isLoad=true;
                currentPage = 0;
                recyclerView.setLoadingMore(true);
                isRefresh = true;
                getPrivateMessageList(currentPage, rows);
            }
        }, 3);

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isLoad==false){
                    isRefresh = false;
                    recyclerView.setLoadingMore(false);
                    recyclerView.hideMoreProgress();

                }
                else{
                    isRefresh = false;
                    getPrivateMessageList(currentPage++, rows);
                }
            }
        });

        getPrivateMessageList(currentPage, rows);
    }

    private void getPrivateMessageList(int currentPage, int row) {
        YinApi.getPrivateMessageListOfUser(currentPage, row, userId, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getPrivateMessageListOfUser", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    infoList.clear();
                    if (isRefresh) {
                        adapter.clearData();

                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "msgs");


                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }
                    adapter.addData(infoList);

                    if (infoList.size() < rows) {
                        isLoad = false;
                        recyclerView.setLoadingMore(false);

//                        Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                    adapter.notifyDataSetChanged();
                }
                recyclerView.hideMoreProgress();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                recyclerView.hideMoreProgress();
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

    private Toolbar toolbar;
    private TextView toolbarTitle;
    private TextView leftTextView;
    private TextView rightTextView;

    public void setupToolbar() {
        View v = findViewById(R.id.toolbar);
        if (v != null) {
            toolbar = (Toolbar) v;
            setSupportActionBar(toolbar);
            toolbarTitle = (TextView) v.findViewById(R.id.toolbar_title);
            if (toolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
            leftTextView = (TextView) v.findViewById(R.id.text_toolbar_left);
            rightTextView=(TextView)v.findViewById(R.id.text_right);
            rightTextView.setText("关闭");
            leftTextView.setVisibility(View.GONE);
            rightTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavHelper.finish(PrivateMessageSendActivity.this);
                }
            });
        }
    }


    public void selectFace(int face)
    {
        Bitmap bitmap = null;

        String str = FaceImage.strings.get(face);

        bitmap = BitmapFactory.decodeResource(getResources(), face);
        ImageSpan imageSpan = new ImageSpan(this,small(bitmap));

        SpannableString spannableString = new SpannableString(str);
        spannableString.setSpan(imageSpan, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        input.append(spannableString);
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
    public void onRoomItemClick(int position) {

    }

    public void sendMessage(String msg)
    {
        YinApi.sendPrivateMessageOfUser(msg,userId,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("sendPrivateMessage", response.toString());

                if (JsonUtil.getBoolean(response, "status"))
                {
                    isLoad=true;
                    currentPage = 0;
                    recyclerView.setLoadingMore(true);
                    isRefresh = true;
                    getPrivateMessageList(currentPage, rows);
                }
                else
                {
                    Toast.makeText(PrivateMessageSendActivity.this, JsonUtil.getString(response, "msg"), Toast.LENGTH_LONG).show();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
}
