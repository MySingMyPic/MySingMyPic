package com.ylsg365.pai.activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.util.DateUtil;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.web.dic.EnumInfoType;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OriginalAdapter extends RecyclerView.Adapter<OriginalAdapter.ViewHolder> {
    private List<JSONObject> infoList;
    private int layoutResId;
    private OnItemClickListener onItemClickListener;

    public OriginalAdapter(int layoutResId, List<JSONObject> list) {
        infoList = new ArrayList<JSONObject>(list);
        this.layoutResId = layoutResId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.setOnItemClickListener(onItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        JSONObject infoJsonObject = infoList.get(position);

        EnumInfoType infoType = EnumInfoType.getEnumInfoType(JsonUtil.getInt(infoJsonObject, "ntype"));
        int infoTypeResId = 0;
        switch (infoType){
            case RECORD:
                infoTypeResId = R.drawable.icon_info_type_record;
                break;
            case CAPPELLA:
                infoTypeResId = R.drawable.icon_info_type_record;
                break;
            case VIDEO:
                infoTypeResId = R.drawable.icon_info_type_video;
                break;
            case MV:
                infoTypeResId = R.drawable.icon_info_type_video;
                break;
            case NEWS:
                infoTypeResId = R.drawable.icon_info_type_news;
                break;

            default:
                infoTypeResId = R.drawable.icon_info_type_news;
        }

        holder.infoIconImageView.setImageResource(infoTypeResId);
        holder.userNickNameTextView.setText(JsonUtil.getString(infoJsonObject, "nickName"));
        holder.cTimeTextView.setText(DateUtil.getFriendlyDate(JsonUtil.getString(infoJsonObject, "cTime")));
        holder.infoContentTextView.setText(String.format("我刚刚录了一首%s", JsonUtil.getString(infoJsonObject, "songName")));
        holder.niceCountTextView.setText(JsonUtil.getString(infoJsonObject, "niceCount"));
        holder.commentCountTextView.setText(JsonUtil.getString(infoJsonObject, "commentCount"));
        holder.forwardCountTextView.setText(JsonUtil.getString(infoJsonObject, "forwardCount"));
        holder.priceTextView.setText(String.format("$%s", JsonUtil.getString(infoJsonObject, "price")));

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
        private OnItemClickListener onItemClickListener;

        public ImageView userHeadImageview;
        public ImageView infoIconImageView;
        public TextView userNickNameTextView;
        public TextView songNameTextView;
        public TextView cTimeTextView;
        //内容
        public TextView infoContentTextView;
        public TextView niceCountTextView;
        public TextView commentCountTextView;
        public TextView forwardCountTextView;
        public TextView priceTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            userHeadImageview = (ImageView)itemView.findViewById(R.id.img_user_headImg);
            infoIconImageView = (ImageView)itemView.findViewById(R.id.img_type);

            userNickNameTextView = (TextView)itemView.findViewById(R.id.text_nickName);
            songNameTextView = (TextView)itemView.findViewById(R.id.text_nickName);
            infoContentTextView = (TextView)itemView.findViewById(R.id.text_content);
            niceCountTextView = (TextView)itemView.findViewById(R.id.text_niceCount);
            commentCountTextView = (TextView)itemView.findViewById(R.id.text_commentCount);
            forwardCountTextView = (TextView)itemView.findViewById(R.id.text_forwardCount);
            cTimeTextView = (TextView)itemView.findViewById(R.id.text_cTime);
            priceTextView = (TextView)itemView.findViewById(R.id.text_price);

            priceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onClick(getPosition());
                    }
                }
            });

        }
        public void setOnItemClickListener(OnItemClickListener listener){
            onItemClickListener =listener;
        }
    }
    public void addData( List<JSONObject> tempInfoList){
        infoList.addAll(tempInfoList);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        onItemClickListener= mOnItemClickListener ;
    }

    public interface OnItemClickListener{
        public abstract void onClick(int position);
    }
}