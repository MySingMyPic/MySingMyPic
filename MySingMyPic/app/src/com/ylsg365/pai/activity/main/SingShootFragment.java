package com.ylsg365.pai.activity.main;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.NewInfoAdapter;
import com.ylsg365.pai.activity.Listener.OnItemClickListener;
import com.ylsg365.pai.activity.Listener.OnMyItemClickListener;
import com.ylsg365.pai.activity.Listener.OnUserHeadClickListener;
import com.ylsg365.pai.activity.video.VideoAddEffectActivity;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.event.NavEvent;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.LogUtil;
import com.ylsg365.pai.util.StringUtil;

import de.greenrobot.event.EventBus;

public class SingShootFragment extends Fragment implements OnItemClickListener, OnUserHeadClickListener,OnMyItemClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    //搜索
    private EditText searchInput;
    private ImageView searchIcon;
     SingShootAdapter singShootAdapter;
     SuperRecyclerView recyclerView;
     private int currentPage = 0;
     private final int rows = 10;
     private boolean isRefresh = false;
     private boolean isLoad=true;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingShootFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingShootFragment newInstance(String param1, String param2) {
        SingShootFragment fragment = new SingShootFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SingShootFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sing_shoot, container, false);
        TextView titileTextView = (TextView)rootView.findViewById(R.id.toolbar_title);
        titileTextView.setText("我唱我拍");
        rootView.findViewById(R.id.text_toolbar_left).setVisibility(View.GONE);
        View v = rootView.findViewById(R.id.text_right);
        v.setVisibility(View.VISIBLE);
        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHelper.toVideoStartActivity(getActivity());
            }
        });

        recyclerView = (SuperRecyclerView) rootView.findViewById(R.id.recycler_fresh);
