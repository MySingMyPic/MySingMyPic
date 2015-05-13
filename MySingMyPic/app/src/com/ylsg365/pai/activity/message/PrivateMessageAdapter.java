package com.ylsg365.pai.activity.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.app.NavHelper;
import com.ylsg365.pai.util.DateUtil;
import com.ylsg365.pai.util.JsonUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PrivateMessageAdapter extends RecyclerView.Adapter<PrivateMessageAdapter.ViewHolder> {
    private final int ITEM_VIEW_TYPE_0 = 0;
    private final int ITEM_VIEW_TYPE_1 = 1;
    private final int ITEM_VIEW_TYPE_2 = 2;
    private final int ITEM_VIEW_TYPE_DEFAULT = 100;
    private List<JSONObject> infoList;
    private int layoutResId;
    private onRoomItemClickListener onRoomItemClickListener;
    private Context mContext;

    public PrivateMessageAdapter(Context context, int layoutResId, List<JSONObject> list) {
        mContext = context;
        infoList = new ArrayList<JSONObject>(list);
        this.layoutResId = layoutResId;
    }

    private View.OnClickListener onNacClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch ((Integer)v.getTag()){
                case 0:
                    NavHelper.toSystemMessagePage(mContext);
                    break;
                case 1:
                    NavHelper.toMyCommentPage(mContext);
            }
        }
    };

    @Override
    public int getItemViewType(int position) {
        int type = -1;
        switch (position) {
            case 0:
                type = ITEM_VIEW_TYPE_0;
                break;
            case 1:
                type = ITEM_VIEW_TYPE_1;
                break;
            case 2:
                type = ITEM_VIEW_TYPE_2;
                break;


            default:
                type = ITEM_VIEW_TYPE_DEFAULT;
        }
        return type;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        ImageView imageView = null;
        TextView nameTextView = null;
        switch (viewType) {
            case ITEM_VIEW_TYPE_0:
                layoutResId = R.layout.item_message_category;
                view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
                view.setTag(0);
                view.setOnClickListener(onNacClickListener);
                imageView = (ImageView)view.findViewById(R.id.img_icon);
                imageView.setBackgroundResource(R.drawable.icon_notice_big);
                nameTextView = (TextView)view.findViewById(R.id.message_name);
                nameTextView.setText("通知");
                break;
            case ITEM_VIEW_TYPE_1:
                layoutResId = R.layout.item_message_category;
                view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
                view.setTag(1);
                view.setOnClickListener(onNacClickListener);
                imageView = (ImageView)view.findViewById(R.id.img_icon);
                imageView.setBackgroundResource(R.drawable.icon_comment_big);
                nameTextView = (TextView)view.findViewById(R.id.message_name);
                nameTextView.setText("评论");
                break;
            case ITEM_VIEW_TYPE_2:
                layoutResId = R.layout.item_message_category;
                view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
                view.setTag(3);
                view.setOnClickListener(onNacClickListener);
                imageView = (ImageView)view.findViewById(R.id.img_icon);
                imageView.setBackgroundResource(R.drawable.icon_share_big);
                nameTextView = (TextView)view.findViewById(R.id.message_name);
                nameTextView.setText("转发");
                break;


            default:
                layoutResId = R.layout.item_message_normal;
                view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        if(position > 2) {
            JSONObject infoJsonObject = infoList.get(position);


        holder.userNickNamTextView.setText(JsonUtil.getString(infoJsonObject, "sendNickName"));
            holder.messageContentTextView.setText(String.format("%s", JsonUtil.getString(infoJsonObject, "ntext")));
            holder.messageCTimeTextView.setText(String.format("%s", DateUtil.getFriendlyDate(JsonUtil.getString(infoJsonObject, "cTime"))));

            ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN + JsonUtil.getString(infoJsonObject, "sendHeadImg"), holder.userHeadImageview);
        }


    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public Object getItem(int position)
    {
        return infoList.get(position);
    }
    public void clearData() {
        infoList.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;

        public ImageView userHeadImageview;
        public TextView userNickNamTextView;
        public TextView messageContentTextView;
        public TextView messageCTimeTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            userHeadImageview = (ImageView)itemView.findViewById(R.id.img_user_headImg);

            userNickNamTextView = (TextView)itemView.findViewById(R.id.text_user_nickName);
            messageContentTextView = (TextView)itemView.findViewById(R.id.text_content);
            messageCTimeTextView = (TextView)itemView.findViewById(R.id.text_cTime);
        }
    }

    public void setOnRoomItemClickListener(PrivateMessageAdapter.onRoomItemClickListener onRoomItemClickListener) {
        this.onRoomItemClickListener = onRoomItemClickListener;
    }

    public interface onRoomItemClickListener {
        void onRoomItemClick(int position);
    }

    public void addData(List<JSONObject> tempInfoList){
        infoList.addAll(tempInfoList);
    }
}