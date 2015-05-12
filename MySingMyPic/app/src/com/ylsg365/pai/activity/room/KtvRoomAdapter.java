package com.ylsg365.pai.activity.room;

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

public class KtvRoomAdapter extends RecyclerView.Adapter<KtvRoomAdapter.ViewHolder> {
    private List<JSONObject> infoList;
    private int layoutResId;
    private onRoomItemClickListener onRoomItemClickListener;
    public KtvRoomAdapter(int layoutResId, List<JSONObject> list) {
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


        holder.roomNameTextView.setText(JsonUtil.getString(infoJsonObject, "nname"));
        holder.roomAuthorTextView.setText(String.format("%s", JsonUtil.getString(infoJsonObject, "nickName")));
        holder.roomhotTextView.setText(String.format("%s", JsonUtil.getString(infoJsonObject, "singCount")));
        holder.roomUserCountTextView.setText(String.format("%s个", JsonUtil.getString(infoJsonObject, "houseCount")));

        ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + JsonUtil.getString(infoJsonObject, "imgUrl"), holder.roomImageview);
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public View itemView;

        public ImageView roomImageview;
        public TextView roomNameTextView;
        public TextView roomAuthorTextView;
        public TextView roomhotTextView;
        public TextView roomUserCountTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            roomImageview = (ImageView)itemView.findViewById(R.id.img_roomImg);

            roomAuthorTextView = (TextView)itemView.findViewById(R.id.text_room_author);
            roomNameTextView = (TextView)itemView.findViewById(R.id.text_room_name);
            roomhotTextView = (TextView)itemView.findViewById(R.id.text_room_hot);
            roomUserCountTextView = (TextView)itemView.findViewById(R.id.text_room_user_count);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onRoomItemClickListener != null) {
                onRoomItemClickListener.onRoomItemClick();
            }
        }
    }

    public void setOnRoomItemClickListener(KtvRoomAdapter.onRoomItemClickListener onRoomItemClickListener) {
        this.onRoomItemClickListener = onRoomItemClickListener;
    }

    interface onRoomItemClickListener {
        void onRoomItemClick();
    }
}