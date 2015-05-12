package com.ylsg365.pai.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ylsg365.pai.R;
import com.ylsg365.pai.activity.Listener.OnItemClickListener;
import com.ylsg365.pai.activity.Listener.OnMyItemClickListener;
import com.ylsg365.pai.activity.Listener.OnUserHeadClickListener;
import com.ylsg365.pai.app.Constants;
import com.ylsg365.pai.face.FaceUtil;
import com.ylsg365.pai.util.DateUtil;
import com.ylsg365.pai.util.JsonUtil;
import com.ylsg365.pai.web.dic.EnumInfoType;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewInfoAdapter extends RecyclerView.Adapter<NewInfoAdapter.ViewHolder> {
    private List<JSONObject> infoList;
    private int layoutResId;
    private OnItemClickListener itemClickListener;
    private OnUserHeadClickListener onUserHeadClickListener;
    private OnMyItemClickListener mOnMyItemClickListener;
    Context context;
    public NewInfoAdapter(Context context,int layoutResId, List<JSONObject> list) {
        infoList = new ArrayList<JSONObject>(list);
        this.context=context;
        this.layoutResId = layoutResId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);

        ViewHolder holder = new ViewHolder(view);
        holder.setItemClickListener(itemClickListener);
        holder.setOnUserHeadClickListener(onUserHeadClickListener);
        holder.setOnMyItemClickListener(mOnMyItemClickListener);
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
        String str= JsonUtil.getString(infoJsonObject, "ntext");
        if(str!=null&&!str.isEmpty())
            holder.infoContentTextView.setText(FaceUtil.setText(context,str));
        holder.niceCountTextView.setText(JsonUtil.getString(infoJsonObject, "niceCount"));
        holder.commentCountTextView.setText(JsonUtil.getString(infoJsonObject, "commentCount"));
        holder.forwardCountTextView.setText(JsonUtil.getString(infoJsonObject, "forwardCount"));

        if( holder.collectionCountTextView != null){
            holder.collectionCountTextView.setText(JsonUtil.getString(infoJsonObject, "collectionCount"));
        }

        if(JsonUtil.getBoolean(infoJsonObject, "attention")){
            holder.keepImageview.setImageResource(R.drawable.img_keep_p);
        }

        ImageLoader.getInstance().displayImage(Constants.WEB_IMG_DOMIN+JsonUtil.getString(infoJsonObject, "headImg"), holder.userHeadImageview);

    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    /**
     * 设置Item点击监听
     * @param listener
     */
    public void setOnItemClickListener(OnItemClickListener listener){
        this.itemClickListener = listener;
    }
    public void setOnUserHeadClickListener(OnUserHeadClickListener onUserHeadClickListener){
        this.onUserHeadClickListener = onUserHeadClickListener;
    }
    public void setOnMyItemClickListener(OnMyItemClickListener onMyItemClickListener){
        this.mOnMyItemClickListener = onMyItemClickListener;
    }

    public JSONObject getItem(int position){
        return infoList.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View itemView;
        private OnItemClickListener mListener;
        private OnUserHeadClickListener mOnUserHeadClickListener;
        private OnMyItemClickListener mOnMyItemClickListener;

        public ImageView userHeadImageview;
        public ImageView keepImageview;
        public ImageView infoIconImageView;
        public TextView userNickNameTextView;
        public TextView cTimeTextView;
        //内容
        public TextView infoContentTextView;
        public TextView niceCountTextView;
        public TextView commentCountTextView;
        public TextView forwardCountTextView;
        public TextView collectionCountTextView;

        //item下边按钮
        public LinearLayout collectLayout;
        public LinearLayout shareLayout;
        public LinearLayout msgLayout;
        public LinearLayout giftLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            userHeadImageview = (ImageView)itemView.findViewById(R.id.img_user_headImg);
            infoIconImageView = (ImageView)itemView.findViewById(R.id.img_type);
            keepImageview = (ImageView)itemView.findViewById(R.id.img_keep);

            userNickNameTextView = (TextView)itemView.findViewById(R.id.text_user_nickName);
            infoContentTextView = (TextView)itemView.findViewById(R.id.text_content);
            niceCountTextView = (TextView)itemView.findViewById(R.id.text_niceCount);
            commentCountTextView = (TextView)itemView.findViewById(R.id.text_commentCount);
            forwardCountTextView = (TextView)itemView.findViewById(R.id.text_forwardCount);
            collectionCountTextView = (TextView)itemView.findViewById(R.id.text_collectionCount);
            cTimeTextView = (TextView)itemView.findViewById(R.id.text_cTime);

            collectLayout = (LinearLayout) itemView.findViewById(R.id.layout_collect);
            shareLayout = (LinearLayout) itemView.findViewById(R.id.layout_share);
            msgLayout = (LinearLayout) itemView.findViewById(R.id.layout_msg);
            giftLayout = (LinearLayout) itemView.findViewById(R.id.layout_gift);

            itemView.setOnClickListener(this);

            userHeadImageview.setOnClickListener(this);

            if(collectLayout != null){
                collectLayout.setOnClickListener(this);
                shareLayout.setOnClickListener(this);
                msgLayout.setOnClickListener(this);
                giftLayout.setOnClickListener(this);
            }
        }


        public void setOnUserHeadClickListener(OnUserHeadClickListener onUserHeadClickListener) {
            this.mOnUserHeadClickListener = onUserHeadClickListener;
        }
        public void setOnMyItemClickListener(OnMyItemClickListener onMyItemClickListener) {
            this.mOnMyItemClickListener = onMyItemClickListener;
        }
        public void setItemClickListener(OnItemClickListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                if(v.getId() == R.id.img_user_headImg){
                    mOnUserHeadClickListener.onUserHeadClick(v, getPosition());
                }else  if(v.getId() == R.id.layout_collect){
                   mOnMyItemClickListener.OnCollectClick(v, getPosition());
                }else  if(v.getId() == R.id.layout_share){
                    mOnMyItemClickListener.OnShareClick(v, getPosition());
                }else  if(v.getId() == R.id.layout_msg){
                    mOnMyItemClickListener.OnMsgClick(v, getPosition());
                }else  if(v.getId() == R.id.layout_gift){
                    mOnMyItemClickListener.OnGiftClick(v, getPosition());
                }else {
                    mListener.onItemClick(v,getPosition());
                }
            }
        }
    }

    public void addData( List<JSONObject> tempInfoList){
        infoList.addAll(tempInfoList);
    }

    public void clearData(){
        infoList.clear();
    }
}