package com.ylsg365.pai.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.util.JsonUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ann on 2015-05-15.
 */
public class GameUserListAdapter extends BaseAdapter {

    private List<JSONObject> infoList=new ArrayList<JSONObject>();
    private Context mContext;
    private LayoutInflater mInflater;
    public GameUserListAdapter(Context context,List<JSONObject> list)
    {
        mContext = context;
        infoList=list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return infoList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return infoList.get(arg0);
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

    ViewHolder holder;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if(convertView==null)
        {
            convertView = mInflater.inflate(R.layout.game_user_item_layout,
                    null);
            holder=new ViewHolder();
            holder.icon=(ImageView)convertView.findViewById(R.id.icon);
            holder.userHead=(ImageView)convertView.findViewById(R.id.userhead);
            holder.message=(TextView)convertView.findViewById(R.id.username);
            convertView.setTag(holder);
        }
        else holder=(ViewHolder)convertView.getTag();

        JSONObject item=infoList.get(position);
        ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + JsonUtil.getString(item, "headImg"), holder.userHead);
        holder.message.setText(JsonUtil.getString(item,"nname"));
        int rank=JsonUtil.getInt(item,"ranking");
        switch (rank)
        {
            case 1:
                holder.icon.setVisibility(View.VISIBLE);
                holder.icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.img_cup_1));
                break;
            case 2:
                holder.icon.setVisibility(View.VISIBLE);
                holder.icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.img_cup_2));
                break;
            case 3:
                holder.icon.setVisibility(View.VISIBLE);
                holder.icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.img_cup_3));
                break;
            default:
                holder.icon.setVisibility(View.GONE);
                break;
        }
        return convertView;
    }

    //come layout
    private class ViewHolder {
        public ImageView userHead,icon;
        public TextView message;

    }

    public void refresh(List<JSONObject> list)
    {
        infoList.addAll(list);
        this.notifyDataSetChanged();
    }
}
