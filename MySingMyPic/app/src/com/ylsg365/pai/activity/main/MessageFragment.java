package com.ylsg365.pai.activity.main;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.message.PrivateMessageAdapter;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageFragment extends Fragment implements PrivateMessageAdapter.onRoomItemClickListener {
    private int currentPage = 0;
    private final int rows = 10;
    private SuperRecyclerView recyclerView;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    PrivateMessageAdapter messageAdapter;
    private boolean isRefresh = true;
    private boolean isLoad=true;

    private String mParam1;
    private String mParam2;

    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView titileTextView = (TextView) rootView.findViewById(R.id.toolbar_title);
        titileTextView.setText("消息");
        rootView.findViewById(R.id.text_toolbar_left).setVisibility(View.GONE);

        TextView rightTextView = (TextView) rootView.findViewById(R.id.text_right);
        rightTextView.setVisibility(View.VISIBLE);
        rightTextView.setText("发私信");

        rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHelper.toPrivateMessageSelectUserActivity(getActivity());
            }
        });

        recyclerView = (SuperRecyclerView) rootView.findViewById(R.id.recycler_song_category);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageAdapter = new PrivateMessageAdapter(getActivity(), R.layout.item_message_normal, infoList);
        messageAdapter.setOnRoomItemClickListener(this);
        recyclerView.setAdapter(messageAdapter);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
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
        }, 3);

        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoad=true;
                currentPage = 0;
                recyclerView.setLoadingMore(true);
                isRefresh = true;
                getPrivateMessageList(currentPage, rows);
            }
        });

        getPrivateMessageList(currentPage, rows);
        return rootView;
    }


    private void getPrivateMessageList(int currentPage, int row) {
        YinApi.getPrivateMessageList(currentPage, row, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getPrivateMessageList", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    infoList.clear();
                    if (isRefresh) {
                        messageAdapter.clearData();

                        if (infoList.size() == 0) {
                            infoList.add(new JSONObject());
                            infoList.add(new JSONObject());
                            infoList.add(new JSONObject());
                        }
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "msgs");



                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }
                    messageAdapter.addData(infoList);

                    if(isRefresh&&infoList.size() == 3){

                    }else if (infoList.size() < rows) {
                        isLoad=false;
                        recyclerView.setLoadingMore(false);

//                        Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                    messageAdapter.notifyDataSetChanged();
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


    @Override
    public void onRoomItemClick(int position) {
        JSONObject item=(JSONObject)messageAdapter.getItem(position);
        if(position>2)
        {
            NavHelper.toPrivateMessageSendActivity(getActivity(),JsonUtil.getInt(item,"userId"),JsonUtil.getString(item,"nickName"));
        }
    }
}
