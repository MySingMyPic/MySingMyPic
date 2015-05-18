package com.ylsg365.pai.activity.message;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.Listener.OnItemClickListener;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.app.YinApplication;
import com.ylsg365.pai.util.DensityUtil;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PrivateMessageSelectUserActivity extends BaseActivity implements OnItemClickListener {

    private SuperRecyclerView recyclerView;
    private MessageUserAdapter messageUserAdapter;
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    private boolean isRefresh = false;
    private boolean isLoad=true;
    private TextView sendMessage;
//    private int selectPos=-1;
//    private JSONObject selectObject=null;
    private List<Integer> mSelPosList = new ArrayList<Integer>();
    private boolean mIsFromKaraOK = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_message_select_user);
        setupToolbar();
        
        Bundle data = getIntent().getExtras();
        if(data != null) {
            mIsFromKaraOK = data.getBoolean("karaok");
            setTitle(data.getString("title"));
        } else {
            setTitle("选择联系人");
        }

        recyclerView = (SuperRecyclerView) findViewById(R.id.recycler);
        sendMessage=(TextView)findViewById(R.id.send_message);
        sendMessage.setEnabled(false);
        if(mIsFromKaraOK) {
            sendMessage.setText("确认");
        }
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                    c.drawLine(child.getLeft() + DensityUtil.dip2px(YinApplication.getInstance(), 15), child.getBottom(), child.getRight(), child.getBottom(), paint);
                }
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
//        recyclerView.setAdapter(new NumberedAdapter(R.layout.item_user, 30));

        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

               if(isLoad){
                   isRefresh  = false;
                   getAttentions(currentPage++, rows);
               }
                else{
                   recyclerView.setLoadingMore(false);
                   recyclerView.hideMoreProgress();
               }
            }
        },3);

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 0;
                recyclerView.setLoadingMore(true);
                isRefresh = true;
                isLoad=true;
                getAttentions(currentPage, rows);
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelPosList!=null && !mSelPosList.isEmpty())
                {
                    JSONObject obj = null;
                    if(mIsFromKaraOK) {
                        ArrayList<String> idList = new ArrayList<String>();
                        ArrayList<String> headList = new ArrayList<String>();
                        for(Integer i : mSelPosList) {
                            obj = messageUserAdapter.getItem(i);
                            idList.add(JsonUtil.getString(obj, "userId"));
                            headList.add(JsonUtil.getString(obj, "headImg"));
                        }
                        Intent intent = new Intent();
                        intent.putStringArrayListExtra("id", idList);
                        intent.putStringArrayListExtra("head", headList);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        obj = messageUserAdapter.getItem(mSelPosList.get(0));
                    NavHelper.toPrivateMessageSendActivity(PrivateMessageSelectUserActivity.this,JsonUtil.getInt(obj,"userId"),JsonUtil.getString(obj,"nickName"));
                    NavHelper.finish(PrivateMessageSelectUserActivity.this);
                    }
                }
            }
        });
        messageUserAdapter = new MessageUserAdapter(this,R.layout.item_user_message_select, infoList);
        messageUserAdapter.setOnItemClickListener(PrivateMessageSelectUserActivity.this);
        recyclerView.setAdapter(messageUserAdapter);
        messageUserAdapter.setSelPosList(mSelPosList);

        getAttentions(currentPage, rows);
    }

    private  int currentPage = 0;
    private static final int rows = 10;

    private void getAttentions(int currentPage, final int rows){
        YinApi.getMyContact(currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getMyContact", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if (isRefresh) {
                        messageUserAdapter.clear();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "data");
                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        JSONObject item=JsonUtil.getJSONObject(infoJsonArray, i);
                        infoList.add(item);
                    }
                    messageUserAdapter.addData(infoList);
                    if (infoList.size() < rows) {
                        isLoad=false;
                        recyclerView.setLoadingMore(false);
//                        Toast.makeText(PrivateMessageUserListActivity.this, "没有更多数据", Toast.LENGTH_LONG).show();
                    }

                }
                messageUserAdapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                messageUserAdapter.notifyDataSetChanged();
                recyclerView.hideMoreProgress();
            }
        });
    }

    @Override
    public void onItemClick(View view, int postion) {
        Integer pos = Integer.valueOf(postion);
        if(mIsFromKaraOK) {
            if(mSelPosList.isEmpty() || mSelPosList.size() < 2 && !mSelPosList.contains(pos)) {
                mSelPosList.add(pos);
            } else {
                if(mSelPosList.contains(pos)) {
                    mSelPosList.remove(pos);
                } else {
                    Toast.makeText(getBaseContext(), "最多选两个", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
                mSelPosList.clear();
                mSelPosList.add(pos);
        }
        if(mSelPosList.isEmpty()) {
            sendMessage.setEnabled(false);
        } else {
            sendMessage.setEnabled(true);
        }
//        JSONObject jsonObject = messageUserAdapter.getItem(postion);
//
//        if(selectPos!=postion){
//            selectObject=jsonObject;
//            selectPos=postion;
//        }
//        else{
//            selectObject=null;
//            selectPos=-1;
//        }
//        messageUserAdapter.setSelectPso(selectPos);

        messageUserAdapter.notifyDataSetChanged();
    }
}
