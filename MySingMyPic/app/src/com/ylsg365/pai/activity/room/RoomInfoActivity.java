package com.ylsg365.pai.activity.room;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.base.BaseActivity;
import com.ylsg365.pai.activity.dialog.MyDialog;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.app.YinApi;
import com.ylsg365.pai.util.FileUtils;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 房间信息页面
 */
public class RoomInfoActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private MyDialog dialog;
    private ImageView roomImg;             //房间头像
    private ImageView room_owner;           //房主
    public static View view;
    private TextView room_name;            //房间名字
    private TextView roomid;           //房间id
    private LinearLayout priority_sing;     //优先唱歌
    private LinearLayout harem_master;     //群主
    private ToggleButton auto_switch;      //120s自动切button
    private TextView remote_music;          //点歌
    private TextView in_room;    //进入房间
    private String filepath;
    private Bitmap mBitmap;
    private TextView room_notice;          //房间公告

    private String roomNameStr;  //包房名字
    private String roomImgStr;  //包房图片链接
    private String roomNoticeStr;          //房间公告Str
    private String isAuto_Switch;     //自动切麦
    private String inRoomStr;         //进入房间
    private String SingStr;           //点唱
    private String pwdStr;               //进入房间密码
    private String room_ownerStr;               //进入房间密码
    private List<String> zhuboIds = new ArrayList<String>();        //主播id
    private List<String> zhuboHeads= new ArrayList<String>();        //主播id
    private List<String> managerIds= new ArrayList<String>();        //管理员id
    private List<String> managerHeads= new ArrayList<String>();        //管理员id
    private String roomNameStr_Old;  //包房名字
    private String roomImgStr_Old;  //包房图片链接
    private String roomNoticeStr_Old;          //房间公告Str
    private String isAuto_Switch_Old;     //自动切麦
    private String inRoomStr_Old;         //进入房间
    private String SingStr_Old;           //点唱
