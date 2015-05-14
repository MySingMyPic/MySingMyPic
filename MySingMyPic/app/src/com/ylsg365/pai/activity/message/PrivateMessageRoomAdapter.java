package com.ylsg365.pai.activity.message;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.face.FaceUtil;
import com.ylsg365.pai.util.JsonUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ann on 2015-05-13.
 */
public class PrivateMessageRoomAdapter extends BaseAdapter{

    private final int ITEM_VIEW_TYPE_0 = 0;
    private final int ITEM_VIEW_TYPE_1 = 1;

    private List<JSONObject> infoList;
    private List<JSONObject> reverseList;
    private int layoutResId;
    private onRoomItemClickListener onRoomItemClickListener;
    private Context mContext;
    private int userId=-1;
    private LayoutInflater mInflater;
    public PrivateMessageRoomAdapter(Context context,int userId, List<JSONObject> list) {
        mContext = context;
        this.userId=userId;
        Log.e("","USERiD:"+userId);
        mInflater = LayoutInflater.from(context);
        infoList = new ArrayList<JSONObject>(list);
        reverseList=new ArrayList<JSONObject>();
    }


    @Override
    public int getItemViewType(int position) {
        int type = 0;
        JSONObject item=reverseList.get(position);
        int id=JsonUtil.getInt(item,"sendUserId");
        Log.e("","sendUseId"+id);
        if(id==userId)
            type=ITEM_VIEW_TYPE_0;
        else type=ITEM_VIEW_TYPE_1;


        return type;
    }


    public void clearData() {
        reverseList.clear();
        infoList.clear();
    }


    public void setOnRoomItemClickListener(PrivateMessageRoomAdapter.onRoomItemClickListener onRoomItemClickListener) {
        this.onRoomItemClickListener = onRoomItemClickListener;
    }

    public interface onRoomItemClickListener {
        void onRoomItemClick(int position);
    }

    public void addData(List<JSONObject> tempInfoList){
        infoList.addAll(tempInfoList);
        reverseList.clear();
        reverseList.addAll(infoList);
        Collections.reverse(reverseList);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return reverseList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return reverseList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }



    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    ViewHolder1 holder1;
    ViewHolder2 holder2;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        int type = getItemViewType(position);
        View convertView1,convertView2;

        switch(type)
        {
            case ITEM_VIEW_TYPE_0:
                convertView1 = convertView;
                if(convertView1==null){
                    convertView1 = mInflater.inflate(R.layout.private_msg_come_layout,
                            null);
                    holder1 = new ViewHolder1();
                    holder1.userHead = (ImageView)convertView1.findViewById(R.id.imageView);

                    holder1. message = (TextView)convertView1.findViewById(R.id.message);
                    convertView1.setTag(holder1);
                }
                else{
                    holder1=(ViewHolder1) convertView1.getTag();
                }
                convertView=convertView1;
                break;
            case ITEM_VIEW_TYPE_1:
                convertView2 = convertView;
                if(convertView2==null){
                    convertView2 = mInflater.inflate(R.layout.private_msg_to_layout,
                            null);
                    holder2 = new ViewHolder2();
                    holder2.userHead = (ImageView)convertView2.findViewById(R.id.imageView);

                    holder2. message = (TextView)convertView2.findViewById(R.id.message);
                    convertView2.setTag(holder2);
                }
                else{
                    holder2=(ViewHolder2) convertView2.getTag();
                }
                convertView=convertView2;
                break;
        }


        switch(type)
        {
            case ITEM_VIEW_TYPE_0:
                holder1 = (ViewHolder1) convertView.getTag();
                break;
            case ITEM_VIEW_TYPE_1:
                holder2 = (ViewHolder2) convertView.getTag();
                break;
        }

        switch(type)
        {
            case ITEM_VIEW_TYPE_0:

                JSONObject infoJsonObject = reverseList.get(position);

                holder1.message.setText(FaceUtil.setText(mContext,JsonUtil.getString(infoJsonObject,"ntext")));
                ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + JsonUtil.getString(infoJsonObject, "sendHeadImg"), holder1.userHead);
                break;
            case ITEM_VIEW_TYPE_1:

                JSONObject infoJsonObject2 = reverseList.get(position);

                holder2.message.setText(FaceUtil.setText(mContext,JsonUtil.getString(infoJsonObject2,"ntext")));
                ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + JsonUtil.getString(infoJsonObject2, "sendHeadImg"), holder2.userHead);

                break;
        }
        return convertView;
    }

    //come layout
    private class ViewHolder1 {
        public ImageView userHead;
        public TextView message;

    }

    //to layout
    private class ViewHolder2 {
        public ImageView userHead;
        public TextView message;
    }

}
