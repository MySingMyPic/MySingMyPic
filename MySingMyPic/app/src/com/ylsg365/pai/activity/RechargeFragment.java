package com.ylsg365.pai.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.dummy.DummyContent;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.CommonAdapter;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.StringUtil;
import com.ylsg365.pai.util.ViewHolder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RechargeFragment extends Fragment implements AbsListView.OnItemClickListener, PullToRefreshBase.OnRefreshListener {
    private int type;
    private OnFragmentInteractionListener mListener;
    private PullToRefreshListView mListView;
    private CommonAdapter mAdapter;
    private int page = 0;
    private final int rows = 10;
    private List<Map<String, String>> data;
    private boolean isRefresh = false;

    /**
     * type 1  提现接口   2  充值接口
     *
     * @param type
     * @return
     */
    public static RechargeFragment newInstance(int type) {
        RechargeFragment fragment = new RechargeFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    public RechargeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            type = getArguments().getInt("type");
        }

        data = new ArrayList<Map<String, String>>();
        mAdapter = new CommonAdapter(getActivity(), data, R.layout.recharge_item) {
            @Override
            public void convert(ViewHolder holder, Object item) {
                Map<String, String> mapItem = (Map<String, String>) item;

                if (type == 1) {
                    if (!StringUtil.isNull(mapItem.get("bankname"))) {
                        holder.setText(R.id.text_bankname, mapItem.get("bankname"));
                    }
                } else {
                       holder.setVisibility(R.id.text_state,View.GONE);
                       holder.setVisibility(R.id.text_bankname,View.GONE);
                       holder.setVisibility(R.id.bank,View.GONE);
                }

                holder.setText(R.id.text_money, "￥" + mapItem.get("money"));
                holder.setText(R.id.text_state, mapItem.get("state"));
                holder.setText(R.id.text_time, mapItem.get("time"));
            }

        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list2, container, false);

        mListView = (PullToRefreshListView) view.findViewById(R.id.list);

        mListView.getRefreshableView().setDivider(null);
        mListView.getRefreshableView().setSelector(android.R.color.transparent);
        mListView.setOnRefreshListener(this);
        // set mode to BOTH
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        ILoadingLayout startLabels = mListView.getLoadingLayoutProxy(true, false);
        startLabels.setPullLabel("下拉刷新");
        startLabels.setRefreshingLabel("加载中...");
        startLabels.setReleaseLabel("释放刷新");
        /**
         * 自动加载更多数据
         */
        mListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                isRefresh = false;
                page++;
                initData();
            }
        });
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(this);

        initData();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {

    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String id);
    }

    private void initData() {
        if (type == 1) {  //提现
            YinApi.getCashList(page + "", rows +
                    "", new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (JsonUtil.getBoolean(response, "status")) {
                        if (isRefresh) {
                            mAdapter.clearData();
                        }
                        JSONArray array = JsonUtil.getJSONArray(response, "cashs");
                        data.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject json = JsonUtil.getJSONObject(array, i);
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("bankname", JsonUtil.getString(json, "bankName"));
                            map.put("money", JsonUtil.getString(json, "money"));
                            map.put("state", JsonUtil.getString(json, "payState"));
                            map.put("time", JsonUtil.getString(json, "cTime"));
                            data.add(map);
                        }
                        if (page == 1) {
                            mAdapter.setDatas(data);
                        } else {
                            mAdapter.addData(data);
                        }
                    } else {
                        mListView.setIsLoadMore(false);
                        Toast.makeText(RechargeFragment.this.getActivity(), getString(R.string.no_more_toast), Toast.LENGTH_LONG).show();
                    }
                    mListView.onRefreshComplete();
                    mAdapter.notifyDataSetChanged();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    mListView.onRefreshComplete();
                    mAdapter.notifyDataSetChanged();
                }
            });
        } else {   //充值
            YinApi.getRechargeList(page + "", rows +
                    "", new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (JsonUtil.getBoolean(response, "status")) {
                        if (isRefresh) {
                            mAdapter.clearData();
                        }
                        JSONArray array = JsonUtil.getJSONArray(response, "datas");
                        data.clear();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject json = JsonUtil.getJSONObject(array, i);
                            Map<String, String> map = new HashMap<String, String>();
//                            map.put("bankname", JsonUtil.getString(json, "bankName"));
                            map.put("money", JsonUtil.getString(json, "money"));
                            map.put("state", JsonUtil.getString(json, "orderState"));
                            map.put("time", JsonUtil.getString(json, "cTime"));
                            data.add(map);
                        }
                        if (page == 1) {
                            mAdapter.setDatas(data);
                        } else {
                            mAdapter.addData(data);
                        }
                    } else {
                        mListView.setIsLoadMore(false);
                        Toast.makeText(RechargeFragment.this.getActivity(), getString(R.string.no_more_toast), Toast.LENGTH_LONG).show();
                    }
                    mListView.onRefreshComplete();
                    mAdapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

}
