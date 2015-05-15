package com.ylsg365.pai.activity.room;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.GameUserListAdapter;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.dialog.MyDialog;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.UIHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.DateUtil;
import com.ylsg365.pai.util.DensityUtil;
import com.ylsg365.pai.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 比赛信息界面
 */
public class GameInfoActivity extends BaseActivity {
    private ImageView game_img;
    private TextView text_game_time;
    private Button joinGame;
    private ListView listView;
    private List<JSONObject> infoList=new ArrayList<JSONObject>();
    private int page=0;
    private int row=10;
    GameUserListAdapter adapter;
    public static View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_info);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        setupToolbar();
        setTitle("比赛信息");
        TextView leftTextView = (TextView) findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadView();
        getGameInfo();
        getGameUsers();
    }

    /**
     * 获取比赛详情信息
     */
    private void getGameInfo() {
        YinApi.getGameCenterInfo(getIntent().getStringExtra("nid"), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (JsonUtil.getBoolean(response, "status")) {

                    String startDate, endDate;
                    startDate = JsonUtil.getString(response, "startDate");
                    endDate = JsonUtil.getString(response, "endDate");
                    startDate = DateUtil.format(DateUtil.parseDate(startDate), "MM.dd");
                    endDate = DateUtil.format(DateUtil.parseDate(endDate), "MM.dd");


                    ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + JsonUtil.getString(response, "imagerUrl"), game_img);
                    text_game_time.setText(startDate + "--" + endDate);
                    setTitle(JsonUtil.getString(response, "nname"));

                } else {
                    String msg = JsonUtil.getString(response, "msg");
                    UIHelper.showToast(msg);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
    }

    public void getGameUsers()
    {
        YinApi.getGameUsers(getIntent().getStringExtra("nid"),page,row,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("gameuser", ":"+response.toString());
                if (JsonUtil.getBoolean(response, "status"))
                {
                    if(JsonUtil.getInt(response,"rowStart")<0)
                    {
                        NavHelper.showToast(GameInfoActivity.this,JsonUtil.getString(response,"msg"));
                    }
                    else{
                    infoList.clear();
                     JSONArray array=JsonUtil.getJSONArray(response,"users");
                     for(int i=0;i<array.length();i++)
                     {
                         JSONObject item=JsonUtil.getJSONObject(array,i);
                         infoList.add(item);
                     }
                     if(adapter==null)
                     {
                         adapter=new GameUserListAdapter(GameInfoActivity.this,infoList);
                         listView.setAdapter(adapter);

                     }
                    else
                     {
                         adapter.refresh(infoList);
                     }
                    setListViewHeight(listView);
                    }
                }
                else
                {

                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }
    private void loadView() {
        game_img = (ImageView) findViewById(R.id.game_img);
        text_game_time = (TextView) findViewById(R.id.text_game_time);
        joinGame = (Button) findViewById(R.id.btn_register_next);
        listView=(ListView)findViewById(R.id.listView);
        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 录制作品dialog
                 */
                final MyDialog dialog = new MyDialog(GameInfoActivity.this);
                dialog.tv_content.setHint("请输入您的联系方式");
                dialog.tv_content.setInputType(InputType.TYPE_CLASS_NUMBER);
                dialog.tv_content2.setHint("请输入您的姓名");
                dialog.tv_content2.setVisibility(View.VISIBLE);
                dialog.tv_bad.setVisibility(View.GONE);
                dialog.tv_ok.setText("开始录制");
                dialog.setOnOkClickListener(new MyDialog.OnOkClickListener() {
                    @Override
                    public void onPositiveclick() {
                        String phone = dialog.getStr();
                        String name = dialog.getStr2();
                    }

                    @Override
                    public void onNegativeclick() {

                    }
                });
            }
        });
    }

    public void setListViewHeight(ListView listView) {

        int totalHeight = 0;
        for (int i = 0; i < listView.getCount(); i++) {

            totalHeight += DensityUtil.dip2px(this,65);
        }

        totalHeight+=listView.getDividerHeight()*(listView.getCount());
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;

        listView.setLayoutParams(params);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static  class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_game_info, container, false);

            view = rootView;
            return rootView;
        }
    }
}
