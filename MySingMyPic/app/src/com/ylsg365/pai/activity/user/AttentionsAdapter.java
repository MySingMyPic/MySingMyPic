package com.ylsg365.pai.activity.user;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.Listener.OnItemClickListener;
import com.ylsg365.pai.activity.Listener.OnViewClickListener;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.util.JsonUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AttentionsAdapter extends RecyclerView.Adapter<AttentionsAdapter.ViewHolder> {
    private List<JSONObject> infoList;
    private int layoutResId;
    private OnItemClickListener onItemClickListener;
    private OnViewClickListener onViewClickListener;
    public AttentionsAdapter(int layoutResId, List<JSONObject> list) {
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
        boolean isAttention = JsonUtil.getBoolean(infoJsonObject, "attention");

        holder.attentionButton.setText(isAttention ? "取消关注" : "关注");

        holder.userNicknameTextView.setText(JsonUtil.getString(infoJsonObject, "nickName"));

        ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + JsonUtil.getString(infoJsonObject, "headImg"), holder.userHeadImageview);
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public JSONObject getItem(int postion){
        return infoList.get(postion);
    }

    public void clear() {
        infoList.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public View itemView;

        public ImageView userHeadImageview;
        public TextView userNicknameTextView;
        public Button attentionButton;

        public ViewHolder(View itemView) {
            super(itemView);

            userHeadImageview = (ImageView)itemView.findViewById(R.id.img_headImg);

            userNicknameTextView = (TextView)itemView.findViewById(R.id.text_nickName);
            attentionButton = (Button)itemView.findViewById(R.id.btn_attention);

            itemView.setOnClickListener(this);
            attentionButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.layout_item:
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, getPosition());
                    }
                case R.id.btn_attention:
                    if (onViewClickListener != null) {
                        onViewClickListener.onViewClick(v, getPosition());
                    }
            }

        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    public void addData( List<JSONObject> tempLabels){
        infoList.addAll(tempLabels);
    }
}