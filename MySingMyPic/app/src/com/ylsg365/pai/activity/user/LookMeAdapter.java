package com.ylsg365.pai.activity.user;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.util.JsonUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LookMeAdapter extends RecyclerView.Adapter<LookMeAdapter.ViewHolder> {
    private List<JSONObject> infoList;
    private int layoutResId;

    public LookMeAdapter(int layoutResId, List<JSONObject> list) {
        infoList = new ArrayList<JSONObject>(list);
        this.layoutResId = layoutResId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        JSONObject infoJsonObject = infoList.get(position);


        holder.userNickNameTextView.setText(JsonUtil.getString(infoJsonObject, "nickName"));

        ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + JsonUtil.getString(infoJsonObject, "headImg"), holder.userHeadImageview);
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public void clear() {
        infoList.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;

        public ImageView userHeadImageview;
        public TextView userNickNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            userHeadImageview = (ImageView)itemView.findViewById(R.id.img_headImg);
            userNickNameTextView = (TextView)itemView.findViewById(R.id.text_nickName);
        }
    }
    public void addData(List<JSONObject> tempLnfoList){
        infoList.addAll(tempLnfoList);
    }
}