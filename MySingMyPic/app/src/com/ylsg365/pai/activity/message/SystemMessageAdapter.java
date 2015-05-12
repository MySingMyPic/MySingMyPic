package com.ylsg365.pai.activity.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ylsg365.pai.R;
import com.ylsg365.pai.util.DateUtil;
import com.ylsg365.pai.util.JsonUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SystemMessageAdapter extends RecyclerView.Adapter<SystemMessageAdapter.ViewHolder> {
    private List<JSONObject> infoList;
    private int layoutResId;
    private onRoomItemClickListener onRoomItemClickListener;
    public SystemMessageAdapter(int layoutResId, List<JSONObject> list) {
        this.infoList = list;
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


        holder.messageContentTextView.setText(JsonUtil.getString(infoJsonObject, "ntext"));
//        holder.messageTitleTextView.setText(String.format("%s", JsonUtil.getString(infoJsonObject, "title")));
        holder.messageCTimeTextView.setText(String.format("%s", DateUtil.getFriendlyDate(JsonUtil.getString(infoJsonObject, "cTime"))));

    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public void clear() {
        infoList.clear();
    }

    public void addData(ArrayList<JSONObject> tempInfoList) {
        tempInfoList.addAll(tempInfoList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;

        public TextView messageContentTextView;
        public TextView messageTitleTextView;
        public TextView messageCTimeTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            messageContentTextView = (TextView)itemView.findViewById(R.id.text_content);
            messageTitleTextView = (TextView)itemView.findViewById(R.id.text_title);
            messageCTimeTextView = (TextView)itemView.findViewById(R.id.text_cTime);
        }
    }

    public void setOnRoomItemClickListener(SystemMessageAdapter.onRoomItemClickListener onRoomItemClickListener) {
        this.onRoomItemClickListener = onRoomItemClickListener;
    }

    interface onRoomItemClickListener {
        void onRoomItemClick();
    }
}