//    private String pwd_Old ;               //进入房间密码

    private RelativeLayout in_room_layout;  //进入房间view
    private RelativeLayout remote_music_layout;  //点歌view
    private RelativeLayout roomImg_layout; //房间头像view
    private RelativeLayout room_name_layout;   //房间名字 view
    private RelativeLayout room_notice_layout;  //房间公告view

    private ListView list;
    private RelativeLayout listLayout;
    String[] data = new String[]{"任何人", "仅关注", "密码"};
    String[] data2 = new String[]{"任何人", "仅关注"};
    @SuppressWarnings("rawtypes")
    ArrayAdapter adapter;
    private JSONObject json;

    private boolean IsOwner = false;  //标记是否房主

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
        setTitle("房间信息");
        TextView leftTextView = (TextView) findViewById(R.id.text_toolbar_left);
        leftTextView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadViews();
        initData();
    }

    protected void loadViews() {
        if (view != null) {

            roomImg = (ImageView) view.findViewById(R.id.room_img);
            room_name = (TextView) findViewById(R.id.room_name);
            roomid = (TextView) findViewById(R.id.room_id);
            room_notice = (TextView) findViewById(R.id.edit_verify_code);
            room_owner = (ImageView) findViewById(R.id.room_owner);
            priority_sing = (LinearLayout) findViewById(R.id.priority_sing);
            harem_master = (LinearLayout) findViewById(R.id.harem_master);
            auto_switch = (ToggleButton) findViewById(R.id.auto_switch);
            remote_music = (TextView) findViewById(R.id.remote_music);
            remote_music_layout = (RelativeLayout) findViewById(R.id.remote_music_layout);
            in_room = (TextView) findViewById(R.id.in_room);
            in_room_layout = (RelativeLayout) findViewById(R.id.in_room_layout);
            room_name_layout = (RelativeLayout) findViewById(R.id.layout_edit_password);
            room_notice_layout = (RelativeLayout) findViewById(R.id.layout_edit_verify_code);

            list = (ListView) findViewById(R.id.list);
            listLayout = (RelativeLayout) findViewById(R.id.listLayout);

        }
    }

    /**
     * 进入房间权限  0  点唱 1
     *
     * @param type
     */
    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    private void initList(final int type) {

        if (type == 0) {
            adapter = new ArrayAdapter(RoomInfoActivity.this, R.layout.list_item, data);
        } else {
            adapter = new ArrayAdapter(RoomInfoActivity.this, R.layout.list_item, data2);
        }

        list.setAdapter(adapter);
        list.setVisibility(View.VISIBLE);
        listLayout.setVisibility(View.VISIBLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 2) {
                    alertModifyDialog("", "pwd");
                }
                if (type == 0) {
                    inRoomStr = getInRoomStr(position + "");
                    in_room.setText(inRoomStr);
                } else {
                    SingStr = getInRoomStr(position + "");
                    remote_music.setText(SingStr);
                }
                list.setVisibility(View.GONE);
                listLayout.setVisibility(View.GONE);

            }
        });
    }

    /**
     * 设置控件事件监听
     *
     * @param listener
     * @param checkListener
     */
    protected void setupListeners(View.OnClickListener listener, CompoundButton.OnCheckedChangeListener checkListener) {

        roomImg.setOnClickListener(listener);
        room_notice.setOnClickListener(listener);
        auto_switch.setOnCheckedChangeListener(checkListener);
        remote_music_layout.setOnClickListener(listener);
        in_room_layout.setOnClickListener(listener);
        room_name.setOnClickListener(listener);
        priority_sing.setOnClickListener(listener);
        harem_master.setOnClickListener(listener);


    }

    private void initData() {
        IsOwner = getIntent().getBooleanExtra("IsOwner", false);
        if (!IsOwner) {  //判断是否房主
            setViewColor();
            setupListeners(null, null);
        } else {
            setupListeners(this, this);
        }
        /**
         * 获取房屋详情
         */
        YinApi.getHouseDetail(getIntent().getStringExtra("nid"), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    json = new JSONObject(response);


                    roomImgStr_Old = JsonUtil.getString(json, "imgUrl");
                    roomImgStr = roomImgStr_Old;
                    roomNameStr_Old = JsonUtil.getString(json, "nname");
                    roomNameStr = roomNameStr_Old;
                    roomNoticeStr_Old = JsonUtil.getString(json, "notice");
                    roomNoticeStr = roomNoticeStr_Old;
                    isAuto_Switch_Old = JsonUtil.getString(json, "autoQiemai");
                    isAuto_Switch = isAuto_Switch_Old;
                    SingStr_Old = "1".equals(JsonUtil.getString(json, "singAuth")) ? "仅关注" : "任何人";
                    SingStr = SingStr_Old;
                    inRoomStr_Old = getInRoomStr(JsonUtil.getString(json, "accessAuth"));
                    inRoomStr = inRoomStr_Old;
                    room_ownerStr = JsonUtil.getString(json, "headImg");

                    ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN +roomImgStr_Old, roomImg);
                    room_name.setText(roomNameStr_Old);
                    roomid.setText(JsonUtil.getString(json, "houseNo"));
                    room_notice.setText(roomNoticeStr_Old);
                    auto_switch.setChecked("1".equals(isAuto_Switch_Old) ? true : false);
                    remote_music.setText(SingStr_Old);
                    in_room.setText(inRoomStr_Old);

                    ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN +room_ownerStr, room_owner);

                    JSONArray manager = JsonUtil.getJSONArray(json, "houseManage");

                    for (int i = 0; i < manager.length(); i++) {
                        JSONObject obj = JsonUtil.getJSONObject(manager, i);
                        if ("1".equals(JsonUtil.getString(obj, "type"))) {   //主播
                            zhuboIds.add(JsonUtil.getString(obj, "userId"));
                            zhuboHeads.add(JsonUtil.getString(obj, "headImg"));
                        } else {
                            managerIds.add(JsonUtil.getString(obj, "userId"));
                            managerHeads.add(JsonUtil.getString(obj, "headImg"));
                        }
                    }
                    int j = 2;
                    for (int i = 0;i <managerIds.size(); i++) {
                        if (i > 2) {
                            return;
                        }
                        ImageView img = (ImageView) harem_master.getChildAt(j);
                        ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN +managerHeads.get(i), img);
                        img.setVisibility(View.VISIBLE);
                        j--;
                    }
                    j = 2;
                    for (int i =0;i< zhuboIds.size(); i++) {
                        if (i > 2) {
                            return;
                        }
                        ImageView img = (ImageView) priority_sing.getChildAt(j);
                        ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN +zhuboHeads.get(i), img);
                        img.setVisibility(View.VISIBLE);
                        j--;
                    }
                } catch (JSONException e) {

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.room_img:

                filepath = FileUtils.path + FileUtils.roomHead;
                if (!new File(FileUtils.path).exists()) {
                    new File(FileUtils.path).mkdirs();
                    try {
                        new File(filepath).createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                NavHelper.toPicCutActivityForResule(RoomInfoActivity.this, filepath);

                break;
            case R.id.edit_verify_code:
                alertModifyDialog(room_notice.getText().toString(), "notice");
                break;
            case R.id.remote_music_layout:  //点歌
                initList(1);
                break;
            case R.id.in_room_layout:  //进入房间
                initList(0);
                break;
            case R.id.priority_sing:  //优先唱歌

                break;
            case R.id.harem_master:  //群主管理

                break;
            case R.id.room_name:
                alertModifyDialog(room_name.getText().toString(), "name");
                break;


        }
    }

    /**
     * 是否自动切麦点击事件
     *
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        isAuto_Switch = isChecked ? "1" : "0";
    }

    /**
     * 项目背景颜色
     */
    public void setViewColor() {
        in_room_layout.setBackgroundColor(getResources().getColor(R.color.editbac));
        room_notice_layout.setBackgroundColor(getResources().getColor(R.color.editbac));
        remote_music_layout.setBackgroundColor(getResources().getColor(R.color.editbac));
        roomImg_layout.setBackgroundColor(getResources().getColor(R.color.editbac));
        room_name_layout.setBackgroundColor(getResources().getColor(R.color.editbac));
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_room_info, container, false);
            view = rootView;
            return rootView;
        }
    }

    /**
     * 修改房间名和公告 dialog
     *
     * @param str
     * @param type
     */
    private void alertModifyDialog(String str, final String type) {

        dialog = new MyDialog(RoomInfoActivity.this);
        dialog.setStr( str,"");
        dialog.setOnOkClickListener(new MyDialog.OnOkClickListener() {
            @Override
            public void onPositiveclick() {
                String t = dialog.getStr();
                if (!StringUtil.isNull(t)) {
                    if ("name".equals(type)) {
                        roomNameStr = t;
                        room_name.setText(roomNameStr);
                    } else if ("notice".equals(type)) {
                        roomNoticeStr = t;
                        room_notice.setText(roomNoticeStr);
                    } else {
                        pwdStr = t;

                    }
                }
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }

            @Override
            public void onNegativeclick() {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            }
        });
    }

    /**
     * 调用更新房间
     */
    private void houseUpdate() {
        if (!roomNameStr_Old.equals(roomNameStr)
                || !roomImgStr_Old.equals(roomImgStr)
                || !roomNoticeStr_Old.equals(roomNoticeStr)
                || !isAuto_Switch_Old.equals(isAuto_Switch)
                || !inRoomStr_Old.equals(inRoomStr)
                || !SingStr_Old.equals(SingStr)
                ) {    //(old 为空  new 不为空 ) ||  (old  !eq new )  执行更新房间信息
            StringBuffer tempZB = new StringBuffer();
            StringBuffer tempGL = new StringBuffer();
            String zbIds = "",glIds= "";
            for (String str : zhuboIds) {
                tempZB.append(str);
                tempZB.append(",");
            }
            if (tempZB.toString().endsWith(",")) {
                zbIds=  tempZB.toString().substring(0,tempZB.toString().length() - 1);
            }
            for (String str : managerIds) {
                tempGL.append(str);
                tempGL.append(",");
            }
            if (tempGL.toString().endsWith(",")) {
                glIds=  tempGL.toString().substring(0,tempGL.toString().length() - 1);
            }

            YinApi.houseUpdate(getIntent().getStringExtra("nid"), roomNameStr, getInRoomInt(inRoomStr), pwdStr, getInRoomInt(SingStr), roomImgStr, JsonUtil.getString(json, "houseNo"), roomNoticeStr, zbIds, glIds, isAuto_Switch, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    if (JsonUtil.getBoolean(response, "status")) {
                        Toast.makeText(RoomInfoActivity.this, "信息修改成功!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    /**
     * 进入房间
     *
     * @param str
     * @return
     */
    private String getInRoomStr(String str) {
        String result = "任何人";
        if ("0".equals(str)) {
            result = "任何人";
        } else if ("1".equals(str)) {
            result = "仅关注";
        } else if ("2".equals(str)) {
            result = "密码";
        }
        return result;
    }

    private String getInRoomInt(String str) {
        String result = "0";
        if ("任何人".equals(str)) {
            result = "0";
        } else if ("仅关注".equals(str)) {
            result = "1";
        } else if ("密码".equals(str)) {
            result = "2";
        }
        return result;
    }

    /**
     * 更改房间头像
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NavHelper.REQUEST_GO_TO_PICCUT) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                mBitmap = BitmapFactory.decodeFile(filepath, options);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            if (mBitmap != null) {
                roomImg.setImageBitmap(mBitmap);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        File file = new File(filepath);
                        Map<String, File> files = new HashMap<String, File>();
                        files.put(file.getName(), file);
                        String url = Constants.WEB_SERVER_DOMAIN + "fileController/imgUpload" ;

                        try {
                            String str = YinApi.imgUplode(url, new HashMap<String, String>(), files);
                            if (!StringUtil.isNull(str)) {
                                JSONObject json = new JSONObject(str);
                                if (JsonUtil.getBoolean(json, "status")) {

                                    JSONArray array = JsonUtil.getJSONArray(json, "fileName");
                                    if (array.length() > 0) {
                                        str = FileUtils.host + array.get(0);
                                        roomImgStr = str;
                                    }

                                }
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

            }

        }

    }

    @Override
    public void finish() {
        houseUpdate();
        super.finish();
    }
}