//        recyclerView.setHasFixedSize(true);

        View header = LayoutInflater.from(getActivity()).inflate(R.layout.layout_category, null);

         singShootAdapter = new SingShootAdapter(header,getActivity(), R.layout.item_home, new ArrayList<JSONObject>());

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        singShootAdapter.setOnItemClickListener(SingShootFragment.this);
        singShootAdapter.setOnUserHeadClickListener(SingShootFragment.this);
        singShootAdapter.setOnMyItemClickListener(SingShootFragment.this);
        recyclerView.setAdapter(singShootAdapter);

        searchInput=(EditText)rootView.findViewById(R.id.edit_search);
        searchInput.setHint("输入歌曲名");
        searchIcon=(ImageView)rootView.findViewById(R.id.img_search);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = searchInput.getText().toString();
                if (StringUtil.isNull(keyword)) {
                    Toast.makeText(getActivity(), "请输入歌曲名进行搜索.", Toast.LENGTH_LONG).show();
                } else {
                    NavHelper.toSongActivity(getActivity(),keyword);
                }
            }
        });
        
        recyclerView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                if(isLoad){
                isRefresh = false;
                    getNewInfos(currentPage++, rows);
                }
                else
                {
                    recyclerView.setLoadingMore(false);
                    recyclerView.hideMoreProgress();
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
                getNewInfos(currentPage, rows);
            }
        });
        getNewInfos(currentPage, rows);
        
        return rootView;
    }

    private void getNewInfos(final int currentPage, final int rows){
        YinApi.getNewInfos(currentPage, rows, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtil.logd("getNewInfos", response.toString());

                if (JsonUtil.getBoolean(response, "status")) {
                    JSONArray infoJsonArray = JsonUtil.getJSONArray(response, "data");
                    if (isRefresh) {
                    	singShootAdapter.clearData();
                    }
                     ArrayList<JSONObject> infoList=new ArrayList<JSONObject>();
                    for (int i = 0; i < infoJsonArray.length(); i++) {
                        infoList.add(JsonUtil.getJSONObject(infoJsonArray, i));
                    }
                    singShootAdapter.addData(infoList);
                    if (infoList.size() < rows) {
                        isLoad=false;
                        recyclerView.setLoadingMore(false);
//                      /**/  Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_LONG).show();
                    }
                    singShootAdapter.notifyDataSetChanged();
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
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    // This method will be called when a SomeOtherEvent is posted
    public void onEvent(NavEvent navEvent){
//        Toast.makeText(getActivity(), "" + navEvent.id, Toast.LENGTH_SHORT).show();

        switch (navEvent.id){
            case R.id.layout_category_1:
                NavHelper.toNewsInfoSendPage(getActivity());
                break;
            case R.id.layout_category_2:
                NavHelper.toOriginalBaseListPage(getActivity(), NavHelper.REQUEST_ALL_ORIGINAL);
                break;
            case R.id.layout_category_3:
                NavHelper.toKaraokePage(getActivity());
                break;
            case R.id.layout_category_4:
                NavHelper.toVideoStartActivity(getActivity());
                break;
            case R.id.layout_category_5:
                NavHelper.toCappellaRecordPage(getActivity());
                break;
            case R.id.layout_category_6:
                NavHelper.toSingerPage(getActivity());
                break;
            case R.id.layout_category_7:
                NavHelper.toSongCategoryPage(getActivity());
                break;
            case R.id.layout_category_8:
                NavHelper.toGameCenterPage(getActivity());
                break;
        }
    }

    @Override
    public void onItemClick(View view, int postion) {
    	Log.e("","点击");
            String newsInfoStr = singShootAdapter.getItem(postion).toString();
        
        JSONObject infoObj = JsonUtil.getJSONObject(newsInfoStr);
        int infoType = JsonUtil.getInt(infoObj, "ntype");
        
        switch (infoType){
            case 0:
            case 1:
            case 2:
            case 3:
            	
                NavHelper.toVideoDetailPage(getActivity(), newsInfoStr);
                break;
            case 4:
            	
                NavHelper.toNewsInfoDetailPage(getActivity(), newsInfoStr);
                break;
        }       
    }


    /**
     * 点解列表中头像回调方法
     * @param view
     * @param postion
     */
    @Override
    public void onUserHeadClick(View view, int postion) {
        String newsInfoStr = singShootAdapter.getItem(postion).toString();

        JSONObject infoObj = JsonUtil.getJSONObject(newsInfoStr);
        int userId = JsonUtil.getInt(infoObj, "userId");

        NavHelper.toUserDetailPage(getActivity(), userId);
    }
    /**
     * item 收藏点击
     * @param view
     * @param postion
     */
    @Override
    public void OnCollectClick(View view, int postion) {
        String newsInfoStr = singShootAdapter.getItem(postion).toString();
        JSONObject infoObj = JsonUtil.getJSONObject(newsInfoStr);
        int nid = JsonUtil.getInt(infoObj, "nid");
        String count = (String)view.getTag();
        if(count.equals("0")) {
            collectNewsInfo(nid);
        } else {
            cancelCollectNewsInfo(nid);
        }
    }
    /**
     * item 点击
     * @param view
     * @param postion
     */
    @Override
    public void OnShareClick(View view, int postion) {

    }
    /**
     * item 点击
     * @param view
     * @param postion
     */
    @Override
    public void OnMsgClick(View view, int postion) {

    }

    /**
     * item 送礼物点击
     * @param view
     * @param postion
     */
    @Override
    public void OnGiftClick(View view, int postion) {

        String newsInfoStr = singShootAdapter.getItem(postion).toString();
        JSONObject infoObj = JsonUtil.getJSONObject(newsInfoStr);
        int userId = JsonUtil.getInt(infoObj, "userId");

        NavHelper.toGiftListActivity(SingShootFragment.this.getActivity(),"",userId+"");
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
                    UIHelper.showToast("收藏失败");
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
                    UIHelper.showToast("取消收藏失败");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UIHelper.showToast("取消收藏失败");
            }
        });
    }

    private void unCollectNewsInfo(){
//        YinApi.unAttentionToUser(userId + "", new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                LogUtil.logd("unAttentionToUser", response.toString());
//
//                if (JsonUtil.getBoolean(response, "status")) {
//                    UIHelper.showToast("取消关注成功");
//
//                    attentionTextView.setText("关注");
//                } else {
//                    UIHelper.showToast("操作失败");
//                }
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                UIHelper.showToast("操作失败");
//            }
//        });
    }
}
