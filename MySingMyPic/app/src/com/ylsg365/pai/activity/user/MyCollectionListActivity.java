package com.ylsg365.pai.activity.user;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.NewInfoAdapter;
import com.ylsg365.pai.activity.Listener.OnItemClickListener;
import com.ylsg365.pai.activity.Listener.OnMyItemClickListener;
import com.ylsg365.pai.activity.Listener.OnUserHeadClickListener;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.adapter.CollectionListViewAdapter;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ylsg365 on 2015-03-23.
 */
public class MyCollectionListActivity extends BaseActivity implements OnUserHeadClickListener,OnItemClickListener,OnMyItemClickListener{
    private SuperRecyclerView recyclerView;
    ArrayList<JSONObject> infoList = new ArrayList<JSONObject>();
    private CollectionListViewAdapter adapter;
    private boolean isRefresh = false;
    private boolean isLoad=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection_list);

        setupToolbar();

        setTitle("我的收藏");

        recyclerView = (SuperRecyclerView) findViewById(R.id.recycler_newsInfo);
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        init();
        adapter = new CollectionListViewAdapter(this,R.layout.item_newsinfo, infoList);
        adapter.setOnUserHeadClickListener(MyCollectionListActivity.this);
        adapter.setOnMyItemClickListener(MyCollectionListActivity.this);
        adapter.setOnItemClickListener(MyCollectionListActivity.this);
        recyclerView.setAdapter(adapter );
        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                if(isLoad){
            	isRefresh = false;
                getMyCollection(currentPage++, rows);
                }
                else
                {
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
                getMyCollection(currentPage, rows);
            }
        });
        getMyCollection(currentPage, rows);
    }

    private  int currentPage = 0;
    private static final int rows = 10;

    private void getMyCollection(int currentPage, final int rows){
        YinApi.getMyCollection(currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getMyCollection", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    if(isRefresh){
                    adapter.clearData();
                    }
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "records");
                    infoList.clear();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                    	JSONObject item=JsonUtil.getJSONObject(infoJsonArray, i);
                    	JsonUtil.put(item, "isCollection", true);
                        infoList.add(item);
                    }
                    adapter.addData(infoList);
                    if(infoList.size()< rows){
                    	isLoad=false;
                        recyclerView.setLoadingMore(false);
//                        Toast.makeText(MyCollectionListActivity.this, "没有更多数据", Toast.LENGTH_LONG).show();
                    }

                }
                adapter.notifyDataSetChanged();
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
	public void OnCollectClick(View view, int postion) {
		// TODO Auto-generated method stub
		JSONObject infoObj = adapter.getItem(postion);
        boolean isCollection=JsonUtil.getBoolean(infoObj, "isCollection");
        if(isCollection==true)
        {
            int id=JsonUtil.getInt(infoObj, "recordId");
            int type=JsonUtil.getInt(infoObj, "ntype");
        	JsonUtil.put(infoObj, "isCollection", false);
        	if(type==2)
        	{
        		//新鲜事取消关�?
        		cancelCollectNewsInfo(id);
        	}
        }
        else 
        {
        	int id=JsonUtil.getInt(infoObj, "recordId");
            int type=JsonUtil.getInt(infoObj, "ntype");
        	JsonUtil.put(infoObj, "isCollection", true);
        	if(type==2)
        	{
        		//新鲜事关�?
        		collectNewsInfo(id);
        	}
        }
        	
        adapter.notifyDataSetChanged();

        
	}

	@Override
	public void OnShareClick(View view, int postion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnMsgClick(View view, int postion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnGiftClick(View view, int postion) {
		// TODO Auto-generated method stub
		
	}
	
	private void collectNewsInfo(int nid){
        YinApi.collectNewsInfo(nid, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("collectNewsInfo", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    UIHelper.showToast("收藏成功");

//                    attentionTextView.setText("取消关注");
                } else {
                    UIHelper.showToast(JsonUtil.getString(response, "msg"));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UIHelper.showToast("收藏失败");
            }
        });
    }
    
    private void cancelCollectNewsInfo(int nid){
        YinApi.cancelCollectNewsInfo(nid, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("cancelCollectNewsInfo", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    UIHelper.showToast("取消收藏成功");

//                    attentionTextView.setText("取消关注");
                } else {
                    UIHelper.showToast(JsonUtil.getString(response, "msg"));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UIHelper.showToast("取消收藏失败");
            }
        });
    }

	@Override
	public void onItemClick(View view, int postion) {
		// TODO Auto-generated method stub
	     String newsInfoStr = adapter.getItem(postion).toString();
	        
	        JSONObject infoObj = JsonUtil.getJSONObject(newsInfoStr);
	        int infoType = JsonUtil.getInt(infoObj, "ntype");
	        
	        switch (infoType){
	            case 0:
	            case 1:

	            	
//	                NavHelper.toVideoDetailPage(MyCollectionListActivity.this, newsInfoStr);
	                break;
	            case 2:
	            	String id=JsonUtil.getString(infoObj, "recordId");
	            	JsonUtil.put(infoObj, "nid", id);
	                NavHelper.toNewsInfoDetailPage(MyCollectionListActivity.this, infoObj.toString());
	                break;
	        } 
	}

	@Override
	public void onUserHeadClick(View view, int postion) {
		// TODO Auto-generated method stub
		 
	}
}
